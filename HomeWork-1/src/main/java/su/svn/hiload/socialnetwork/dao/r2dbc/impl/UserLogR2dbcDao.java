package su.svn.hiload.socialnetwork.dao.r2dbc.impl;

import io.r2dbc.spi.*;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.dao.r2dbc.UserLogCustomDao;
import su.svn.hiload.socialnetwork.model.UserLog;
import su.svn.hiload.socialnetwork.utils.ClosingConsumer;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

@Repository("userLogR2dbcDao")
public class UserLogR2dbcDao implements UserLogCustomDao {

    private static final Logger LOG = LoggerFactory.getLogger(UserLogR2dbcDao.class);

    private static final String CREATE = "INSERT " +
            " INTO user_log (user_profile_id, date_time) VALUES (?, ?)";

    private static final String LONG_EXECUTION_STATEMENT = "SELECT " +
            " benchmark(" +
            "  (SELECT FLOOR(" +
            "   RAND((SELECT UNIX_TIMESTAMP(NOW())))" +
            "     * (4999999 - 99999 + 1) + 99999))," +
            "    md5('when will it end?')" +
            ")";

    private final AtomicLong count = new AtomicLong(0);

    private final ConnectionFactory connectionFactory;

    private final ConnectionFactory connectionFactoryRo;

    public UserLogR2dbcDao(
            ConnectionFactory connectionFactory,
            @Qualifier("connectionFactoryRo") ConnectionFactory connectionFactoryRo) {
        this.connectionFactory = connectionFactory;
        this.connectionFactoryRo = connectionFactoryRo;
    }

    @Override
    public Mono<Integer> createTransaction(final UserLog userLog) {
        Publisher<? extends Connection> resourceProvider = connectionFactory.create();

        Function<Connection, Mono<Integer>> resourceClosure = new Function<Connection, Mono<Integer>>() {

            @Override
            public Mono<Integer> apply(Connection connection) {
                Mono<Integer> monoInsertUserLog = Mono.just(connection).map(obj -> {
                    return connection.createStatement(CREATE)
                            .bind(0, userLog.getUserProfileId())
                            .bind(1, userLog.getDateTime())
                            .returnGeneratedValues("id");
                }).flatMap(stmt -> {
                    return Mono.from(stmt.execute());
                }).flatMap(result -> {
                    return Mono.from(whenRowsUpdatedSetId(result, userLog));
                }).doOnError(ex -> {
                    LOG.error("UserInsertLog ", ex);
                });

                Mono<Integer> monoLongExecution = Mono.just(connection)
                        .map(obj -> connection.createStatement(LONG_EXECUTION_STATEMENT)
                        .returnGeneratedValues("res"))
                        .flatMap(stmt -> Mono.from(stmt.execute()))
                        .flatMap(result -> Mono.from(result.getRowsUpdated()))
                        .doOnError(Throwable::printStackTrace);

                Mono<Integer> monoTransaction = Mono.from(connection.beginTransaction())
                        .then(monoLongExecution)
                        .then(monoInsertUserLog);

                return Mono.from(connection.setAutoCommit(false)).then(monoTransaction);
            }
        };

        return Mono.usingWhen(resourceProvider, resourceClosure, asyncCleanup, asyncCleanupOnError);
    }


    private Mono<Integer> whenRowsUpdatedSetId(Result result, UserLog userLog) {
        return Mono.from(mapResultUserLog(result, userLog))
                .flatMap(i -> Mono.from(result.getRowsUpdated()));
    }

    private Publisher<Integer> mapResultUserLog(Result result, UserLog userLog) {
        return result.map((row, rowMetadata) -> userLogSetId(row, userLog));
    }

    private Integer userLogSetId(Row row, UserLog userLog) {
        Integer id = row.get("id", Integer.class);
        if (id != null) {
            userLog.setId(id);
        }
        return id;
    }

    private void incrementAndGetPrint() {
        long c = count.incrementAndGet();
        System.err.printf("count transaction = %d\n", c);
    }

    private Function<Connection, Mono<Void>> asyncCleanup = connection -> {
        Mono<Void> commitMono = Mono.from(connection.close());
        return commitMono.doOnSuccess(v -> incrementAndGetPrint());
    };

    /**
     * Function returning a mono which runs resource cleanup if resourceClosure
     * terminates with onError
     */
    private Function<Connection, Mono<Void>> asyncCleanupOnError = connection -> {
        return Mono.from(connection.close());
    };
}
