package su.svn.hiload.socialnetwork.dao.r2dbc;

import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.model.security.UserProfile;

@Repository("userProfileR2dbcDao")
public class UserProfileR2dbcDao implements UserProfileDao {

    private final DatabaseClient databaseClient;

    private final static String FIND_BY_LOGIN = "SELECT id, login, hash, expired, locked FROM user_profile WHERE login = $1";

    private final static String FIND_ALL = "SELECT id, login, hash, expired, locked FROM user_profile";

    public UserProfileR2dbcDao(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<UserProfile> findByLogin(String login) {
        return databaseClient.execute(FIND_BY_LOGIN)
                .bind("$1", login)
                .as(UserProfile.class)
                .fetch().first();
    }

    @Override
    public Flux<UserProfile> findAll() {
        return databaseClient.execute(FIND_ALL)
                .as(UserProfile.class)
                .fetch().all();
    }
}
