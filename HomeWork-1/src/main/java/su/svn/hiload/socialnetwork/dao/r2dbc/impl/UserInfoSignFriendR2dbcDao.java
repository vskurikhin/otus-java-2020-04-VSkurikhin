package su.svn.hiload.socialnetwork.dao.r2dbc.impl;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.dao.UserInfoSignFriendDao;
import su.svn.hiload.socialnetwork.model.UserInfoSignFriend;
import su.svn.hiload.socialnetwork.utils.ClosingConsumer;

import java.util.Objects;

@Service("userInfoSignFriendR2dbcDao")
public class UserInfoSignFriendR2dbcDao implements UserInfoSignFriendDao {

    private final ConnectionFactory connectionFactory;

    private static final Logger LOG = LoggerFactory.getLogger(UserInfoSignFriendR2dbcDao.class);

    private final static String READ_BY_ID = "SELECT " +
            " u.id, u.first_name, u.sur_name, u.age, u.sex, u.city, " +
            " (SELECT f.id FROM user_friends f WHERE f.user_info_id = ? AND f.friend_info_id = ?) AS friend " +
            " FROM user_info u " +
            " WHERE id = ? ";

    private final static String READ_ALL_USERS = "SELECT " +
            " u.id, u.first_name, u.sur_name, u.age, u.sex, u.city, f.id AS friend " +
            " FROM user_info u " +
            " LEFT OUTER JOIN user_friends f ON u.id = f.friend_info_id AND f.user_info_id = ? " +
            " WHERE u.id <> ? ";

    public UserInfoSignFriendR2dbcDao(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public Mono<UserInfoSignFriend> readFriendByIdSignFriend(long friendId, long userId) {
        Flux<Result> resultsFlux = Mono.from(connectionFactory.create())
                .flatMapMany(connection -> executeReadById(friendId, userId, connection));
        return liftMapResultToUserInfo(resultsFlux)
                .next()
                .switchIfEmpty(Mono.empty());
    }

    private Flux<? extends Result> executeReadById(long friendId, long userId, Connection connection) {
        return Flux.from(connection.createStatement(READ_BY_ID)
                .bind(0, userId)
                .bind(1, friendId)
                .bind(2, friendId)
                .execute())
                .doOnSubscribe(new ClosingConsumer(connection));
    }

    public Flux<UserInfoSignFriend> readAllUsersSignFriend(long id) {
        Flux<Result> resultsFlux = Mono.from(connectionFactory.create())
                .flatMapMany(connection -> executeReadAllUsers(id, connection));
        return liftMapResultToUserInfo(resultsFlux);
    }

    private Flux<? extends Result> executeReadAllUsers(long id, Connection connection) {
        return Flux.from(connection.createStatement(READ_ALL_USERS)
                .bind(0, id)
                .bind(1, id)
                .execute())
                .doOnSubscribe(new ClosingConsumer(connection));
    }

    private Flux<UserInfoSignFriend> liftMapResultToUserInfo(Flux<Result> resultsFlux) {
        return resultsFlux.flatMap(result -> result.map((row, rowMetadata) -> {
            Long id = row.get("id", Long.class);
            String firstName = row.get("first_name", String.class);
            String surName = row.get("sur_name", String.class);
            Integer age = row.get("age", Integer.class);
            String sex = row.get("sex", String.class);
            String city = row.get("city", String.class);
            Long friend = row.get("friend", Long.class);
            Objects.requireNonNull(id);

            return new UserInfoSignFriend(id, firstName, surName, age, sex, city, friend != null);
        }));
    }
}
