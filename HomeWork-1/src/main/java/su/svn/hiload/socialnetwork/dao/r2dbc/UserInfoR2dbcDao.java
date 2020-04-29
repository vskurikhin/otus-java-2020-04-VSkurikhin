package su.svn.hiload.socialnetwork.dao.r2dbc;

import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.model.UserInfo;

@Repository("userInfoR2dbcDao")
public class UserInfoR2dbcDao implements UserInfoDao {

    private final DatabaseClient databaseClient;

    private final static String CREATE = "INSERT INTO user_info (id, first_name, sur_name, age, sex, city) VALUES ($1, $2, $3, $4, $5, $6)";

    private final static String READ_BY_ID = "SELECT id, first_name, sur_name, age, sex, city FROM user_info WHERE id = $1";

    private final static String READ_ALL = "SELECT id, first_name, sur_name, age, sex, city FROM user_info";

    public UserInfoR2dbcDao(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<Integer> create(UserInfo userInfo) {
        return databaseClient.execute(CREATE)
                .bind("$1", userInfo.getId())
                .bind("$2", userInfo.getFirstName())
                .bind("$3", userInfo.getSurName())
                .bind("$4", userInfo.getAge())
                .bind("$5", userInfo.getSex())
                .bind("$6", userInfo.getCity())
                .fetch()
                .rowsUpdated();
    }

    @Override
    public Mono<UserInfo> readById(long id) {
        return databaseClient.execute(READ_BY_ID)
                .bind("$1", id)
                .as(UserInfo.class)
                .fetch().first();
    }

    @Override
    public Flux<UserInfo> readAll() {
        return databaseClient.execute(READ_ALL)
                .as(UserInfo.class)
                .fetch().all();
    }
}
