package fi.hsl.common.pulsar;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import fi.hsl.common.config.ConfigParser;
import fi.hsl.common.config.ConfigUtils;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.Producer;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PulsarContainer;
import redis.clients.jedis.Jedis;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class ITPulsarApplication {

    static final Logger logger = LoggerFactory.getLogger(ITPulsarApplication.class);

    static final boolean PRINT_PULSAR_LOG = ConfigUtils.getEnv("PRINT_PULSAR_LOG").map(Boolean::parseBoolean).orElse(false);

    @ClassRule
    public static GenericContainer redis = MockContainers.newRedisContainer();

    @ClassRule
    public static PulsarContainer pulsar = MockContainers.newPulsarContainer();

    @BeforeClass
    public static void setUp() throws Exception {
        if (PRINT_PULSAR_LOG) {
            MockContainers.tail(pulsar, logger);
        }
    }

    @Test
    public void testRedis() {
        Jedis jedis = MockContainers.newMockJedisConnection(redis);
        jedis.set("key", "value");
        String value = jedis.get("key");
        assertEquals(value, "value");
    }

    @Test
    public void readConfig() {
        defaultConfig();
    }

    private Config defaultConfig() {
        Config config = ConfigParser.createConfig("integration-test.conf");
        assertNotNull(config);
        return config;
    }

    private Config defaultConfigWithOverrides(Map<String, Object> overrides) {
        Config configOverrides = ConfigFactory.parseMap(overrides);
        return ConfigParser.mergeConfigs(defaultConfig(), configOverrides);
    }

    @Test
    public void testPulsar() throws Exception {
        Config base = defaultConfig();

        PulsarApplication app = PulsarMockApplication.newInstance(base, redis, pulsar);
        assertNotNull(app);

        logger.info("Pulsar Application created, testing to send a message");

        final String payload = "Test-message";

        Producer<byte[]> producer = app.getContext().getProducer();
        producer.send(payload.getBytes());

        logger.info("Message sent, reading it back");

        Consumer<byte[]> consumer = app.getContext().getConsumer();
        Message<byte[]> msg = consumer.receive(5, TimeUnit.SECONDS);

        assertNotNull(msg);

        String received = new String(msg.getData(), Charset.defaultCharset());
        logger.info("Received: " + received);
        assertEquals(payload, received);

        Jedis jedis = app.getContext().getJedis();

        assertTrue(consumer.isConnected());
        assertTrue(producer.isConnected());
        assertTrue(jedis.isConnected());

        app.close();

        assertFalse(consumer.isConnected());
        assertFalse(producer.isConnected());
        assertFalse(jedis.isConnected());

    }

    @Test
    public void testPulsarAutoClose() throws Exception {
        Config base = defaultConfig();

        Producer<byte[]> producer;
        Consumer<byte[]> consumer;
        Jedis jedis;
        try(PulsarApplication app = PulsarMockApplication.newInstance(base, redis, pulsar)) {
            logger.info("Pulsar Application created within try-with-resources-block");
            assertNotNull(app);

            producer = app.getContext().getProducer();
            assertTrue(producer.isConnected());

            consumer = app.getContext().getConsumer();
            assertTrue(consumer.isConnected());

            jedis = app.getContext().getJedis();
            assertTrue(jedis.isConnected());
        }

        logger.info("Pulsar Application out of scope, all connections should be closed");

        assertFalse(consumer.isConnected());
        assertFalse(producer.isConnected());
        assertFalse(jedis.isConnected());

    }

    @Test
    public void testPulsarInitFailureOnRedis() {
        Map<String, Object> invalidAddress = new HashMap<>();
        invalidAddress.put("redis.port", 9999);
        Config base = defaultConfigWithOverrides(invalidAddress);

        boolean exception = false;
        try(PulsarApplication app = PulsarMockApplication.newInstance(base, redis, pulsar)) {
            logger.info("Pulsar Application should never be created because of redis port");
        }
        catch (Exception e) {
            exception = true;
            logger.info("Exception as expected");
        }
        assertTrue(exception);
    }
}
