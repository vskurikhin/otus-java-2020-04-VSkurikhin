package su.svn.hiload.socialnetwork.dao;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.dao.r2dbc.MessageCustomDao;
import su.svn.hiload.socialnetwork.model.Message;

import java.util.UUID;

public interface MessageDao extends ReactiveCrudRepository<Message, Long>, MessageCustomDao {

    Mono<Message> findByIdAndLabel(Long id, UUID label);

    Flux<Message> findAll();
}
