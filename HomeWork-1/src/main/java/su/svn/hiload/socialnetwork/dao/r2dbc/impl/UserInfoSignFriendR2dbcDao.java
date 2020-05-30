package su.svn.hiload.socialnetwork.dao.r2dbc.impl;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.dao.UserInfoSignFriendDao;
import su.svn.hiload.socialnetwork.model.UserInfoSignFriend;
import su.svn.hiload.socialnetwork.utils.ClosingConsumer;

import java.util.Objects;

@Service("userInfoSignFriendR2dbcDao")
public class UserInfoSignFriendR2dbcDao implements UserInfoSignFriendDao {

    private static final Logger LOG = LoggerFactory.getLogger(UserInfoSignFriendR2dbcDao.class);

    private static final String READ_BY_ID = "SELECT " +
            " u.id, u.first_name, u.sur_name, u.age, u.sex, u.city, " +
            " (SELECT f.id FROM user_friends f WHERE f.user_info_id = ? AND f.friend_info_id = ?) AS friend " +
            " FROM user_info u " +
            " WHERE id = ? ";

    private static final String READ_ALL_USERS = "SELECT " +
            " u.id, u.first_name, u.sur_name, u.age, u.sex, u.city, f.id AS friend " +
            " FROM user_info u " +
            " LEFT OUTER JOIN user_friends f ON u.id = f.friend_info_id AND f.user_info_id = ? " +
            " WHERE u.id <> ? ";

    private static final String SEARCH_ALL_BY_FIRST_NAME = "SELECT " +
            " u.id, u.first_name, u.sur_name, u.age, u.sex, u.city, f.id AS friend " +
            " FROM user_info u " +
            " LEFT OUTER JOIN user_friends f ON u.id = f.friend_info_id AND f.user_info_id = ? " +
            " WHERE u.first_name LIKE ? ";

    private static final String SEARCH_ALL_BY_SUR_NAME = "SELECT " +
            " u.id, u.first_name, u.sur_name, u.age, u.sex, u.city, f.id AS friend " +
            " FROM user_info u " +
            " LEFT OUTER JOIN user_friends f ON u.id = f.friend_info_id AND f.user_info_id = ? " +
            " WHERE u.sur_name LIKE ? ";

    private static final String SEARCH_ALL_BY_FIRST_NAME_AND_SUR_NAME = "SELECT " +
            " u.id, u.first_name, u.sur_name, u.age, u.sex, u.city, f.id AS friend " +
            " FROM user_info u " +
            " LEFT OUTER JOIN user_friends f ON u.id = f.friend_info_id AND f.user_info_id = ? " +
            " WHERE u.first_name LIKE ? AND u.sur_name LIKE ?";

    private final ConnectionFactory connectionFactory;

    private final ConnectionFactory connectionFactoryRo;

    public UserInfoSignFriendR2dbcDao(
            ConnectionFactory connectionFactory,
            @Qualifier("connectionFactoryRo") ConnectionFactory connectionFactoryRo) {
        this.connectionFactory = connectionFactory;
        this.connectionFactoryRo = connectionFactoryRo;
    }

    public Mono<UserInfoSignFriend> readFriendByIdSignFriend(long friendId, long userId) {
        Flux<Result> resultsFlux = Mono.from(connectionFactoryRo.create())
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
        Flux<Result> resultsFlux = Mono.from(connectionFactoryRo.create())
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

    @Override
    public Flux<UserInfoSignFriend> searchAllByFirstName(long id, String firstName) {
        Flux<Result> resultsFlux = Mono.from(connectionFactory.create())
                .flatMapMany(connection -> executeSearchAllByFirstName(id, firstName, connection));
        return liftMapResultToUserInfo(resultsFlux);
    }

    private Flux<? extends Result> executeSearchAllByFirstName(long id, String firstName, Connection connection) {
        return Flux.from(connection.createStatement(SEARCH_ALL_BY_FIRST_NAME)
                .bind(0, id)
                .bind(1, valueWithWildCard(firstName))
                .execute())
                .doOnSubscribe(new ClosingConsumer(connection));
    }

    @Override
    public Flux<UserInfoSignFriend> searchAllBySurName(long id, String surName) {
        Flux<Result> resultsFlux = Mono.from(connectionFactoryRo.create())
                .flatMapMany(connection -> executeSearchAllBySurName(id, surName, connection));
        return liftMapResultToUserInfo(resultsFlux);
    }

    private Flux<? extends Result> executeSearchAllBySurName(long id, String surName, Connection connection) {
        return Flux.from(connection.createStatement(SEARCH_ALL_BY_SUR_NAME)
                .bind(0, id)
                .bind(1, valueWithWildCard(surName))
                .execute())
                .doOnSubscribe(new ClosingConsumer(connection));
    }

    @Override
    public Flux<UserInfoSignFriend> searchAllByFirstNameAndSurName(long id, String firstName, String surName) {
        Flux<Result> resultsFlux = Mono.from(connectionFactoryRo.create())
                .flatMapMany(connection -> executeSearchAllByFirstNameAndSurName(id, firstName, surName, connection));
        return liftMapResultToUserInfo(resultsFlux);
    }

    private Flux<? extends Result> executeSearchAllByFirstNameAndSurName(
            long id,
            String firstName,
            String surName,
            Connection connection) {
        return Flux.from(connection.createStatement(SEARCH_ALL_BY_FIRST_NAME_AND_SUR_NAME)
                .bind(0, id)
                .bind(1, valueWithWildCard(firstName))
                .bind(2, valueWithWildCard(surName))
                .execute())
                .doOnSubscribe(new ClosingConsumer(connection));
    }

    private String valueWithWildCard(String value) {
        return value + '%';
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
