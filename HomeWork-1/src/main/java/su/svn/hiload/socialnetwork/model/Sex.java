package su.svn.hiload.socialnetwork.model;

public enum Sex {
    MALE("male", "M"),
    FEMALE("female", "F");

    private final String displayValue;

    private final String value;

    private Sex(String displayValue, String sex) {
        this.displayValue = displayValue;
        this.value = sex;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public String getValue() {
        return value;
    }

    public static Sex findByDisplayValue(String displayValue) {
        for (Sex v : values()) {
            if (v.displayValue.equals(displayValue)) {
                return v;
            }
        }
        return null;
    }

    public static Sex findByValue(String value) {
        for (Sex v : values()) {
            if (v.value.equals(value)) {
                return v;
            }
        }
        return null;
    }
}
