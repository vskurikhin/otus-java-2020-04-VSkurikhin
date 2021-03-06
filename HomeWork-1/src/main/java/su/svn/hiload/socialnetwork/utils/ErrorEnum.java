package su.svn.hiload.socialnetwork.utils;

public enum ErrorEnum {
    OK(0, "Success"),
    E1(1, "Redirect to index"),
    E11(11, "Binding result errors"),
    E12(12, "Can't create user profile"),
    E13(13, "Empty result or timeout duration"),
    E14(14, "Got an empty count when create the record in the database."),
    E15(15, "Got an empty count when update the record in the database."),
    E99(99, "Empty result or timeout duration.");

    private int code;
    private String message;

    ErrorEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
