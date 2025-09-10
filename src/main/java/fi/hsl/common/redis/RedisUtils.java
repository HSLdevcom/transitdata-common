package fi.hsl.common.redis;

import fi.hsl.common.pulsar.PulsarApplicationContext;
import fi.hsl.common.transitdata.TransitdataProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Use {@link RedisStore}
 */
@Deprecated(forRemoval = true)
public class RedisUtils {
    private static final Logger log = LoggerFactory.getLogger(RedisUtils.class);

    private static RedisUtils instance;
    public static Jedis jedis;
    public static int ttlSeconds;

    @NotNull
    public static RedisUtils newInstance(@NotNull final PulsarApplicationContext context) {
        if (instance == null) {
            instance = new RedisUtils(context);
        }
        return instance;
    }

    /**
     * Creates RedisUtils with specified jedis instance.
     *
     * This constructor should only be used for testing.
     *
     * @param jedis
     * @param ttlSeconds
     */
    RedisUtils(@NotNull final Jedis jedis, final int ttlSeconds) {
        this.jedis = jedis;
        this.ttlSeconds = ttlSeconds;
        log.info("Redis TTL: {} seconds", ttlSeconds);
    }

    private RedisUtils(@NotNull final PulsarApplicationContext context) {
        this(context.getJedis(), context.getConfig().getInt("redis.ttlSeconds"));
    }

    @NotNull
    public String setValue(@NotNull final String key, @NotNull final String value) {
        synchronized (jedis) {
            return jedis.set(key, value);
        }
    }

    @NotNull
    public String setExpiringValue(@NotNull final String key, @NotNull final String value) {
        return setExpiringValue(key, value, ttlSeconds);
    }

    @NotNull
    public String setExpiringValue(@NotNull final String key, @NotNull final String value, final int ttlInSeconds) {
        synchronized (jedis) {
            return jedis.setex(key, ttlInSeconds, value);
        }
    }

    @NotNull
    public String setValues(@NotNull final String key, @NotNull final Map<@NotNull String, @NotNull String> values) {
        synchronized (jedis) {
            return jedis.hmset(key, values);
        }
    }

    @NotNull
    public String setExpiringValues(@NotNull final String key, @NotNull final Map<@NotNull String, @NotNull String> values) {
        return setExpiringValues(key, values, ttlSeconds);
    }

    @NotNull
    public String setExpiringValues(@NotNull final String key, @NotNull final Map<@NotNull String, @NotNull String> values, final int ttlInSeconds) {
        String response = setValues(key, values);
        // TODO: what if HMSET succeeds but EXPIRE does not?
        setExpire(key, ttlInSeconds);
        return response;
    }

    @NotNull
    public Long setExpire(@NotNull final String key) {
        return setExpire(key, ttlSeconds);
    }

    @NotNull
    public Long setExpire(@NotNull final String key, final int ttlInSeconds) {
        synchronized (jedis) {
            return jedis.expire(key, ttlInSeconds);
        }
    }

    public Optional<String> getValue(@NotNull final String key) {
        synchronized (jedis) {
            final String value = jedis.get(key);
            if (value == null || value.isEmpty()) {
                return Optional.empty();
            }
            return Optional.ofNullable(value);
        }
    }

    public Optional<Map<@NotNull String, @NotNull String>> getValues(@NotNull final String key) {
        synchronized (jedis) {
            final Map<String, String> values = jedis.hgetAll(key);
            if (values == null || values.isEmpty()) {
                return Optional.empty();
            }
            return Optional.ofNullable(values);
        }
    }

    /**
     * Fetches all keys that start with prefix
     * @param prefix
     * @param count Approximate/maximum number of keys
     * @return ArrayList of matching keys
     */
    @NotNull
    public List<@NotNull String> getKeys(@NotNull final String prefix, @NotNull final Integer count) {
        return getKeys(prefix, "*", count);
    }

    /**
     * Fetches all keys that match prefix + pattern
     * @param prefix
     * @param pattern
     * @param count Approximate/maximum number of keys
     * @return ArrayList of matching keys
     */
    @NotNull
    public List<String> getKeys(@NotNull final String prefix, @NotNull final String pattern, @NotNull final Integer count) {
        ScanParams scanParams = new ScanParams();
        scanParams.match(prefix + pattern);
        scanParams.count(count);
        String cursor = ScanParams.SCAN_POINTER_START;

        HashSet<String> keys = new HashSet<>();
        synchronized (jedis) {
            do {
                ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                List<String> result = scanResult.getResult();
                keys.addAll(result);
                cursor = scanResult.getCursor();
            } while(!"0".equals(cursor));

            return new ArrayList<>(keys);
        }
    }

    /**
     * Fetches hash values for keys
     * @param keys
     * @return HashMap of keys and their hash values if they exist
     */
    @NotNull
    public Map<@NotNull String, Optional<Map<@NotNull String, @NotNull String>>> getValuesByKeys(@NotNull final List<@NotNull String> keys) {
        synchronized (jedis) {
            final Transaction transaction = jedis.multi();
            final Map<String, Response<Map<String, String>>> responses = new HashMap<>();
            keys.forEach(key -> responses.put(key, transaction.hgetAll(key)));
            transaction.exec();

            final Map<String, Optional<Map<String, String>>> values = new HashMap<>(responses.size());
            responses.forEach((k, v) -> {
                final Map<String, String> value = v.get();
                if (value == null || value.isEmpty()) {
                    values.put(k, Optional.empty());
                } else {
                    values.put(k, Optional.of(value));
                }
            });

            return values;
        }
    }

    /**
     * Fetches string values for keys
     * @param keys
     * @return HashMap of keys and their values if they exist
     */
    @NotNull
    public Map<@NotNull String, Optional<String>> getValueBykeys(@NotNull final List<@NotNull String> keys) {
        synchronized (jedis) {
            final Transaction transaction = jedis.multi();
            final Map<String, Response<String>> responses = new HashMap<>();
            keys.forEach(key -> responses.put(key, transaction.get(key)));
            transaction.exec();

            final Map<String, Optional<String>> values = new HashMap<>(responses.size());
            responses.forEach((k, v) -> {
                final String value = v.get();
                if (value == null || value.isEmpty()) {
                    values.put(k, Optional.empty());
                } else {
                    values.put(k, Optional.of(value));
                }
            });

            return values;
        }
    }

    @NotNull
    public String updateTimestamp() {
        synchronized (jedis) {
            final OffsetDateTime now = OffsetDateTime.now();
            final String ts = DateTimeFormatter.ISO_INSTANT.format(now);
            log.info("Updating Redis timestamp to {}", ts);
            return jedis.set(TransitdataProperties.KEY_LAST_CACHE_UPDATE_TIMESTAMP, ts);
        }
    }

    public boolean checkResponse(@Nullable final String response) {
        return response != null && response.trim().equalsIgnoreCase("OK");
    }

    public boolean checkResponse(@Nullable final Long response) {
        return response != null && response == 1;
    }
}
