package su.svn.hiload.socialnetwork.dao.r2dbc;

import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Result;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.model.security.UserProfile;

import java.util.Objects;

@Repository("userProfileR2dbcDao")
public class UserProfileR2dbcDao implements UserProfileCustomDao {

    private final ConnectionFactory connectionFactory;

    private final DatabaseClient databaseClient;

    private final static String FIND_BY_LOGIN = "SELECT id, login, hash, expired, locked FROM user_profile WHERE login = ?";

    private final static String FIND_ALL = "SELECT id, login, hash, expired, locked FROM user_profile";

    private final static String CREATE = "INSERT INTO user_profile (login, hash, expired, locked) VALUES (?, ?, ?, ?)";

    public UserProfileR2dbcDao(ConnectionFactory connectionFactory, DatabaseClient databaseClient) {
        this.connectionFactory = connectionFactory;
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<Integer> create(UserProfile userProfile) {
        Flux<Result> resultsFlux = Mono.from(connectionFactory.create())
                .flatMapMany(connection -> connection.createStatement(CREATE)
                        .bind(0, userProfile.getLogin())
                        .bind(1, userProfile.getHash())
                        .bind(2, userProfile.isExpired())
                        .bind(3, userProfile.isLocked())
                        .execute());
        return resultsFlux
                .flatMap(result -> result.map((row, rowMetadata) -> row.get(0, Integer.class)))
                .next();
    }

    @Override
    public Mono<UserProfile> readFirstByLogin(String login) {
        Flux<Result> resultsFlux = Mono.from(connectionFactory.create())
                .flatMapMany(connection -> connection.createStatement(FIND_BY_LOGIN)
                .bind(0, login)
                .execute());
        return liftMapResultToUserProfile(resultsFlux).next();
    }

    @Override
    public Flux<UserProfile> readAll() {
        Flux<Result> resultsFlux = Mono.from(connectionFactory.create())
                .flatMapMany(connection -> connection.createStatement(FIND_ALL)
                .execute());
        return liftMapResultToUserProfile(resultsFlux);
    }

    private Flux<UserProfile> liftMapResultToUserProfile(Flux<Result> resultsFlux) {
        return resultsFlux.flatMap(result -> result.map((row, rowMetadata) -> {
            Long id = row.get("id", Long.class);
            String login = row.get("login", String.class);
            String hash = row.get("hash", String.class);
            Boolean expired = row.get("expired", Boolean.class);
            Boolean locked = row.get("locked", Boolean.class);
            Objects.requireNonNull(id);
            return new UserProfile(id, login, hash, expired, locked);
        }));
    }
}
