package su.svn.hiload.socialnetwork.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import su.svn.hiload.socialnetwork.dao.*;

@Service
public class ReactiveService {

    private BCryptPasswordEncoder encoder;

    private final int bufferSize;

    private final int duration;

    private final int limit;

    // private final UserProfileDao userProfileDao;


    public ReactiveService(
            @Value("${application.security.strength}") int strength,
            @Value("${application.reactive.buffer-size}") int bufferSize,
            @Value("${application.reactive.duration}") int duration,
            @Value("${application.reactive.limit}") int limit /*,
            UserProfileDao userProfileDao */) {
        this.encoder = new BCryptPasswordEncoder(strength);
        this.bufferSize = bufferSize;
        this.duration = duration;
        this.limit = limit;
        // this.userProfileDao = userProfileDao;
    }
}
