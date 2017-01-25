package sample.communication;

import com.google.gson.annotations.SerializedName;

/**
 * Created by tobia on 14.01.2017.
 */
public class EdgeResponse {
    @SerializedName("begin") int begin;
    @SerializedName("end") int end;
    @SerializedName("weight") double weight;

    public int getBegin() {
        return begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
