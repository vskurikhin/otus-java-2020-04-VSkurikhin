package su.svn.hiload.socialnetwork.dao;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import su.svn.hiload.socialnetwork.dao.r2dbc.MessageCustomDao;
import su.svn.hiload.socialnetwork.model.Message;

public interface MessageDao extends ReactiveCrudRepository<Message, Long>, MessageCustomDao {

    @Query("SELECT * FROM message")
    Flux<Message> findAll();
}
