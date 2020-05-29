package su.svn.hiload.socialnetwork.dao.r2dbc.impl;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.dao.r2dbc.UserInterestCustomDao;
import su.svn.hiload.socialnetwork.exceptions.BufferLimitException;
import su.svn.hiload.socialnetwork.model.UserInterest;
import su.svn.hiload.socialnetwork.utils.ClosingConsumer;
import su.svn.hiload.socialnetwork.utils.Util;

import java.util.Collection;
import java.util.Objects;

@Repository("userInterestR2dbcDao")
public class UserInterestR2DbcDao implements UserInterestCustomDao {

    private static final String READ_BY_ID = "SELECT id, user_info_id, interest FROM user_interest WHERE id = ?";

    private static final String READ_BY_USER_INFO_ID = "SELECT id, user_info_id, interest FROM user_interest" +
            " WHERE user_info_id = ?";

    private static final String SELECT = "SELECT id, user_info_id, interest FROM user_interest";

    private static final String WHERE_USER_INFO_ID_IN = " WHERE user_info_id IN ";

    private static final int MAX_BUFFER = 128;

    private final ConnectionFactory connectionFactory;

    private final ConnectionFactory connectionFactoryRo;

    public UserInterestR2DbcDao(
            ConnectionFactory connectionFactory,
            @Qualifier("connectionFactoryRo") ConnectionFactory connectionFactoryRo) {
        this.connectionFactory = connectionFactory;
        this.connectionFactoryRo = connectionFactoryRo;
    }

    @Override
    public Mono<UserInterest> readById(long id) {
        Flux<Result> resultsFlux = Mono.from(connectionFactoryRo.create())
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
        Flux<Result> resultsFlux = Mono.from(connectionFactoryRo.create())
                .flatMapMany(connection -> executeReadByUserInfo(userInfoId, connection));
        return liftMapResultToUserInterest(resultsFlux);
    }

    @Override
    public Flux<UserInterest> searchAllUserInfoId(Iterable<Long> ids) {
        Flux<Result> resultsFlux = Mono.from(connectionFactoryRo.create())
                .flatMapMany(connection -> executeReadWhereIdIn(Util.makeCollection(ids), connection));
        return liftMapResultToUserInterest(resultsFlux);
    }

    private Publisher<? extends Result> executeReadWhereIdIn(Collection<Long> ids, Connection connection) {
        createStatement(connection, ids);
        return Flux.from(createStatement(connection, ids).execute())
                .doOnSubscribe(new ClosingConsumer(connection));
    }

    private Statement createStatement(Connection connection, Collection<Long> ids) {
        if (1 > ids.size() || ids.size() > MAX_BUFFER)
            BufferLimitException.throwNow("Buffer limit.");
        StringBuilder builder = new StringBuilder(SELECT);
        builder.append(WHERE_USER_INFO_ID_IN);
        builder.append('(');
        builder.append("?, ".repeat(Math.max(0, ids.size() - 1)));
        builder.append("?)");
        Statement statement = connection.createStatement(builder.toString());

        int index = 0;
        for (long id : ids) {
            statement.bind(index++, id);
        }
        return statement;
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
