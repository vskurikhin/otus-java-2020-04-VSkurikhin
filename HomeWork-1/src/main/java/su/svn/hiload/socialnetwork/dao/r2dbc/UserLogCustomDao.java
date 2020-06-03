package su.svn.hiload.socialnetwork.dao.r2dbc;

import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.model.UserLog;

public interface UserLogCustomDao {

    Mono<Integer> create(UserLog userLog);
}
