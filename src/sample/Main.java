package sample;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.service.elevation.ElevationResult;
import com.lynden.gmapsfx.service.elevation.ElevationServiceCallback;
import com.lynden.gmapsfx.service.elevation.ElevationStatus;
import com.lynden.gmapsfx.service.geocoding.GeocoderStatus;
import com.lynden.gmapsfx.service.geocoding.GeocodingResult;
import com.lynden.gmapsfx.service.geocoding.GeocodingServiceCallback;
import com.lynden.gmapsfx.shapes.Circle;
import com.lynden.gmapsfx.shapes.CircleOptions;
import com.lynden.gmapsfx.shapes.Polyline;
import com.lynden.gmapsfx.shapes.PolylineOptions;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sample.communication.*;
import sample.utils.PriceEnum;
import sample.utils.RoomEnum;

public class Main extends Application implements MapComponentInitializedListener,
        ElevationServiceCallback, GeocodingServiceCallback, Callback<GeneralRespose> {

    static final String BASE_URL = "http://127.0.0.1:5000/";
    static final String colors[] = {"red", "green", "blue", "orange", "yellow", "purple", "black", "white", "pink"};

    private BorderPane borderPane;
    private StackPane centerStackPane;
    private HBox containerHBox;
    private VBox checkboxesContainerVBox;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private ToggleGroup weekToggleGroup;
    private RadioButton defaultRadioButton, weekendRadioButton, weekdayRadioButton;
    private ToggleButton startToggleButton, endToggleButton, daysGroupToggleButton, subgraphsToggleButton, markersToggleButton, linesToggleButton;
    private ChoiceBox<String> weekdayChoiceBox, priceTypeChoiceBox, roomTypeChoiceBox;
    private Button analizeButton;
    private TextArea logTextArea;
    private ProgressIndicator progressIndicator;

    protected GoogleMapView mapComponent;
    protected GoogleMap map;

    private ArrayList<ArrayList<MapShape>> nodes = new ArrayList<ArrayList<MapShape>>();
    private ArrayList<ArrayList<MapShape>> edges = new ArrayList<ArrayList<MapShape>>();

    private ServerAPI serverAPI;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void start(final Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        stage.setTitle("TASS - temat 10");
        Scene mainScene = new Scene(root, 760, 760);

        setupViews(mainScene);
        setupListeners();

        stage.setScene(mainScene);
        stage.show();
    }

    private void setupViews(Scene scene) {
        borderPane = (BorderPane) scene.lookup("#BorderPane");
        centerStackPane = (StackPane) scene.lookup("#centerStackPane");
        containerHBox = (HBox) scene.lookup("#containerHBox");
        checkboxesContainerVBox = (VBox) scene.lookup("#checkboxesContainerVBox");

        mapComponent = new GoogleMapView(Locale.getDefault().getLanguage(), null);
        mapComponent.addMapInializedListener(this);
        centerStackPane.getChildren().remove(containerHBox);
        centerStackPane.getChildren().addAll(mapComponent, containerHBox);

        startToggleButton = (ToggleButton) scene.lookup("#startToggleButton");
        endToggleButton = (ToggleButton) scene.lookup("#endToggleButton");

        LocalDate localDate = LocalDate.parse("2014-01-01", formatter);
        startDatePicker = (DatePicker) scene.lookup("#startDatePicker");
        startDatePicker.setConverter(new StringConverter<LocalDate>() {

            @Override
            public String toString(LocalDate localDate) {
                if(localDate == null)
                    return "";
                return formatter.format(localDate);
            }

            @Override
            public LocalDate fromString(String dateString) {
                if(dateString == null || dateString.trim().isEmpty()) {
                    return null;
                }
                return LocalDate.parse(dateString, formatter);
            }
        });
        startDatePicker.setValue(localDate);
        endDatePicker = (DatePicker) scene.lookup("#endDatePicker");
        endDatePicker.setConverter(new StringConverter<LocalDate>() {

            @Override
            public String toString(LocalDate localDate) {
                if(localDate == null)
                    return "";
                return formatter.format(localDate);
            }

            @Override
            public LocalDate fromString(String dateString) {
                if(dateString == null || dateString.trim().isEmpty()) {
                    return null;
                }
                return LocalDate.parse(dateString, formatter);
            }
        });
        endDatePicker.setValue(localDate);

        weekToggleGroup = new ToggleGroup();
        defaultRadioButton = (RadioButton) scene.lookup("#defaultRadioButton");
        weekendRadioButton = (RadioButton) scene.lookup("#weekendRadioButton");
        weekdayRadioButton = (RadioButton) scene.lookup("#weekdayRadioButton");
        defaultRadioButton.setToggleGroup(weekToggleGroup);
        weekendRadioButton.setToggleGroup(weekToggleGroup);
        weekdayRadioButton.setToggleGroup(weekToggleGroup);

        daysGroupToggleButton = (ToggleButton) scene.lookup("#daysGroupToggleButton");

        weekdayChoiceBox = (ChoiceBox<String>) scene.lookup("#dayOfWeekChoiceBox");
        weekdayChoiceBox.setItems(FXCollections.observableArrayList(
                "poniedziałek", "wtorek ", "środa", "czwartek", "piątek", "sobota", "niedziela")
        );
        weekdayChoiceBox.getSelectionModel().selectFirst();

        roomTypeChoiceBox = (ChoiceBox<String>) scene.lookup("#roomTypeChoiceBox");
        roomTypeChoiceBox.setItems(FXCollections.observableArrayList(
                "<brak preferencji>", "inne ", "pojedyńcze", "podwójne")
        );
        roomTypeChoiceBox.getSelectionModel().selectFirst();

        priceTypeChoiceBox = (ChoiceBox<String>) scene.lookup("#roomPriceChoiceBox");
        priceTypeChoiceBox.setItems(FXCollections.observableArrayList(
                "<brak preferencji>", "niska ", "średnia", "wysoka")
        );
        priceTypeChoiceBox.getSelectionModel().selectFirst();

        subgraphsToggleButton = (ToggleButton) scene.lookup("#subgraphsToggleButton");
        markersToggleButton = (ToggleButton) scene.lookup("#markersToggleButton");
        linesToggleButton = (ToggleButton) scene.lookup("#linesToggleButton");

        analizeButton = (Button) scene.lookup("#analizeButton");
        analizeButton.setDisable(true);

        logTextArea = (TextArea) scene.lookup("#logTextArea");

        progressIndicator = new ProgressIndicator();
    }

    private void setupListeners() {

        weekToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
            @Override
            public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
                if (weekToggleGroup.getSelectedToggle() != null) {
                    RadioButton radioButton = (RadioButton) weekToggleGroup.getSelectedToggle();
                    if (radioButton == weekdayRadioButton) {
                        weekdayChoiceBox.setDisable(false);
                    } else {
                        weekdayChoiceBox.setDisable(true);
                    }

                    if (radioButton == weekendRadioButton) {
                        daysGroupToggleButton.setDisable(false);
                    } else {
                        daysGroupToggleButton.setDisable(true);
                    }
                }
            }
        });

        startToggleButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (startToggleButton.isSelected()) {
                    startDatePicker.setDisable(false);
                } else {
                    startDatePicker.setDisable(true);
                }
            }
        });

        endToggleButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (endToggleButton.isSelected()) {
                    endDatePicker.setDisable(false);
                } else {
                    endDatePicker.setDisable(true);
                }
            }
        });

        daysGroupToggleButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (daysGroupToggleButton.isSelected()) {
                    daysGroupToggleButton.setText("Tylko weekend");
                } else {
                    daysGroupToggleButton.setText("Bez weekendu");
                }
            }
        });

        subgraphsToggleButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                containerHBox.setVisible(subgraphsToggleButton.isSelected());
            }
        });

        markersToggleButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (markersToggleButton.isSelected()) {
                    markersToggleButton.setText("Pokaż znaczniki");
                } else {
                    markersToggleButton.setText("Ukryj znaczniki");
                }
                for (ArrayList<MapShape> subgraphs : nodes) {
                    for (MapShape node : subgraphs) {
                        node.setVisible(!markersToggleButton.isSelected());
                    }
                }
            }
        });

        linesToggleButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (linesToggleButton.isSelected()) {
                    linesToggleButton.setText("Pokaż linie");
                } else {
                    linesToggleButton.setText("Ukryj linie");
                }
                for (ArrayList<MapShape> subgraphs : edges) {
                    for (MapShape edge : subgraphs) {
                        edge.setVisible(!linesToggleButton.isSelected());
                    }
                }
            }
        });


        analizeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                makeCall();
            }
        });
    }

    private void setupAPIClient() {
       Gson gson = new GsonBuilder()
               .serializeNulls()
               .setLenient()
               .create();

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(120, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();

        serverAPI = retrofit.create(ServerAPI.class);
    }

    private void makeCall() {
        centerStackPane.getChildren().add(progressIndicator);
        Call<GeneralRespose> call = serverAPI.analizeData(makeAnalizeRequestFromControls());
        call.enqueue(this);
    }

    private AnalizeRequest makeAnalizeRequestFromControls() {

        String date_beg = null;
        Integer day_of_week = null;
        String price_level = null;
        String room_type = null;
        String date_end = null;
        Boolean is_weekend = null;

        if(startToggleButton.isSelected()) {
            LocalDate localDate = startDatePicker.getValue();
            date_beg = formatter.format(localDate);
        }

        RadioButton radioButton = (RadioButton) weekToggleGroup.getSelectedToggle();
        if (radioButton == weekendRadioButton) {
            is_weekend = daysGroupToggleButton.isSelected();
        }

        if (radioButton == weekdayRadioButton) {
            day_of_week = weekdayChoiceBox.getSelectionModel().getSelectedIndex();
        }

        switch (roomTypeChoiceBox.getSelectionModel().getSelectedIndex()) {
            case 0: {
                room_type = null;
                break;
            }
            case 1: {
                room_type = RoomEnum.OTHER.getValue();
                break;
            }
            case 2: {
                room_type = RoomEnum.SINGLE.getValue();
                break;
            }
            case 3: {
                room_type = RoomEnum.DOUBLE.getValue();
                break;
            }
        }

        switch (priceTypeChoiceBox.getSelectionModel().getSelectedIndex()) {
            case 0: {
                price_level = null;
                break;
            }
            case 1: {
                price_level = PriceEnum.SMALL.getValue();
                break;
            }
            case 2: {
                price_level = PriceEnum.MEDIUM.getValue();
                break;
            }
            case 3: {
                price_level = PriceEnum.HIGHT.getValue();
                break;
            }
        }

        if(endToggleButton.isSelected()) {
            LocalDate localDate = endDatePicker.getValue();
            date_end = formatter.format(localDate);
        }

        AnalizeRequest request = new AnalizeRequest(date_beg, day_of_week, price_level, room_type, date_end, is_weekend);
        return request;
    }

    @Override
    public void mapInitialized() {
        //Once the map has been loaded by the Webview, initialize the map details.
        LatLong wroclaw = new LatLong(51.123207,  17.025898);
        mapComponent.addMapReadyListener(() -> {
            setupAPIClient();
            analizeButton.setDisable(false);
        });

        MapOptions options = new MapOptions();
        options.center(wroclaw)
                .mapMarker(true)
                .zoom(11)
                .overviewMapControl(false)
                .panControl(false)
                .rotateControl(false)
                .scaleControl(false)
                .streetViewControl(false)
                .zoomControl(false)
                .mapType(MapTypeIdEnum.TERRAIN)
                .styleString("[{'featureType':'landscape','stylers':[{'saturation':-100},{'lightness':65},{'visibility':'on'}]},{'featureType':'poi','stylers':[{'saturation':-100},{'lightness':51},{'visibility':'simplified'}]},{'featureType':'road.highway','stylers':[{'saturation':-100},{'visibility':'simplified'}]},{\"featureType\":\"road.arterial\",\"stylers\":[{\"saturation\":-100},{\"lightness\":30},{\"visibility\":\"on\"}]},{\"featureType\":\"road.local\",\"stylers\":[{\"saturation\":-100},{\"lightness\":40},{\"visibility\":\"on\"}]},{\"featureType\":\"transit\",\"stylers\":[{\"saturation\":-100},{\"visibility\":\"simplified\"}]},{\"featureType\":\"administrative.province\",\"stylers\":[{\"visibility\":\"off\"}]},{\"featureType\":\"water\",\"elementType\":\"labels\",\"stylers\":[{\"visibility\":\"on\"},{\"lightness\":-25},{\"saturation\":-100}]},{\"featureType\":\"water\",\"elementType\":\"geometry\",\"stylers\":[{\"hue\":\"#ffff00\"},{\"lightness\":-25},{\"saturation\":-97}]}]");

        //[{\"featureType\":\"landscape\",\"stylers\":[{\"saturation\":-100},{\"lightness\":65},{\"visibility\":\"on\"}]},{\"featureType\":\"poi\",\"stylers\":[{\"saturation\":-100},{\"lightness\":51},{\"visibility\":\"simplified\"}]},{\"featureType\":\"road.highway\",\"stylers\":[{\"saturation\":-100},{\"visibility\":\"simplified\"}]},{\"featureType\":\"road.arterial\",\"stylers\":[{\"saturation\":-100},{\"lightness\":30},{\"visibility\":\"on\"}]},{\"featureType\":\"road.local\",\"stylers\":[{\"saturation\":-100},{\"lightness\":40},{\"visibility\":\"on\"}]},{\"featureType\":\"transit\",\"stylers\":[{\"saturation\":-100},{\"visibility\":\"simplified\"}]},{\"featureType\":\"administrative.province\",\"stylers\":[{\"visibility\":\"off\"}]},{\"featureType\":\"water\",\"elementType\":\"labels\",\"stylers\":[{\"visibility\":\"on\"},{\"lightness\":-25},{\"saturation\":-100}]},{\"featureType\":\"water\",\"elementType\":\"geometry\",\"stylers\":[{\"hue\":\"#ffff00\"},{\"lightness\":-25},{\"saturation\":-97}]}]
        map = mapComponent.createMap(options,false);
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void elevationsReceived(ElevationResult[] results, ElevationStatus status) {
        if(status.equals(ElevationStatus.OK)){
            for(ElevationResult e : results){
                System.out.println(" Elevation on "+ e.getLocation().toString() + " is " + e.getElevation());
            }
        }
    }

    @Override
    public void geocodedResultsReceived(GeocodingResult[] results, GeocoderStatus status) {
        if(status.equals(GeocoderStatus.OK)){
            for(GeocodingResult e : results){
                System.out.println(e.getVariableName());
                System.out.println("GEOCODE: " + e.getFormattedAddress() + "\n" + e.toString());
            }
        }
    }

    @Override
    public void onResponse(Call<GeneralRespose> call, Response<GeneralRespose> response) {

        if (response.code() == 200) {
            if(response.body().isCompute() == true) {
                GeneralRespose gr = response.body();
                setupLog(gr);
                drawGraphOnMap(gr);
            } else {
                logTextArea.setText("Dla zadanych parametrów nie udało się utworzyć grafu");
            }
        } else if (response.code() == 500) {
            logTextArea.setText("Wystąpił błąd na serwerze.");
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                centerStackPane.getChildren().remove(progressIndicator);
            }
        });
    }

    @Override
    public void onFailure(Call<GeneralRespose> call, Throwable throwable) {
        logTextArea.setText(throwable.toString());
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                centerStackPane.getChildren().remove(progressIndicator);
            }
        });
    }

    private void setupLog(GeneralRespose gr) {
        logTextArea.clear();
        String subgraphsInfo = "";

        if (gr.getSubgraphs() != null) {
            for (SubgraphResponse subgraph : gr.getSubgraphs()) {
                subgraphsInfo += "\n\n\nWspółczynniki podgrafu " + subgraph.getColorId() + ": \n"
                        + "\nLiczba węzłów: " + subgraph.getInfo().getNumberOfNodes()
                        + "\nLiczba krawędzi: " + subgraph.getInfo().getNumberOfEdges()
                        + "\nSredni stopień węzła: " + subgraph.getInfo().getAverageNodeDegree()
                        + "\nPrzechodność: " + subgraph.getInfo().getTransitivity()
                        + "\nAsortatywność: " + subgraph.getInfo().getAssortativityCoefficient()
                        + "\nWspółczynnik gronowania: " + gr.getInfo().getAverageClustering()
                        + "\nKorelacja Pearsona: " + subgraph.getInfo().getPearsonCorelation()
                        + "\nWęzłowy współczynnik korelacji Pearsona: " + subgraph.getInfo().getPearsoonCorrelationCoefficient();
            }
        }

        logTextArea.setText(
                "Współczynniki wyznaczonego grafu: \n"
                        + "\nLiczba węzłów: " + gr.getInfo().getNumberOfNodes()
                        + "\nLiczba krawędzi: " + gr.getInfo().getNumberOfEdges()
                        + "\nSredni stopień węzła: " + gr.getInfo().getAverageNodeDegree()
                        + "\nPrzechodność: " + gr.getInfo().getTransitivity()
                        + "\nAsortatywność: " + gr.getInfo().getAssortativityCoefficient()
                        + "\nWspółczynnik gronowania: " + gr.getInfo().getAverageClustering()
                        + "\nKorelacja Pearsona: " + gr.getInfo().getPearsonCorelation()
                        + "\nWęzłowy współczynnik korelacji Pearsona: " + gr.getInfo().getPearsoonCorrelationCoefficient()
                        + subgraphsInfo
        );
    }

    private void drawGraphOnMap(GeneralRespose respose) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                clearMap();

                if (respose.getSubgraphs() == null) {
                    ArrayList<MapShape> subgraphNodes = new ArrayList<>();
                    for (HotelResponse node : respose.getHotels()) {
                        setMarker(new LatLong(node.getLatitude(), node.getLongitude()), "red", subgraphNodes);
                    }
                    nodes.add(subgraphNodes);

                    ArrayList<MapShape> subgraphEdges = new ArrayList<>();
                    for (EdgeResponse edge : respose.getEdges()) {
                        LatLong start = new LatLong(0, 0);
                        LatLong end = new LatLong(0, 0);
                        for (HotelResponse node : respose.getHotels()) {
                            if (edge.getBegin() == node.getId()) {
                                start = new LatLong(node.getLatitude(), node.getLongitude());
                                break;
                            }
                        }

                        for (HotelResponse node : respose.getHotels()) {
                            if (edge.getEnd() == node.getId()) {
                                end = new LatLong(node.getLatitude(), node.getLongitude());
                                break;
                            }
                        }
                        setLine(start, end, "green", subgraphEdges);
                    }
                    edges.add(subgraphEdges);
                } else {
                    int index = 0;
                    for (SubgraphResponse subrgraph : respose.getSubgraphs()) {

                        ArrayList<MapShape> subgraphNodes = new ArrayList<>();
                        for (HotelResponse node : respose.getHotels()) {
                            for (int i : subrgraph.getNodes()) {
                                if (i == node.getId()) {
                                    setMarker(new LatLong(node.getLatitude(), node.getLongitude()), colors[index], subgraphNodes);
                                }
                            }
                        }
                        nodes.add(subgraphNodes);

                        ArrayList<MapShape> subgraphEdges = new ArrayList<>();
                        for (EdgeResponse edge : respose.getEdges()) {
                            for (int i : subrgraph.getNodes()) {
                                if (i == edge.getBegin() || i == edge.getEnd()) {
                                    LatLong start = new LatLong(0, 0);
                                    LatLong end = new LatLong(0, 0);
                                    for (HotelResponse node : respose.getHotels()) {
                                        if (edge.getBegin() == node.getId()) {
                                            start = new LatLong(node.getLatitude(), node.getLongitude());
                                            break;
                                        }
                                    }

                                    for (HotelResponse node : respose.getHotels()) {
                                        if (edge.getEnd() == node.getId()) {
                                            end = new LatLong(node.getLatitude(), node.getLongitude());
                                            break;
                                        }
                                    }
                                    setLine(start, end, colors[index], subgraphEdges);
                                }
                            }
                        }
                        edges.add(subgraphEdges);

                        index += 1;
                    }
                    setupSubgraphsSideMenu(respose.getSubgraphs());
                }
            }
        });
    }

    private void setMarker(LatLong location, String color, ArrayList<MapShape> group) {
        CircleOptions cOpts = new CircleOptions()
                .center(location)
                .radius(20)
                .strokeColor("black")
                .strokeWeight(1)
                .fillColor(color)
                .fillOpacity(1);

        Circle c = new Circle(cOpts);
        c.setVisible(!markersToggleButton.isSelected());
        group.add(c);
        map.addMapShape(c);
    }

    private void setLine(LatLong start, LatLong end, String color, ArrayList<MapShape> group) {
        LatLong[] ary = new LatLong[]{start, end};
        MVCArray mvc = new MVCArray(ary);

        PolylineOptions polyOpts = new PolylineOptions()
                .path(mvc)
                .strokeColor(color)
                .strokeWeight(1);

        Polyline poly = new Polyline(polyOpts);
        poly.setVisible(!linesToggleButton.isSelected());
        group.add(poly);
        map.addMapShape(poly);
    }

    private void setupSubgraphsSideMenu(ArrayList<SubgraphResponse> subgraphs) {
        checkboxesContainerVBox.getChildren().removeAll(checkboxesContainerVBox.getChildren());
        for (int i = 0; i < subgraphs.size(); i++) {
            final ArrayList<MapShape> localNodes = nodes.get(i);
            final ArrayList<MapShape> localEdges = edges.get(i);
            CheckBox checkBox = new CheckBox();
            checkBox.setText("Podgraf " + i);
            checkBox.setSelected(true);
            checkBox.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    for (MapShape node : localNodes) {
                        node.setVisible(checkBox.isSelected());
                    }
                    for (MapShape edge : localEdges) {
                        edge.setVisible(checkBox.isSelected());
                    }
                }
            });

            checkboxesContainerVBox.getChildren().add(checkBox);
        }
    }

    private void clearMap() {
        for (ArrayList<MapShape> subgraphs : nodes) {
            for (MapShape node : subgraphs) {
                map.removeMapShape(node);
            }
        }
        for (ArrayList<MapShape> subgraphs : edges) {
            for (MapShape edge : subgraphs) {
                map.removeMapShape(edge);
            }
        }
        nodes.clear();
        edges.clear();
    }
}

