package sample.communication;

/**
 * Created by tobia on 14.01.2017.
 */

public class AnalizeRequest {
    String date_beg;
    Integer day_of_week;
    String price_level;
    String room_type;
    String date_end;
    Boolean is_weekend;

    public AnalizeRequest(String date_beg, Integer day_of_week, String price_level, String room_type, String date_end, Boolean is_weekend) {
        this.date_beg = date_beg;
        this.day_of_week = day_of_week;
        this.price_level = price_level;
        this.room_type = room_type;
        this.date_end = date_end;
        this.is_weekend = is_weekend;
    }
}
