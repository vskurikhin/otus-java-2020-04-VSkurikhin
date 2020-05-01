package su.svn.hiload.socialnetwork.dao.r2dbc;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import su.svn.hiload.socialnetwork.model.UserInterest;

@Repository
public interface UserInterestDao extends ReactiveCrudRepository<UserInterestDao, Long>, UserInterestCustomDao {
    @Query("SELECT * FROM user_interest WHERE user_info_id = $1")
    Flux<UserInterest> findAllByUserInfoId(long id);
}
