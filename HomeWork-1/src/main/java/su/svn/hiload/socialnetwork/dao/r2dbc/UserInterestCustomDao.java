package su.svn.hiload.socialnetwork.dao.r2dbc;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.model.UserInterest;

public interface UserInterestCustomDao {

    Mono<UserInterest> readById(long id);

    Flux<UserInterest> readAllByUserInfoId(long userInfoId);

    Flux<UserInterest> searchAllUserInfoId(Iterable<Long> ids);
}
