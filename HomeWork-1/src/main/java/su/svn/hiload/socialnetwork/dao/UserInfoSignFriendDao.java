package su.svn.hiload.socialnetwork.dao;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.model.UserInfoSignFriend;

public interface UserInfoSignFriendDao {

    Mono<UserInfoSignFriend> readFriendByIdSignFriend(long friendId, long userId);

    Flux<UserInfoSignFriend> readAllUsersSignFriend(long id);

    Flux<UserInfoSignFriend> searchAllByFirstName(long id, String firstName);

    Flux<UserInfoSignFriend> searchAllBySurName(long id, String surName);

    Flux<UserInfoSignFriend> searchAllByFirstNameAndSurName(long id, String firstName, String surName);
}
