package su.svn.hiload.socialnetwork.dao.r2dbc;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.model.security.UserProfile;

public interface UserProfileDao {

    Mono<Integer> create(UserProfile userProfile);

    Mono<UserProfile> readLogin(String login);

    Flux<UserProfile> readAll();
}
