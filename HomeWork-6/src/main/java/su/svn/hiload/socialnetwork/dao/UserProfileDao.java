package su.svn.hiload.socialnetwork.dao;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.dao.r2dbc.UserProfileCustomDao;
import su.svn.hiload.socialnetwork.model.security.UserProfile;

public interface UserProfileDao extends ReactiveCrudRepository<UserProfile, Long>, UserProfileCustomDao {

    @Query("SELECT * FROM user_profile WHERE login = ?")
    Mono<UserProfile> findFirstByLogin(String login);

    @Query("SELECT * FROM user_profile WHERE id > ? LIMIT 1")
    Mono<UserProfile> findFirstOverId(long id);

    @Query("SELECT * FROM user_profile")
    Flux<UserProfile> findAll();
}
