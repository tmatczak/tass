package sample.communication;

/**
 * Created by tobia on 14.01.2017.
 */
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ServerAPI {

    @POST("data")
    Call<GeneralRespose> analizeData(@Body AnalizeRequest analizeRequest);
}
