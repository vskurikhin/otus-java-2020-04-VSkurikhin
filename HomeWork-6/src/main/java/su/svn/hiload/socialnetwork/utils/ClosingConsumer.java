package su.svn.hiload.socialnetwork.utils;

import io.r2dbc.spi.Connection;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

public class ClosingConsumer implements Consumer<Subscription> {

    private final Connection connection;

    public ClosingConsumer(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void accept(Subscription subscription) {
        Mono.from(connection.close()).subscribe();
    }
}
