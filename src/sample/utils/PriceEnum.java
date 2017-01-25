package sample.utils;

/**
 * Created by tobia on 14.01.2017.
 */
public enum PriceEnum {
    SMALL ("SMALL"),
    MEDIUM ("MEDIUM"),
    HIGHT ("HIGHT");

    private final String value;

    PriceEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
