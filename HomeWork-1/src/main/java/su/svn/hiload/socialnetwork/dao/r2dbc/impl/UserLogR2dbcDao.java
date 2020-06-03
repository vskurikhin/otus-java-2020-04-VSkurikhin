package su.svn.hiload.socialnetwork.dao.r2dbc.impl;

import io.r2dbc.spi.*;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.dao.r2dbc.UserLogCustomDao;
import su.svn.hiload.socialnetwork.model.UserLog;
import su.svn.hiload.socialnetwork.utils.ClosingConsumer;

import java.util.function.BiFunction;
import java.util.function.Function;

@Repository("userLogR2dbcDao")
public class UserLogR2dbcDao implements UserLogCustomDao {

    private static final Logger LOG = LoggerFactory.getLogger(UserLogR2dbcDao.class);

    private static final String CREATE = "INSERT " +
            " INTO user_log (user_profile_id, date_time) VALUES (?, ?)";

    private final ConnectionFactory connectionFactory;

    private final ConnectionFactory connectionFactoryRo;

    public UserLogR2dbcDao(
            ConnectionFactory connectionFactory,
            @Qualifier("connectionFactoryRo") ConnectionFactory connectionFactoryRo) {
        this.connectionFactory = connectionFactory;
        this.connectionFactoryRo = connectionFactoryRo;
    }

    @Override
    public Mono<Integer> create(final UserLog userLog) {
        Publisher<? extends Connection> resourceProvider = connectionFactory.create();

        Function<Connection, Mono<Integer>> resourceClosure = new Function<Connection, Mono<Integer>>() {

            @Override
            public Mono<Integer> apply(Connection connection) {
                Mono<Integer> mono = Mono.just(connection).map(obj -> {
                    return connection.createStatement(CREATE)
                            .bind(0, userLog.getUserProfileId())
                            .bind(1, userLog.getDateTime())
                            .returnGeneratedValues("id");
                }).flatMap(stmt -> {
                    return Mono.from(stmt.execute());
                }).flatMap(result -> {
                    return Mono.from(whenRowsUpdatedSetId(result, userLog));
                }).doOnError(ex -> {
                    ex.printStackTrace();
                });
                return Mono.from(connection.beginTransaction()).then(mono);
            }
        };

        Function<Connection, Mono<Void>> asyncCleanup = new Function<Connection, Mono<Void>>() {

            @Override
            public Mono<Void> apply(Connection connection) {
                Mono<Void> commitMono = Mono.from(connection.commitTransaction());
                // Mono<Void> closeMono = Mono.from(connection.close());
                return commitMono;
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


    /**
     * Function returning a mono which runs resource cleanup if resourceClosure
     * terminates with onError
     */
    Function<Connection, Mono<Void>> asyncCleanupOnError = new Function<Connection, Mono<Void>>() {

        @Override
        public Mono<Void> apply(Connection connection) {
            // Mono<Void> rollbackMono = Mono.from(connection.rollbackTransaction());
            Mono<Void> closeMono = Mono.from(connection.close());
            return closeMono;
        }
    };

    private Flux<Object> executeCreate(UserLog userLog, Connection connection) {

        Mono<Result> o = Mono.from(connection.createStatement(CREATE)
                .bind(0, userLog.getUserProfileId())
                .bind(1, userLog.getDateTime())
                .execute())
                .map(new Function<Result, Result>() {
                    @Override
                    public Result apply(Result result) {
                        result.map(new BiFunction<Row, RowMetadata, Result>() {
                            @Override
                            public Result apply(Row row, RowMetadata rowMetadata) {
                                Long id = row.get(0, Long.class);
                                if (id != null) {
                                    userLog.setId(id);
                                }
                                return result;
                            }
                        });
                        return result;
                    }
                })
                .switchIfEmpty(Mono.empty());

        return Mono.from(connection.beginTransaction())
                .<Object>thenReturn(o)
                .concatWith(connection.commitTransaction())
                .doOnSubscribe(new ClosingConsumer(connection));
    }
}
