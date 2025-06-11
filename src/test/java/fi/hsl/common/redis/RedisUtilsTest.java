package fi.hsl.common.redis;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import redis.clients.jedis.Jedis;

import java.util.Base64;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class RedisUtilsTest {
    private static final int TTL_SECONDS = 5;

    @Rule
    public GenericContainer redis = new GenericContainer(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    private Jedis jedis;
    private RedisUtils redisUtils;

    @Before
    public void setup() {
        jedis = new Jedis(redis.getHost(), redis.getFirstMappedPort());
        jedis.connect();

        redisUtils = new RedisUtils(jedis, TTL_SECONDS);
    }

    @After
    public void teardown() {
        jedis.close();
    }

    @Test
    public void testSetAndGet() {
        redisUtils.setValue("test", "abc");

        assertEquals("abc", redisUtils.getValue("test").get());
    }

    @Test
    public void testSetGetExpiringValue() throws InterruptedException {
        redisUtils.setExpiringValue("test", "expiring_value");

        assertEquals("expiring_value", redisUtils.getValue("test").get());

        Thread.sleep(500 + 1000 * TTL_SECONDS);

        assertFalse(redisUtils.getValue("test").isPresent());
    }

    @Test
    public void extractUsernameFromTokenBase64PaddingWorks() {
        // Payload with length not a multiple of 4 (e.g., 2 or 3 mod 4)
        String header = Base64.getUrlEncoder().withoutPadding().encodeToString("{\"alg\":\"none\"}".getBytes());

        // 2 mod 4 length
        String payload2 = Base64.getUrlEncoder().withoutPadding().encodeToString("{\"oid\":\"ab\"}".getBytes());
        String token2 = header + "." + payload2 + ".sig";
        assertEquals("ab", RedisUtils.extractUsernameFromToken(token2));

        // 3 mod 4 length
        String payload3 = Base64.getUrlEncoder().withoutPadding().encodeToString("{\"oid\":\"abc\"}".getBytes());
        String token3 = header + "." + payload3 + ".sig";
        assertEquals("abc", RedisUtils.extractUsernameFromToken(token3));
    }
}
