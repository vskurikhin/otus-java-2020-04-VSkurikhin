package su.svn.hiload.socialnetwork.dao.r2dbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.model.UserInfo;

@Repository("userInfoR2dbcDao")
public class UserInfoR2dbcDao implements UserInfoDao {

    private static final Logger LOG = LoggerFactory.getLogger(UserInfoR2dbcDao.class);

    private final DatabaseClient databaseClient;

    private final static String CREATE = "INSERT INTO user_info (id, first_name, sur_name, age, sex, city) VALUES ($1, $2, $3, $4, $5, $6)";

    private final static String READ_BY_ID = "SELECT id, first_name, sur_name, age, sex, city FROM user_info WHERE id = ";

    private final static String READ_ALL_USERS_BY_ID = "SELECT" +
            " ui.id AS id, ui.first_name AS first_name, ui.sur_name AS sur_name, ui.age AS age, ui.sex AS sex, ui.city AS city," +
            " IF(uf.id IS NOT NULL, 'FALSE', 'TRUE') AS friend" +
            " FROM user_info ui" +
            " LEFT OUTER JOIN user_friends uf ON ui.id = uf.friend_info_id AND uf.user_info_id = ";

    private final static String READ_ALL_FRIENDS = "SELECT" +
            " ui.id AS id, ui.first_name AS first_name, ui.sur_name AS sur_name, ui.age AS age, ui.sex AS sex, ui.city AS city," +
            " 'TRUE' AS friend" +
            " FROM user_info ui" +
            " INNER JOIN user_friends uf ON ui.id = uf.friend_info_id" +
            " WHERE uf.user_info_id = ";

    private final static String WHERE_NOT_CURRENT_USER = " WHERE ui.id != ";

    private final static String INSERT_FRIEND = "INSERT INTO user_friends (user_info_id, friend_info_id) VALUES (";

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
        return databaseClient.execute(READ_BY_ID + id)
                .as(UserInfo.class)
                .fetch().first();
    }

    @Override
    public Flux<UserInfo> readAllUsers(long id) {
        Hooks.onOperatorDebug();
        return databaseClient.execute(READ_ALL_USERS_BY_ID + id + WHERE_NOT_CURRENT_USER + id)
                .as(UserInfo.class)
                .fetch().all();
    }

    @Override
    public Flux<UserInfo> readAllFriends(long id) {
        return databaseClient.execute(READ_ALL_FRIENDS + id)
                .as(UserInfo.class)
                .fetch().all();
    }

    @Override
    public Mono<UserInfo> addFriend(long id, long friendId) {
        return databaseClient.execute(INSERT_FRIEND + id + "," + friendId + ")")
                .fetch()
                .rowsUpdated()
                .flatMap(integer -> this.readById(friendId));
    }
}
