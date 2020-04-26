package su.svn.hiload.socialnetwork.services.security;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.dao.UserProfileDao;
import su.svn.hiload.socialnetwork.exceptions.NotFound;
import su.svn.hiload.socialnetwork.model.security.UserProfileDetails;

@Service
public class UserProfileDetailsService implements ReactiveUserDetailsService {

    private final UserProfileDao userProfileDao;

    public UserProfileDetailsService(UserProfileDao userProfileDao) {
        this.userProfileDao = userProfileDao;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return Mono.just(new UserProfileDetails(userProfileDao.findByLogin(username).orElseThrow(NotFound::is)));
    }
}
