package sample.utils;

/**
 * Created by tobia on 14.01.2017.
 */
public enum RoomEnum {
    OTHER ("Other"),
    SINGLE ("Single"),
    DOUBLE ("Double");

    private final String value;

    RoomEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
