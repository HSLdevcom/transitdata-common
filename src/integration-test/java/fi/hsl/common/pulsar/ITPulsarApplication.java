package fi.hsl.common.pulsar;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import fi.hsl.common.config.ConfigParser;
import fi.hsl.common.config.ConfigUtils;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminBuilder;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.common.policies.data.TenantInfo;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PulsarContainer;
import redis.clients.jedis.Jedis;

import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class ITPulsarApplication {

    static final Logger logger = LoggerFactory.getLogger(ITPulsarApplication.class);

    static final boolean PRINT_PULSAR_LOG = ConfigUtils.getEnv("PRINT_PULSAR_LOG").map(Boolean::parseBoolean).orElse(false);

    @ClassRule
    public static GenericContainer redis = MockContainers.newRedisContainer();

    @ClassRule
    public static PulsarContainer pulsar = MockContainers.newPulsarContainer();

    private static final String TENANT = "hsl";
    private static final String NAMESPACE = "transitdata";

    @BeforeClass
    public static void setUp() throws Exception {
        if (PRINT_PULSAR_LOG) {
            MockContainers.tail(pulsar, logger);
        }

        PulsarAdmin admin = PulsarAdmin.builder()
                .serviceHttpUrl(pulsar.getHttpServiceUrl())
                .build();

        TenantInfo info = new TenantInfo();
        Set<String> clusters = new HashSet<>(Arrays.asList("standalone"));
        info.setAllowedClusters(clusters);
        info.setAdminRoles(new HashSet<>(Arrays.asList("all")));
        admin.tenants().createTenant(TENANT, info);

        admin.namespaces().createNamespace(TENANT + "/" + NAMESPACE, clusters);
        logger.info("Pulsar setup done");
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

    private Config defaultConfigWithOverride(String key, Object value) {
        Map<String, Object> overrides = new HashMap<>();
        overrides.put(key, value);
        return defaultConfigWithOverrides(overrides);
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
        readAndValidateMsg(consumer, payload);

        Jedis jedis = app.getContext().getJedis();

        assertTrue(consumer.isConnected());
        assertTrue(producer.isConnected());
        assertTrue(jedis.isConnected());

        app.close();

        assertFalse(consumer.isConnected());
        assertFalse(producer.isConnected());
        assertFalse(jedis.isConnected());

    }

/*
    @Test
    public void testPulsarWithMultipleTopics() throws Exception {

        Map<String, Object> overrides = new HashMap<>();
        overrides.put("pulsar.consumer.multipleTopics", true);
        overrides.put("pulsar.consumer.topicsPattern", "persistent://hsl/transitdata/test-.*");
        overrides.put("pulsar.producer.topic", "persistent://hsl/transitdata/test-first");

        Config config = defaultConfigWithOverrides(overrides);

        PulsarApplication app = PulsarMockApplication.newInstance(config, redis, pulsar);
        assertNotNull(app);

        Producer<byte[]> producer = app.getContext().getProducer();

        //Create a second producer but bind into different topic
        Config producerConfig = defaultConfigWithOverride("pulsar.producer.topic", "persistent://hsl/transitdata/test-second");
        Producer<byte[]> secondProducer = app.createProducer(app.client, producerConfig);

        logger.info("Multi-topic Pulsar Application created, testing to send a message");
        Consumer<byte[]> consumer = app.getContext().getConsumer();

        final String firstPayload = "to-topic1";
        producer.send(firstPayload.getBytes());
        readAndValidateMsg(consumer, firstPayload);

        final String secondPayload = "to-topic2";
        secondProducer.send(secondPayload.getBytes());
        readAndValidateMsg(consumer, secondPayload);

        secondProducer.close();
        app.close();
    }*/

    private void readAndValidateMsg(Consumer<byte[]> consumer, String correctPayload) throws Exception {
        logger.info("Reading a message from Pulsar");
        Message<byte[]> msg = consumer.receive(5, TimeUnit.SECONDS);
        assertNotNull(msg);
        String received = new String(msg.getData(), Charset.defaultCharset());
        logger.info("Received: " + received);
        assertEquals(correctPayload, received);
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
    public void testInitFailureOnRedis() {
        Map<String, Object> invalid = new HashMap<>();
        invalid.put("redis.port", 9999);
        Config config = defaultConfigWithOverrides(invalid);
        testInitFailure(config);
    }

    @Test
    public void testInitFailureOnPulsar() {
        Map<String, Object> invalid = new HashMap<>();
        invalid.put("pulsar.producer.topic", "illegal://topic name");
        Config config = defaultConfigWithOverrides(invalid);
        testInitFailure(config);
    }

    @Test
    public void testInitFailureOnInvalidTopicsPattern() {
        Map<String, Object> overrides = new HashMap<>();
        overrides.put("pulsar.consumer.multipleTopics", true);
        overrides.put("pulsar.consumer.topicsPattern", "?transitdata/pubtrans/departure"); // ? is invalid in this regex
        Config config = defaultConfigWithOverrides(overrides);
        testInitFailure(config);
    }

    public void testInitFailure(Config config) {
        try(PulsarApplication app = PulsarMockApplication.newInstance(config, redis, pulsar)) {
            logger.info("You should never see this message, init should throw an exception");
            assertTrue(false);
        }
        catch (Exception e) {
            logger.debug("Exception as expected");
        }
    }
}
