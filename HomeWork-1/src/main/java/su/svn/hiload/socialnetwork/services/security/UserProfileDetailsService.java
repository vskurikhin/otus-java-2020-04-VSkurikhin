package su.svn.hiload.socialnetwork.services.security;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.dao.UserProfileDao;
import su.svn.hiload.socialnetwork.model.security.UserProfileDetails;

import java.time.Duration;

@Service
public class UserProfileDetailsService implements ReactiveUserDetailsService {

    private final UserProfileDao userProfileDao;

    public UserProfileDetailsService(UserProfileDao userProfileDao) {
        this.userProfileDao = userProfileDao;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userProfileDao.readFirstByLogin(username)
                .timeout(Duration.ofMinutes(2), Mono.empty())
                .flatMap(profile -> Mono.just(new UserProfileDetails(profile)));
    }
}
