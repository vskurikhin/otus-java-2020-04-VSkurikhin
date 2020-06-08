package su.svn.hiload.socialnetwork.dao.r2dbc;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.model.Message;

public interface MessageCustomDao {

    Mono<Message> create(Message message);

    Flux<Message> readAll();
}
