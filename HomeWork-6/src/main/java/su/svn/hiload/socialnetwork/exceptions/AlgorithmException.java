package su.svn.hiload.socialnetwork.exceptions;

public class AlgorithmException extends RuntimeException {
    private AlgorithmException(String message) {
        super(message);
    }

    public static AlgorithmException make(String message) {
        return new AlgorithmException(message);
    }

    public static void throwNow(String message) {
        throw new AlgorithmException(message);
    }
}
