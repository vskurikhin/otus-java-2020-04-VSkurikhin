package su.svn.hiload.socialnetwork.dao.r2dbc.impl;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Row;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.dao.r2dbc.MessageCustomDao;
import su.svn.hiload.socialnetwork.model.Message;
import su.svn.hiload.socialnetwork.utils.ClosingConsumer;

import java.util.Objects;
import java.util.UUID;

import static su.svn.hiload.socialnetwork.utils.ErrorCode.CREATE_SWITCH_IF_EMPTY;

@Repository("messageR2dbcDao")
public class MessageR2dbcDao implements MessageCustomDao {

    private static final String FIND_ALL = "SELECT " +
            " id, label, from_id, from_label, to_id, to_label, text_message " +
            " FROM message ";

    private static final String CREATE = "INSERT " +
            " INTO message " +
            " (id, label, from_id, from_label, to_id, to_label, text_message)" +
            " VALUES " +
            " ((message_id_nextval($1)), $2, $3, $4, $5, $6, $7) ";

    private final ConnectionFactory connectionFactory;

    private final ConnectionFactory connectionFactoryRo;

    public MessageR2dbcDao(
            ConnectionFactory connectionFactory,
            @Qualifier("connectionFactoryRo") ConnectionFactory connectionFactoryRo) {
        this.connectionFactory = connectionFactory;
        this.connectionFactoryRo = connectionFactoryRo;
    }

    @Override
    public Mono<Integer> create(Message message) {
        Flux<Result> resultsFlux = Mono.from(connectionFactory.create())
                .flatMapMany(connection -> executeCreate(message, connection));
        return resultsFlux
                // .flatMap(result -> result.map((row, meta) -> getMessageId(message, row)))
                .flatMap(Result::getRowsUpdated)
                .next()
                .switchIfEmpty(Mono.just(CREATE_SWITCH_IF_EMPTY));
    }
    /*
                .flatMap(result -> result.map((row, rowMetadata) -> row.get(0, Integer.class)))
                .flatMap(count -> count > 0 ? readFirstById(friendId) : Mono.empty())
                .next()
     */

    private Message getMessageId(Message message, Row row) {
        Long id = row.get("id", Long.class);
        if (id != null) {
            message.setId(id);
        }
        return message;
    }

    private Flux<? extends Result> executeCreate(Message message, Connection connection) {
        return Flux.from(connection.createStatement(CREATE)
                .bind("$1", message.getLabel())
                .bind("$2", message.getLabel())
                .bind("$3", message.getFromId())
                .bind("$4", message.getFromLabel())
                .bind("$5", message.getToId())
                .bind("$6", message.getToLabel())
                .bind("$7", message.getTextMessage())
                .returnGeneratedValues("id")
                .execute())
                .doOnSubscribe(new ClosingConsumer(connection));
    }

    @Override
    public Flux<Message> readAll() {
        Flux<Result> resultsFlux = Mono.from(connectionFactoryRo.create())
                .flatMapMany(connection -> Flux.from(connection.createStatement(FIND_ALL)
                .execute())
                .doOnSubscribe(new ClosingConsumer(connection)));
        return liftMapResultToUserProfile(resultsFlux);
    }

    private Flux<Message> liftMapResultToUserProfile(Flux<Result> resultsFlux) {
        return resultsFlux.flatMap(result -> result.map((row, rowMetadata) -> {
            Long id = row.get("id", Long.class);
            UUID label = row.get("label", UUID.class);
            Long fromId = row.get("from_id", Long.class);
            UUID fromLabel = row.get("from_label", UUID.class);
            Long toId = row.get("from_id", Long.class);
            UUID toLabel = row.get("from_label", UUID.class);
            String textMessage = row.get("text_message", String.class);
            Objects.requireNonNull(id);

            return Message.builder()
                    .id(id)
                    .label(label)
                    .fromId(fromId)
                    .fromLabel(fromLabel)
                    .toId(toId)
                    .toLabel(toLabel)
                    .textMessage(textMessage).build();
        }));
    }
}
