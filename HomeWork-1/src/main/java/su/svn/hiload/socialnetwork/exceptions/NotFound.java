package su.svn.hiload.socialnetwork.exceptions;

public class NotFound extends RuntimeException {
    private NotFound(String message) {
        super(message);
    }

    public static RuntimeException is() {
        return new NotFound("Not Found!");
    }
}
