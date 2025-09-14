package fi.hsl.common.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.Response;
import redis.clients.jedis.params.ScanParams;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class RedisStore {

    private final JedisSentinelPool pool;

    public RedisStore(JedisSentinelPool pool) {
        this.pool = pool;
    }

    public String setValue(String key, String value) {
        return execute(jedis -> jedis.set(key, value));
    }

    public String setExpiringValue(String key, String value, Duration ttl) {
        return execute(jedis -> jedis.setex(key, ttl.toSeconds(), value));
    }

    public String setValues(String key, Map<String, String> values) {
        return execute(jedis -> jedis.hmset(key, values));
    }

    public String setExpiringValues(String key, Map<String, String> values, Duration ttl) {
        var response = this.setValues(key, values);
        setExpire(key, ttl);
        return response;
    }

    public Long setExpire(String key, Duration ttl) {
        return execute(jedis -> jedis.expire(key, ttl.toSeconds()));
    }

    public Optional<String> getValue(String key) {
        return execute(jedis -> {
            var value = jedis.get(key);
            return value != null && !value.isEmpty() ? Optional.of(value) : Optional.empty();
        });
    }

    public Optional<Map<String, String>> getValues(String key) {
        return execute(jedis -> {
            Map<String, String> values = jedis.hgetAll(key);
            return values != null && !values.isEmpty() ? Optional.of(values) : Optional.empty();
        });
    }

    public List<String> getKeys(String prefix, Integer count) {
        return this.getKeys(prefix, "*", count);
    }

    public List<String> getKeys(String prefix, String pattern, Integer count) {
        var scanParams = new ScanParams();
        scanParams.match(prefix + pattern);
        scanParams.count(count);
        var keys = new HashSet<String>();

        return execute(jedis -> {
            var cursor = ScanParams.SCAN_POINTER_START;
            do {
                var scanResult = jedis.scan(cursor, scanParams);
                var result = scanResult.getResult();
                keys.addAll(result);
                cursor = scanResult.getCursor();
            } while (!"0".equals(cursor));

            return new ArrayList<>(keys);
        });
    }

    public Map<String, Optional<Map<String, String>>> getValuesByKeys(List<String> keys) {
        return execute(jedis -> {
            var transaction = jedis.multi();
            var responses = new HashMap<String, Response<Map<String, String>>>();
            keys.forEach((key) -> responses.put(key, transaction.hgetAll(key)));
            transaction.exec();
            var values = new HashMap<String, Optional<Map<String, String>>>(responses.size());
            responses.forEach((k, v) -> {
                var value = v.get();
                if (value != null && !value.isEmpty()) {
                    values.put(k, Optional.of(value));
                } else {
                    values.put(k, Optional.empty());
                }

            });
            return values;
        });
    }

    public Map<String, Optional<String>> getValueByKeys(List<String> keys) {
        return execute(jedis -> {
            var transaction = jedis.multi();
            var responses = new HashMap<String, Response<String>>();
            keys.forEach((key) -> responses.put(key, transaction.get(key)));
            transaction.exec();
            var values = new HashMap<String, Optional<String>>(responses.size());
            responses.forEach((k, v) -> {
                var value = v.get();
                if (value != null && !value.isEmpty()) {
                    values.put(k, Optional.of(value));
                } else {
                    values.put(k, Optional.empty());
                }

            });
            return values;
        });
    }

    public boolean checkResponse(String response) {
        return response != null && response.trim().equalsIgnoreCase("OK");
    }

    public boolean checkResponse(Long response) {
        return response != null && response == 1L;
    }

    public <T> T execute(Function<Jedis, T> action) {
        try (final var jedis = pool.getResource()) {
            return action.apply(jedis);
        }
    }

    public void close() {
        if (!pool.isClosed()) {
            pool.close();
        }
    }
}
