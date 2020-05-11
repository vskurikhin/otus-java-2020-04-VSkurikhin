package su.svn.hiload.socialnetwork.dao.r2dbc.impl;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Result;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.dao.r2dbc.UserProfileCustomDao;
import su.svn.hiload.socialnetwork.model.security.UserProfile;
import su.svn.hiload.socialnetwork.utils.ClosingConsumer;

import java.util.Objects;

@Repository("userProfileR2dbcDao")
public class UserProfileR2dbcDao implements UserProfileCustomDao {

    private final ConnectionFactory connectionFactory;

    private final static String FIND_BY_LOGIN = "SELECT " +
            " id, login, hash, expired, locked " +
            " FROM user_profile " +
            " WHERE login = ? ";

    private final static String FIND_ALL = "SELECT " +
            " id, login, hash, expired, locked " +
            " FROM user_profile ";

    private final static String CREATE = "INSERT " +
            " INTO user_profile " +
            " (login, hash, expired, locked)" +
            " VALUES " +
            " (?, ?, ?, ?) ";

    public UserProfileR2dbcDao(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public Mono<Integer> create(UserProfile userProfile) {
        Flux<Result> resultsFlux = Mono.from(connectionFactory.create())
                .flatMapMany(connection -> executeCreate(userProfile, connection));
        return resultsFlux
                .flatMap(Result::getRowsUpdated)
                .next()
                .switchIfEmpty(Mono.just(-1));
    }

    private Flux<? extends Result> executeCreate(UserProfile userProfile, Connection connection) {
        return Flux.from(connection.createStatement(CREATE)
                .bind(0, userProfile.getLogin())
                .bind(1, userProfile.getHash())
                .bind(2, userProfile.isExpired())
                .bind(3, userProfile.isLocked())
                .execute())
                .doOnSubscribe(new ClosingConsumer(connection));
    }

    @Override
    public Mono<UserProfile> readFirstByLogin(String login) {
        Flux<Result> resultsFlux = Mono.from(connectionFactory.create())
                .flatMapMany(connection -> executeFindByLogin(login, connection));
        return liftMapResultToUserProfile(resultsFlux)
                .next()
                .switchIfEmpty(Mono.empty());
    }

    private Flux<? extends Result> executeFindByLogin(String login, Connection connection) {
        return Flux.from(connection.createStatement(FIND_BY_LOGIN)
                .bind(0, login)
                .execute())
                .doOnSubscribe(new ClosingConsumer(connection));
    }

    @Override
    public Flux<UserProfile> readAll() {
        Flux<Result> resultsFlux = Mono.from(connectionFactory.create())
                .flatMapMany(connection -> Flux.from(connection.createStatement(FIND_ALL)
                .execute())
                .doOnSubscribe(new ClosingConsumer(connection)));
        return liftMapResultToUserProfile(resultsFlux);
    }

    private Flux<UserProfile> liftMapResultToUserProfile(Flux<Result> resultsFlux) {
        return resultsFlux.flatMap(result -> result.map((row, rowMetadata) -> {
            Long id = row.get("id", Long.class);
            String login = row.get("login", String.class);
            String hash = row.get("hash", String.class);
            Integer expired = row.get("expired", Integer.class);
            Integer locked = row.get("locked", Integer.class);
            Objects.requireNonNull(id);

            return new UserProfile(
                    id, login, hash,
                    expired != null && expired > 0,
                    locked != null && locked != 0);
        }));
    }
}
