package su.svn.hiload.socialnetwork.configs;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Option;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import java.time.Duration;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

@Configuration
@EnableR2dbcRepositories
public class R2dbcConfiguration {

    @Value("${application.db.r2dbc.host}")
    private String dbHost;

    @Value("${application.db.r2dbc.database}")
    private String dbName;

    @Value("${application.db.username}")
    private String dbUser;

    @Value("${application.db.password}")
    private String dbPassword;

    @Bean
    public ConnectionFactory connectionFactory() {
        ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
                .option(DRIVER, "mysql")
                .option(HOST, dbHost)
                .option(USER, dbUser)
                .option(PORT, 3306)
                .option(PASSWORD, dbPassword)
                .option(DATABASE, dbName)
                .option(CONNECT_TIMEOUT, Duration.ofSeconds(3)) // optional, default null, null means no timeout
                .option(SSL, false) // optional, default is enabled, it will be ignore if "sslMode" is set
                .option(Option.valueOf("useServerPrepareStatement"), true) // optional, default false
                .build();
        return ConnectionFactories.get(options);
    }
}
