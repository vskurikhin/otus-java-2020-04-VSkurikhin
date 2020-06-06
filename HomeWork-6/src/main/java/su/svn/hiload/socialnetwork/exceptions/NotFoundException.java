package su.svn.hiload.socialnetwork.exceptions;

public class NotFoundException extends RuntimeException {
    private NotFoundException(String message) {
        super(message);
    }

    public static RuntimeException make() {
        return new NotFoundException("Not Found!");
    }

    public static void throwNow(String message) {
        throw new NotFoundException(message);
    }
}
