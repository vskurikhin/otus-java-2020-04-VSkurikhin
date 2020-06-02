package su.svn.hiload.socialnetwork.configs;

import io.r2dbc.spi.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.r2dbc.core.DefaultReactiveDataAccessStrategy;
import org.springframework.data.r2dbc.core.ReactiveDataAccessStrategy;
import org.springframework.data.r2dbc.dialect.MySqlDialect;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.data.r2dbc.repository.support.R2dbcRepositoryFactory;
import su.svn.hiload.socialnetwork.dao.UserInfoDao;
import su.svn.hiload.socialnetwork.dao.UserInterestDao;
import su.svn.hiload.socialnetwork.dao.UserProfileDao;
import su.svn.hiload.socialnetwork.dao.r2dbc.impl.UserInfoR2dbcDao;
import su.svn.hiload.socialnetwork.dao.r2dbc.impl.UserInterestR2DbcDao;
import su.svn.hiload.socialnetwork.dao.r2dbc.impl.UserProfileR2dbcDao;

import java.time.Duration;

import static io.r2dbc.pool.PoolingConnectionFactoryProvider.*;
import static io.r2dbc.spi.ConnectionFactoryOptions.*;

@Configuration
@EnableR2dbcRepositories
public class R2dbcConfiguration {

    @Value("${application.db.r2dbc.host-rw}")
    private String dbHostRw;

    @Value("${application.db.r2dbc.host-ro}")
    private String dbHostRo;

    @Value("${application.db.r2dbc.port-rw}")
    private int dbPortRw;

    @Value("${application.db.r2dbc.port-ro}")
    private int dbPortRo;

    @Value("${application.db.r2dbc.database}")
    private String dbName;

    @Value("${application.db.r2dbc.pool.connect-timeout:500}")
    private int connectTimeout;

    @Value("${application.db.r2dbc.pool.duration}")
    private int poolDuration;

    @Value("${application.db.r2dbc.pool.validation-depth}")
    private ValidationDepth validationDepth;

    @Value("${application.db.r2dbc.pool.validation-query}")
    private String validationQuery;

    @Value("${spring.r2dbc.pool.acquire-retry:3}")
    private int acquireRetry;

    @Value("${spring.r2dbc.pool.initial-size:10}")
    private int initialSize;

    @Value("${spring.r2dbc.pool.max-size:10}")
    private int maxSize;

    @Value("${application.db.username}")
    private String dbUser;

    @Value("${application.db.password}")
    private String dbPassword;

    @Bean
    @Primary
    public ConnectionFactory connectionFactory() {
        ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
                .option(DRIVER, "pool")
                .option(PROTOCOL, "mysql")
                .option(HOST, dbHostRw)
                .option(PORT, dbPortRw)
                .option(USER, dbUser)
                .option(PASSWORD, dbPassword)
                .option(DATABASE, dbName)
                .option(ACQUIRE_RETRY, acquireRetry)
                .option(CONNECT_TIMEOUT, Duration.ofMillis(connectTimeout))
                .option(INITIAL_SIZE, initialSize)
                .option(MAX_SIZE, Math.max(maxSize, 100))
                .option(VALIDATION_DEPTH, validationDepth)
                .option(VALIDATION_QUERY, validationQuery)
                .option(Option.valueOf("maxLifeTime"), poolDuration)
                .option(Option.valueOf("maxIdleTime"), Duration.ofMillis(100))
                .option(Option.valueOf("registerJmx"), true)
                .option(Option.valueOf("useServerPrepareStatement"), true) // optional, default false
                .build();
        return ConnectionFactories.get(options);
    }

    @Bean(name = "connectionFactoryRo")
    public ConnectionFactory connectionFactoryRo() {
        ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
                .option(DRIVER, "pool")
                .option(PROTOCOL, "mysql")
                .option(HOST, dbHostRo)
                .option(PORT, dbPortRo)
                .option(USER, dbUser)
                .option(PASSWORD, dbPassword)
                .option(DATABASE, dbName)
                .option(ACQUIRE_RETRY, acquireRetry)
                .option(CONNECT_TIMEOUT, Duration.ofMillis(connectTimeout))
                .option(INITIAL_SIZE, initialSize)
                .option(MAX_SIZE, maxSize)
                .option(VALIDATION_DEPTH, validationDepth)
                .option(VALIDATION_QUERY, validationQuery)
                .option(Option.valueOf("maxLifeTime"), poolDuration)
                .option(Option.valueOf("maxIdleTime"), Duration.ofMillis(100))
                .option(Option.valueOf("registerJmx"), true)
                .option(Option.valueOf("useServerPrepareStatement"), true) // optional, default false
                .build();
        return ConnectionFactories.get(options);
    }

    public ConnectionFactory connectionFactoryNoPool() {
        ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
                .option(DRIVER, "mysql")
                .option(HOST, dbHostRw)
                .option(PORT, dbPortRw)
                .option(USER, dbUser)
                .option(PASSWORD, dbPassword)
                .option(DATABASE, dbName)
                .option(CONNECT_TIMEOUT, Duration.ofSeconds(3)) // optional, default null, null means no timeout
                .option(SSL, false) // optional, default is enabled, it will be ignore if "sslMode" is set
                .option(Option.valueOf("useServerPrepareStatement"), true) // optional, default false
                .build();
        return ConnectionFactories.get(options);
    }

    @Bean
    public ReactiveDataAccessStrategy reactiveDataAccessStrategy() {
        return new DefaultReactiveDataAccessStrategy(MySqlDialect.INSTANCE);
    }

    @Bean
    public R2dbcRepositoryFactory factory(DatabaseClient client, ReactiveDataAccessStrategy strategy) {
        return new R2dbcRepositoryFactory(client, strategy);
    }

    @Bean("userInfoDao")
    public UserInfoDao userInfoDao(R2dbcRepositoryFactory factory, UserInfoR2dbcDao userInfoR2dbcDao) {
        return factory.getRepository(UserInfoDao.class, userInfoR2dbcDao);
    }

    @Bean("userInterestDao")
    public UserInterestDao userInterestDao(R2dbcRepositoryFactory factory, UserInterestR2DbcDao userInterestR2DbcDao) {
        return factory.getRepository(UserInterestDao.class, userInterestR2DbcDao);
    }

    @Bean("userProfileDao")
    public UserProfileDao userProfileDao(R2dbcRepositoryFactory factory, UserProfileR2dbcDao userProfileR2dbcDao) {
        return factory.getRepository(UserProfileDao.class, userProfileR2dbcDao);
    }
}
