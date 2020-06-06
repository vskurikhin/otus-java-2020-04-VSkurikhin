package su.svn.hiload.socialnetwork.exceptions;

public class BufferLimitException extends RuntimeException {
    private BufferLimitException(String message) {
        super(message);
    }

    public static BufferLimitException make(String message) {
        return new BufferLimitException(message);
    }

    public static void throwNow(String message) {
        throw new BufferLimitException(message);
    }
}
