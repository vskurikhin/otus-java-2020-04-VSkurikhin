package su.svn.hiload.socialnetwork.model;

public enum Sex {
    MALE("male"),
    FEMALE("female");

    private final String displayValue;

    private Sex(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
