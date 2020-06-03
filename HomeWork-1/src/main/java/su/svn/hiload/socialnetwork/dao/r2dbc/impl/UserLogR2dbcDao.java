package su.svn.hiload.socialnetwork.dao.r2dbc.impl;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.dao.r2dbc.UserLogCustomDao;
import su.svn.hiload.socialnetwork.model.UserLog;

import java.util.concurrent.atomic.AtomicLong;

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
    public Mono<UserLog> createTransaction(final UserLog userLog) {
        return Mono.from(connectionFactory.create())
                .flatMap(connection -> createUserLogMono(userLog, connection));
    }

    private Mono<UserLog> createUserLogMono(final UserLog userLog, final Connection connection) {
        return Mono.from(connection.beginTransaction())
                .then(longExecution(connection))
                .then(createUserLog(userLog, connection))
                .map(result -> result.map((row, meta) -> getUserLogId(userLog, row)))
                .flatMap(Mono::from)
                .delayUntil(r -> connection.commitTransaction())
                .doOnSuccess(v -> incrementAndGetPrint())
                .doOnError(e -> LOG.error("createUserLogMono ", e))
                .doFinally((st) -> connection.close());
    }

    private Mono<? extends Result> createUserLog(UserLog userLog, Connection connection) {
        return Mono.from(connection.createStatement(CREATE)
                .bind(0, userLog.getUserProfileId())
                .bind(1, userLog.getDateTime())
                .returnGeneratedValues("id")
                .execute());
    }

    private Mono<? extends Result> longExecution(Connection connection) {
        return Mono.from(connection.createStatement(LONG_EXECUTION_STATEMENT)
                .returnGeneratedValues("res")
                .execute());
    }

    private UserLog getUserLogId(UserLog userLog, Row row) {
        Long id = row.get("id", Long.class);
        if (id != null) {
            userLog.setId(id);
        }
        return userLog;
    }

    private void incrementAndGetPrint() {
        long c = count.incrementAndGet();
        System.err.printf("count transaction = %d\n", c);
    }
}
