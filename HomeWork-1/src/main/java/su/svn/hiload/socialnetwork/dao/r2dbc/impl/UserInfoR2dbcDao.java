package su.svn.hiload.socialnetwork.dao.r2dbc.impl;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.dao.r2dbc.UserInfoCustomDao;
import su.svn.hiload.socialnetwork.model.UserInfo;
import su.svn.hiload.socialnetwork.utils.ClosingConsumer;

import java.util.Objects;

import static su.svn.hiload.socialnetwork.utils.ErrorCode.CREATE_SWITCH_IF_EMPTY;
import static su.svn.hiload.socialnetwork.utils.ErrorCode.UPDATE_SWITCH_IF_EMPTY;

@Repository("userInfoR2dbcDao")
public class UserInfoR2dbcDao implements UserInfoCustomDao {

    private final ConnectionFactory connectionFactory;

    private static final Logger LOG = LoggerFactory.getLogger(UserInfoR2dbcDao.class);

    private final static String CREATE = "INSERT " +
            " INTO user_info (id, first_name, sur_name, age, sex, city) " +
            " VALUES (?, ?, ?, ?, ?, ?) ";

    private final static String READ_BY_ID = "SELECT " +
            " id, first_name, sur_name, age, sex, city " +
            " FROM user_info " +
            " WHERE id = ? ";

    private final static String READ_ALL_USERS_BY_ID = "SELECT " +
            " id, first_name, sur_name, age, sex, city " +
            " FROM user_info " +
            " WHERE id <> ? ";

    private final static String READ_ALL_FRIENDS = "SELECT " +
            " u.id AS id, u.first_name AS first_name, u.sur_name AS sur_name, u.age AS age, u.sex AS sex, u.city AS city " +
            " FROM user_info u " +
            " INNER JOIN user_friends f ON u.id = f.friend_info_id " +
            " WHERE f.user_info_id = ? ";

    private final static String UPDATE = "UPDATE " +
            " user_info SET " +
            " first_name = ?, " +
            " sur_name = ?, " +
            " age = ?, " +
            " sex = ?, " +
            " city = ? " +
            " WHERE id = ? ";

    private final static String INSERT_FRIEND = "INSERT " +
            " INTO user_friends (user_info_id, friend_info_id) " +
            " VALUES (?, ?) ";

    public UserInfoR2dbcDao(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public Mono<Integer> create(UserInfo userInfo) {
        Flux<Result> resultsFlux = Mono.from(connectionFactory.create())
                .flatMapMany(connection -> executeCreate(userInfo, connection));
        return resultsFlux
                .flatMap(Result::getRowsUpdated)
                .next()
                .switchIfEmpty(Mono.just(CREATE_SWITCH_IF_EMPTY));
    }

    private Flux<? extends Result> executeCreate(UserInfo userInfo, Connection connection) {
        return Flux.from(connection.createStatement(CREATE)
                .bind(0, userInfo.getId())
                .bind(1, userInfo.getFirstName())
                .bind(2, userInfo.getSurName())
                .bind(3, userInfo.getAge())
                .bind(4, userInfo.getSex())
                .bind(5, userInfo.getCity())
                .execute())
                .doOnSubscribe(subscription -> Mono.from(connection.close()).subscribe());
    }

    @Override
    public Mono<UserInfo> readFirstById(long id) {
        Flux<Result> resultsFlux = Mono.from(connectionFactory.create())
                .flatMapMany(connection -> executeReadById(id, connection));
        return liftMapResultToUserInfo(resultsFlux)
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
    public Flux<UserInfo> readAllUsers(long id) {
        Flux<Result> resultsFlux = Mono.from(connectionFactory.create())
                .flatMapMany(connection -> executeReadAllUsersById(id, connection));
        return liftMapResultToUserInfo(resultsFlux);
    }

    private Flux<? extends Result> executeReadAllUsersById(long id, Connection connection) {
        return Flux.from(connection.createStatement(READ_ALL_USERS_BY_ID)
                .bind(0, id)
                .execute())
                .doOnSubscribe(new ClosingConsumer(connection));
    }

    @Override
    public Flux<UserInfo> readAllFriends(long id) {
        Flux<Result> resultsFlux = Mono.from(connectionFactory.create())
                .flatMapMany(connection -> executeReadAllFriends(id, connection));
        return liftMapResultToUserInfo(resultsFlux);
    }

    private Flux<? extends Result> executeReadAllFriends(long id, Connection connection) {
        return Flux.from(connection.createStatement(READ_ALL_FRIENDS)
                .bind(0, id)
                .execute())
                .doOnSubscribe(new ClosingConsumer(connection));
    }

    @Override
    public Mono<UserInfo> addFriend(long id, long friendId) {
        Flux<Result> resultsFlux = Mono.from(connectionFactory.create())
                .flatMapMany(connection -> executeInsertFriend(id, friendId, connection));
        return resultsFlux
                .flatMap(result -> result.map((row, rowMetadata) -> row.get(0, Integer.class)))
                .flatMap(count -> count > 0 ? readFirstById(friendId) : Mono.empty())
                .next()
                .switchIfEmpty(Mono.empty());
    }

    private Flux<? extends Result> executeInsertFriend(long id, long friendId, Connection connection) {
        return Flux.from(connection.createStatement(INSERT_FRIEND)
                .bind(0, id)
                .bind(1, friendId)
                .execute())
                .doOnSubscribe(new ClosingConsumer(connection));
    }

    @Override
    public Mono<Integer> update(UserInfo userInfo) {
        Flux<Result> resultsFlux = Mono.from(connectionFactory.create())
                .flatMapMany(connection -> executeUpdate(userInfo, connection));
        return resultsFlux.flatMap(Result::getRowsUpdated)
                .next()
                .switchIfEmpty(Mono.just(UPDATE_SWITCH_IF_EMPTY));
    }

    private Flux<? extends Result> executeUpdate(UserInfo userInfo, Connection connection) {
        return Flux.from(connection.createStatement(UPDATE)
                .bind(0, userInfo.getFirstName())
                .bind(1, userInfo.getSurName())
                .bind(2, userInfo.getAge())
                .bind(3, userInfo.getSex())
                .bind(4, userInfo.getCity())
                .bind(5, userInfo.getId())
                .execute())
                .doOnSubscribe(new ClosingConsumer(connection));
    }

    private Flux<UserInfo> liftMapResultToUserInfo(Flux<Result> resultsFlux) {
        return resultsFlux.flatMap(result -> result.map((row, rowMetadata) -> {
            Long id = row.get("id", Long.class);
            String firstName = row.get("first_name", String.class);
            String surName = row.get("sur_name", String.class);
            Integer age = row.get("age", Integer.class);
            String sex = row.get("sex", String.class);
            String city = row.get("city", String.class);
            Objects.requireNonNull(id);
            return new UserInfo(id, firstName, surName, age, sex, city);
        }));
    }
}
