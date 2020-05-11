package su.svn.hiload.socialnetwork.dao;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.dao.r2dbc.UserInfoCustomDao;
import su.svn.hiload.socialnetwork.model.UserInfo;

public interface UserInfoDao extends ReactiveCrudRepository<UserInfo, Long>, UserInfoCustomDao {

    Mono<UserInfo> findFirstById(long id);

    Flux<UserInfo> findAllByIdNot(long id);
}
