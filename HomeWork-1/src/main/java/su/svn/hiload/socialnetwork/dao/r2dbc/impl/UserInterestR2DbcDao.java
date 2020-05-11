package su.svn.hiload.socialnetwork.dao.r2dbc.impl;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Result;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.dao.r2dbc.UserInterestCustomDao;
import su.svn.hiload.socialnetwork.model.UserInterest;
import su.svn.hiload.socialnetwork.utils.ClosingConsumer;

import java.util.Objects;

@Repository("userInterestR2dbcDao")
public class UserInterestR2DbcDao implements UserInterestCustomDao {

    private final ConnectionFactory connectionFactory;

    private final static String READ_BY_ID = "SELECT id, user_info_id, interest FROM user_interest WHERE id = ?";

    private final static String READ_BY_USER_INFO_ID = "SELECT id, user_info_id, interest FROM user_interest" +
            " WHERE user_info_id = ?";

    public UserInterestR2DbcDao(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public Mono<UserInterest> readById(long id) {
        Flux<Result> resultsFlux = Mono.from(connectionFactory.create())
                .flatMapMany(connection -> executeReadById(id, connection));
        return liftMapResultToUserInterest(resultsFlux)
                .next()
                .switchIfEmpty(Mono.empty());
    }

    private Flux<? extends Result> executeReadById(long id, Connection connection) {
        return Flux.from(connection.createStatement(READ_BY_ID)
                .bind(0, id)
                .execute())
                .doOnSubscribe(new ClosingConsumer(connection));
    }

    @Override
    public Flux<UserInterest> readAllByUserInfoId(long userInfoId) {
        Flux<Result> resultsFlux = Mono.from(connectionFactory.create())
                .flatMapMany(connection -> executeReadByUserInfo(userInfoId, connection));
        return liftMapResultToUserInterest(resultsFlux);
    }

    private Flux<? extends Result> executeReadByUserInfo(long userInfoId, Connection connection) {
        return Flux.from(connection.createStatement(READ_BY_USER_INFO_ID)
                .bind(0, userInfoId)
                .execute())
                .doOnSubscribe(new ClosingConsumer(connection));
    }

    private Flux<UserInterest> liftMapResultToUserInterest(Flux<Result> resultsFlux) {
        return resultsFlux.flatMap(result -> result.map((row, rowMetadata) -> {
            Long id = row.get("id", Long.class);
            Long userInfoId = row.get("user_info_id", Long.class);
            String interest = row.get("interest", String.class);
            Objects.requireNonNull(userInfoId);

            return new UserInterest(id, userInfoId, interest);
        }));
    }
}
