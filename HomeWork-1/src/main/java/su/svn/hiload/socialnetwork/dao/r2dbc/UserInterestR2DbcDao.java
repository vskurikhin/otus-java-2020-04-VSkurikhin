package su.svn.hiload.socialnetwork.dao.r2dbc;

import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Result;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.model.UserInterest;

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
                .flatMapMany(connection -> connection.createStatement(READ_BY_ID)
                .bind(0, id)
                .execute());
        return liftMapResultToUserInterest(resultsFlux).next();
    }

    @Override
    public Flux<UserInterest> readAllByUserInfoId(long userInfoId) {
        Flux<Result> resultsFlux = Mono.from(connectionFactory.create())
                .flatMapMany(connection -> connection.createStatement(READ_BY_USER_INFO_ID)
                        .bind(0, userInfoId)
                        .execute());
        return liftMapResultToUserInterest(resultsFlux);
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
