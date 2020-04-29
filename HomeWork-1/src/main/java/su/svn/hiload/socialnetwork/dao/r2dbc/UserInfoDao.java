package su.svn.hiload.socialnetwork.dao.r2dbc;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.model.UserInfo;

public interface UserInfoDao {

    Mono<Integer> create(UserInfo userInfo);

    Mono<UserInfo> readById(long id);

    Flux<UserInfo> readAll();
}
