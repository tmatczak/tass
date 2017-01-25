package sample.communication;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by tobia on 14.01.2017.
 */
public class GeneralRespose {

    @SerializedName("isCompute") boolean isCompute;
    @SerializedName("hotel_positions") ArrayList<HotelResponse> hotels;
    @SerializedName("edges") ArrayList<EdgeResponse> edges;
    @SerializedName("graph_info") InfoResponse info;
    @SerializedName("sub_graphs") ArrayList<SubgraphResponse> subgraphs;

    public boolean isCompute() {
        return isCompute;
    }

    public void setCompute(boolean compute) {
        isCompute = compute;
    }

    public ArrayList<HotelResponse> getHotels() {
        return hotels;
    }

    public void setHotels(ArrayList<HotelResponse> hotels) {
        this.hotels = hotels;
    }

    public ArrayList<EdgeResponse> getEdges() {
        return edges;
    }

    public void setEdges(ArrayList<EdgeResponse> edges) {
        this.edges = edges;
    }

    public InfoResponse getInfo() {
        return info;
    }

    public void setInfo(InfoResponse info) {
        this.info = info;
    }

    public ArrayList<SubgraphResponse> getSubgraphs() {
        return subgraphs;
    }

    public void setSubgraphs(ArrayList<SubgraphResponse> subgraphs) {
        this.subgraphs = subgraphs;
    }
}
