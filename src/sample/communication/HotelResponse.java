package sample.communication;

import com.google.gson.annotations.SerializedName;

/**
 * Created by tobia on 14.01.2017.
 */
public class HotelResponse {
    @SerializedName("hotelId") int id;
    @SerializedName("latutude") double latitude;
    @SerializedName("longitude") double longitude;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
