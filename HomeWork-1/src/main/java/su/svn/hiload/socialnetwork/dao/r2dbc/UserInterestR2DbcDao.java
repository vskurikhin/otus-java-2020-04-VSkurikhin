package su.svn.hiload.socialnetwork.dao.r2dbc;

import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.model.UserInterest;

@Repository("userInterestR2dbcDao")
public class UserInterestR2DbcDao implements UserInterestCustomDao {

    private final DatabaseClient databaseClient;

    private final static String READ_BY_ID = "SELECT id, user_info_id, interest FROM user_interest WHERE id = ";

    private final static String READ_BY_USER_INFO_ID = "SELECT id, user_info_id, interest FROM user_interest" +
            " WHERE user_info_id = ";

    public UserInterestR2DbcDao(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<UserInterest> readById(long id) {
        return databaseClient.execute(READ_BY_ID + id)
                .as(UserInterest.class)
                .fetch().first();
    }

    @Override
    public Flux<UserInterest> readAllByUserInfoId(long userInfoId) {
        return databaseClient.execute(READ_BY_USER_INFO_ID + userInfoId)
                .as(UserInterest.class)
                .fetch().all();
    }
}
