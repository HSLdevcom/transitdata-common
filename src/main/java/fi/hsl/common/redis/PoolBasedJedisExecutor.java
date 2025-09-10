package fi.hsl.common.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

import java.util.function.Function;

import static redis.clients.jedis.Protocol.DEFAULT_DATABASE;

public class PoolBasedJedisExecutor implements JedisExecutor {

    private final JedisSentinelPool pool;

    public PoolBasedJedisExecutor(RedisClusterProperties properties) {
        this.pool = new JedisSentinelPool(
                properties.masterName,
                properties.sentinels,
                properties.jedisPoolConfig(),
                (int) properties.connectionTimeout.toMillis(),
                (int) properties.socketTimeout.toMillis(),
                null,
                DEFAULT_DATABASE
        );
    }

    @Override
    public <T> T execute(Function<Jedis, T> action) {
        try (final var jedis = pool.getResource()) {
            return action.apply(jedis);
        }
    }
}
