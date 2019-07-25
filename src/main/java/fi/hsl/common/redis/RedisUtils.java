package fi.hsl.common.redis;

import fi.hsl.common.pulsar.PulsarApplicationContext;
import fi.hsl.common.transitdata.TransitdataProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import javax.swing.text.html.Option;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class RedisUtils {
    private static final Logger log = LoggerFactory.getLogger(RedisUtils.class);

    private static RedisUtils instance;
    public static Jedis jedis;
    public static int ttlSeconds;

    public static RedisUtils newInstance(final PulsarApplicationContext context) {
        if (instance == null) {
            instance = new RedisUtils(context);
        }
        return instance;
    }

    private RedisUtils(final PulsarApplicationContext context) {
        jedis = context.getJedis();
        ttlSeconds = context.getConfig().getInt("redis.ttlSeconds");
        log.info("Redis TTL: {} seconds", ttlSeconds);
    }

    public String setValue(final String key, final String value) {
        return jedis.set(key, value);
    }

    public String setExpiringValue(final String key, final String value) {
        return setExpiringValue(key, value, ttlSeconds);
    }

    public String setExpiringValue(final String key, final String value, final int ttlInSeconds) {
        return jedis.setex(key, ttlInSeconds, value);
    }

    public String setValues(final String key, final Map<String, String> values) {
        return jedis.hmset(key, values);
    }

    public String setExpiringValues(final String key, final Map<String, String> values) {
        return setExpiringValues(key, values, ttlSeconds);
    }

    public String setExpiringValues(final String key, final Map<String, String> values, final int ttlInSeconds) {
        String response = setValues(key, values);
        // TODO: what if HMSET succeeds but EXPIRE does not?
        setExpire(key, ttlInSeconds);
        return response;
    }

    public Long setExpire(final String key) {
        return setExpire(key, ttlSeconds);
    }

    public Long setExpire(final String key, final int ttlInSeconds) {
        return jedis.expire(key, ttlInSeconds);
    }

    public Optional<String> getValue(final String key) {
        final String value = jedis.get(key);
        if (value.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(value);
    }

    public Optional<Map<String, String>> getValues(final String key) {
        final Map<String, String> values = jedis.hgetAll(key);
        if (values.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(values);
    }

    /**
     * Fetches all keys that start with prefix
     * @param prefix
     * @param count Approximate/maximum number of keys
     * @return ArrayList of matching keys
     */
    public List<String> getKeys(final String prefix, final Integer count) {
        return getKeys(prefix, "*", count);
    }

    /**
     * Fetches all keys that match prefix + pattern
     * @param prefix
     * @param pattern
     * @param count Approximate/maximum number of keys
     * @return ArrayList of matching keys
     */
    public List<String> getKeys(final String prefix, final String pattern, final Integer count) {
        ScanParams scanParams = new ScanParams();
        scanParams.match(prefix + pattern);
        scanParams.count(count);
        String cursor = ScanParams.SCAN_POINTER_START;

        HashSet<String> keys = new HashSet<>();
        do {
            ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
            List<String> result = scanResult.getResult();
            keys.addAll(result);
            cursor = scanResult.getStringCursor();
        } while(!"0".equals(cursor));

        return new ArrayList<>(keys);
    }

    /**
     * Fetches hash values for keys
     * @param keys
     * @return HashMap of keys and their hash values if they exist
     */
    public Map<String, Optional<Map<String, String>>> getValuesByKeys(final List<String> keys) {
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

    /**
     * Fetches string values for keys
     * @param keys
     * @return HashMap of keys and their values if they exist
     */
    public Map<String, Optional<String>> getValueBykeys(final List<String> keys) {
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

    public String updateTimestamp() {
        final OffsetDateTime now = OffsetDateTime.now();
        final String ts = DateTimeFormatter.ISO_INSTANT.format(now);
        log.info("Updating Redis timestamp to {}", ts);
        return jedis.set(TransitdataProperties.KEY_LAST_CACHE_UPDATE_TIMESTAMP, ts);
    }

    public boolean checkResponse(final String response) {
        return response != null && response.trim().equalsIgnoreCase("OK");
    }

    public boolean checkResponse(final Long response) {
        return response != null && response == 1;
    }
}
