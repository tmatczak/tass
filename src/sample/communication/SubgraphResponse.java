package sample.communication;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by tobia on 15.01.2017.
 */
public class SubgraphResponse {
    @SerializedName("color") int colorId;
    @SerializedName("subgraph_info") InfoResponse info;
    @SerializedName("nodes") ArrayList<Integer> nodes;

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }

    public InfoResponse getInfo() {
        return info;
    }

    public void setInfo(InfoResponse info) {
        this.info = info;
    }

    public ArrayList<Integer> getNodes() {
        return nodes;
    }

    public void setNodes(ArrayList<Integer> nodes) {
        this.nodes = nodes;
    }
}
