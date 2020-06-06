package su.svn.hiload.socialnetwork.consistenthash;

import java.security.NoSuchAlgorithmException;

public interface HashFunction {

    /**
     * Hash String to long value
     */
    long hashFunction(String key);
}
