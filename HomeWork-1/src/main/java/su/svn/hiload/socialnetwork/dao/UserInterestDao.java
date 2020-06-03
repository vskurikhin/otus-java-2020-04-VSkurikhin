package su.svn.hiload.socialnetwork.dao;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.dao.r2dbc.UserInterestCustomDao;
import su.svn.hiload.socialnetwork.model.UserInterest;

public interface UserInterestDao extends ReactiveCrudRepository<UserInterest, Long>, UserInterestCustomDao {

    @Query("SELECT id, user_info_id, interest FROM user_interest WHERE id = ?")
    Mono<UserInterest> findById(long id);

    @Query("SELECT * FROM user_interest WHERE user_info_id = ?")
    Flux<UserInterest> findAllByUserInfoId(long id);
}
