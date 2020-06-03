package su.svn.hiload.socialnetwork.dao;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.dao.r2dbc.UserLogCustomDao;
import su.svn.hiload.socialnetwork.model.UserLog;

public interface UserLogDao extends ReactiveCrudRepository<UserLog, Long>, UserLogCustomDao {
    <S extends UserLog> Mono<S> save(S entity);

    <S extends UserLog> Flux<S> saveAll(Iterable<S> entities);
}
