package su.svn.hiload.socialnetwork.dao.r2dbc.impl;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.dao.r2dbc.MessageCustomDao;
import su.svn.hiload.socialnetwork.model.Message;
import su.svn.hiload.socialnetwork.utils.ClosingConsumer;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Repository("messageR2dbcDao")
public class MessageR2dbcDao implements MessageCustomDao {

    private static final Logger LOG = LoggerFactory.getLogger(MessageR2dbcDao.class);

    private static final String FIND_ALL = "SELECT " +
            " id, label, from_id, from_label, to_id, to_label, text_message " +
            " FROM message ";

    private static final String CREATE = "INSERT " +
            " INTO message " +
            " (id, label, from_id, from_label, to_id, to_label, text_message)" +
            " VALUES " +
            " ($1, $2, $3, $4, $5, $6, $7) ";
    private static final String SELECT_NEXTVAL_ID = "SELECT message_id_nextval($1)";
    private static final String NEXTVAL_ID = "nextval_id";

    private final AtomicLong count = new AtomicLong(0);

    private final ConnectionFactory connectionFactory;

    private final ConnectionFactory connectionFactoryRo;

    public MessageR2dbcDao(
            ConnectionFactory connectionFactory,
            @Qualifier("connectionFactoryRo") ConnectionFactory connectionFactoryRo) {
        this.connectionFactory = connectionFactory;
        this.connectionFactoryRo = connectionFactoryRo;
    }

    @Override
    public Mono<Message> create(final Message message) {
        return Mono.from(connectionFactory.create())
                .flatMap(connection -> createTransaction(connection, message));
    }

    private Mono<? extends Message> createTransaction(Connection connection, final Message message) {
        return Mono.from(connection.beginTransaction())
                .then(getNextvalId(connection, message))
                .map(result -> result.map((row, meta) -> setMessageId(message, row)))
                .flatMap(Mono::from)
                .map(m1 -> executeCreate(connection, message))
                .flatMap(r -> Mono.just(message))
                .delayUntil(m2 -> connection.commitTransaction())
                .doOnSuccess(m3 -> incrementAndGetPrint())
                .doOnError(e -> LOG.error("createUserLogMono ", e))
                .doFinally((st) -> connection.close());
    }

    private void incrementAndGetPrint() {
        long c = count.incrementAndGet();
    }

    private Message setMessageId(final Message message, Row row) {
        Long id = row.get(NEXTVAL_ID, Long.class);
        if (id != null) {
            message.setId(id);
        }
        return message;
    }

    private Mono<? extends Result> getNextvalId(Connection connection, Message message) {
        return Mono.from(connection.createStatement(SELECT_NEXTVAL_ID)
                .bind("$1", message.getLabel())
                .returnGeneratedValues(NEXTVAL_ID)
                .execute());
    }

    private Mono<? extends Result> executeCreate(Connection connection, Message message) {
        return Mono.from(connection.createStatement(CREATE)
                .bind("$1", message.getId())
                .bind("$2", message.getLabel())
                .bind("$3", message.getFromId())
                .bind("$4", message.getFromLabel())
                .bind("$5", message.getToId())
                .bind("$6", message.getToLabel())
                .bind("$7", message.getTextMessage())
                .execute());
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
