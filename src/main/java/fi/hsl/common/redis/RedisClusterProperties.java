package fi.hsl.common.redis;

import com.typesafe.config.Config;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.Set;
import java.util.function.Function;

import static fi.hsl.common.lang.Checks.checkNotEmpty;
import static fi.hsl.common.lang.Checks.checkRequired;
import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toSet;

public class RedisClusterProperties {

    private static final Duration DEFAULT_IDLE_CONNECTION_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration DEFAULT_MAX_WAIT = Duration.ofMillis(500);
    private static final int DEFAULT_MIN_IDLE_CONNECTIONS = 12;
    private static final int DEFAULT_MAX_CONNECTIONS = 16;
    private static final Duration DEFAULT_CONNECTION_TIMEOUT = Duration.ofSeconds(500);
    private static final Duration DEFAULT_SOCKET_TIMEOUT = Duration.ofSeconds(500);

    public final String masterName;
    public final Set<String> sentinels;
    public final boolean healthCheck;
    public final Duration idleConnectionTimeout;
    public final int minIdleConnections;
    public final int maxConnections;
    public final Duration maxWait;
    public final Duration connectionTimeout;
    public final Duration socketTimeout;

    private RedisClusterProperties(String masterName,
                                   Set<String> sentinels,
                                   @Nullable Boolean healthCheck,
                                   @Nullable Duration idleConnectionTimeout,
                                   @Nullable Integer minIdleConnections,
                                   @Nullable Integer maxConnections,
                                   @Nullable Duration maxWait,
                                   @Nullable Duration connectionTimeout,
                                   @Nullable Duration socketTimeout) {
        this.masterName = checkNotEmpty("masterName", masterName);
        this.sentinels = checkNotEmpty("sentinels", sentinels);
        this.healthCheck = ofNullable(healthCheck).orElse(false);
        this.idleConnectionTimeout = ofNullable(idleConnectionTimeout).orElse(DEFAULT_IDLE_CONNECTION_TIMEOUT);
        this.minIdleConnections = ofNullable(minIdleConnections).orElse(DEFAULT_MIN_IDLE_CONNECTIONS);
        this.maxConnections = ofNullable(maxConnections).orElse(DEFAULT_MAX_CONNECTIONS);
        this.maxWait = ofNullable(maxWait).orElse(DEFAULT_MAX_WAIT);
        this.connectionTimeout = ofNullable(connectionTimeout).orElse(DEFAULT_CONNECTION_TIMEOUT);
        this.socketTimeout = ofNullable(socketTimeout).orElse(DEFAULT_SOCKET_TIMEOUT);
    }

    public static RedisClusterProperties redisClusterProperties(Config config) {
        return new RedisClusterProperties(
                getRequired(config, "redisCluster.masterName", config::getString),
                stream(getRequired(config, "redisCluster.sentinels", config::getString)
                        .split(","))
                        .filter(not(StringUtils::isBlank))
                        .collect(toSet()),
                getNullable(config, "redisCluster.healthCheck", config::getBoolean),
                getNullable(config, "redisCluster.idleConnectionTimeout", config::getDuration),
                getNullable(config, "redisCluster.minIdleConnections", config::getInt),
                getNullable(config, "redisCluster.maxConnections", config::getInt),
                getNullable(config, "redisCluster.maxWait", config::getDuration),
                getNullable(config, "redisCluster.connectionTimeout", config::getDuration),
                getNullable(config, "redisCluster.socketTimeout", config::getDuration)
        );
    }

    public JedisPoolConfig jedisPoolConfig() {
        final JedisPoolConfig config = new JedisPoolConfig();
        config.setMinEvictableIdleTime(idleConnectionTimeout);
        config.setMinIdle(minIdleConnections);
        config.setMaxIdle(maxConnections);
        config.setMaxTotal(maxConnections);
        config.setTestOnBorrow(false);
        config.setTestOnCreate(false);
        config.setTestOnReturn(false);
        config.setTestWhileIdle(true);
        config.setMaxWait(maxWait);
        return config;
    }

    private static <T> T getRequired(Config config, String path, Function<String, T> f) {
        var value = config.hasPath(path) ? f.apply(path) : null;
        checkRequired(path, value);
        return value;
    }

    private static <T> T getNullable(Config config, String path, Function<String, T> f) {
        return config.hasPath(path) ? f.apply(path) : null;
    }
}
