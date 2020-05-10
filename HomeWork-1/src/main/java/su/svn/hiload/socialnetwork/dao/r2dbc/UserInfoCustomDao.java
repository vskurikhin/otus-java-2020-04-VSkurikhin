package su.svn.hiload.socialnetwork.dao.r2dbc;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.model.UserInfo;

public interface UserInfoCustomDao {

    Mono<Integer> create(UserInfo userInfo);

    Mono<UserInfo> readFirstById(long id);

    Flux<UserInfo> readAllUsers(long id);

    Flux<UserInfo> readAllFriends(long id);

    Mono<UserInfo> addFriend(long id, long friendId);

    Mono<Integer> update(UserInfo userInfo);
}
