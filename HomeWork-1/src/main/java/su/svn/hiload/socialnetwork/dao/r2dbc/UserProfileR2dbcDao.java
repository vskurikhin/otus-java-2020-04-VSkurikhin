package su.svn.hiload.socialnetwork.dao.r2dbc;

import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.model.security.UserProfile;

import java.util.regex.Pattern;

@Repository("userProfileR2dbcDao")
public class UserProfileR2dbcDao implements UserProfileDao {

    private final DatabaseClient databaseClient;

    private final static String FIND_BY_LOGIN = "SELECT id, login, hash, expired, locked FROM user_profile WHERE login = '%s'";

    private final static String FIND_ALL = "SELECT id, login, hash, expired, locked FROM user_profile";

    private final static String CREATE = "INSERT INTO user_profile (login, hash, expired, locked) VALUES ($1, $2, $3, $4)";

    private final static Pattern pattern = Pattern.compile("\\w+");

    public UserProfileR2dbcDao(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<Integer> create(UserProfile userProfile) {
        return databaseClient.execute(CREATE)
                .bind("$1", userProfile.getLogin())
                .bind("$2", userProfile.getHash())
                .bind("$3", userProfile.isExpired())
                .bind("$4", userProfile.isLocked())
                .fetch()
                .rowsUpdated();
    }

    @Override
    public Mono<UserProfile> readLogin(String login) {
        if (pattern.matcher(login).matches()) {
            return databaseClient.execute(String.format(FIND_BY_LOGIN, login))
                    .as(UserProfile.class)
                    .fetch().first();
        } else
            return Mono.empty();
    }

    @Override
    public Flux<UserProfile> readAll() {
        return databaseClient.execute(FIND_ALL)
                .as(UserProfile.class)
                .fetch().all();
    }
}
