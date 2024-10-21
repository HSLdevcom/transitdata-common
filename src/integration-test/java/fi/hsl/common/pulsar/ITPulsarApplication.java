package fi.hsl.common.pulsar;

import com.typesafe.config.Config;
import fi.hsl.common.config.ConfigParser;
import fi.hsl.common.config.ConfigUtils;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.Producer;
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

    private static final String TENANT = "hsl";
    private static final String NAMESPACE = "transitdata";

    static final String CONFIG_FILE = "integration-test.conf";

    @ClassRule
    public static GenericContainer redis = MockContainers.newRedisContainer();

    @ClassRule
    public static PulsarContainer pulsar = MockContainers.newPulsarContainer();
    
    @Test
    public void testRedisContainer() {
        Jedis jedis = MockContainers.newMockJedisConnection(redis);
        jedis.set("key", "value");
        String value = jedis.get("key");
        assertEquals(value, "value");
    }

    @Test
    public void testPulsarApplicationRedis() throws Exception {
        Config config = ConfigParser.createConfig("test-redis-only.conf");
        assertNotNull(config);
        PulsarApplication app = PulsarMockApplication.newInstance(config, redis, pulsar);
        assertNotNull(app);

        app.getContext().getJedis().set("pulsar-application-jedis", "should work");
        String value = app.getContext().getJedis().get("pulsar-application-jedis");
        assertEquals(value, "should work");
    }


    @Test
    public void readConfig() {
        PulsarMockApplication.readConfig(CONFIG_FILE);
    }


    @Test
    public void testPulsar() throws Exception {
        Config base = PulsarMockApplication.readConfig(CONFIG_FILE);

        PulsarApplication app = PulsarMockApplication.newInstance(base, redis, pulsar);
        assertNotNull(app);

        logger.info("Pulsar Application created, testing to send a message");

        final String payload = "Test-message";

        Producer<byte[]> producer = app.getContext().getSingleProducer();
        producer.send(payload.getBytes());

        logger.info("Message sent, reading it back");

        Consumer<byte[]> consumer = app.getContext().getConsumer();
        readAndValidateMsg(consumer, new HashSet<>(Arrays.asList(payload)));

        Jedis jedis = app.getContext().getJedis();

        assertTrue(consumer.isConnected());
        assertTrue(producer.isConnected());
        assertTrue(jedis.isConnected());

        app.close();

        assertFalse(consumer.isConnected());
        assertFalse(producer.isConnected());
        assertFalse(jedis.isConnected());

    }

    public static String formatTopicName(String topic) {
        return "persistent://" + TENANT + "/" + NAMESPACE + "/" + topic;
    }

    @Test
    public void testPulsarWithMultipleTopics() throws Exception {
        Map<String, Object> o1 = new HashMap<>();
        o1.put("pulsar.consumer.enabled", false);
        o1.put("redis.enabled", false);
        o1.put("pulsar.producer.multipleProducers", true);
        String topic1 = formatTopicName("test-1");
        String topic2 = formatTopicName("test-2");
        o1.put("pulsar.producer.topics", String.join(",",topic1 , topic2));
        o1.put("pulsar.producer.topicKeys",topic1 + "=test-1," + topic2 + "=test-2");
        Config producer1Config = PulsarMockApplication.readConfigWithOverrides("integration-multiprod-test.conf", o1);

        PulsarApplication app = PulsarMockApplication.newInstance(producer1Config, redis, pulsar);
        assertNotNull(app);

        Producer<byte[]> producer = app.getContext().getProducers().get("test-1");

        //Create a second producer but bind into different topic

        Producer<byte[]> secondProducer = app.getContext().getProducers().get("test-2");

        logger.info("Multi-topic Pulsar Application created, testing to send a message");

        //Next create the consumer
        Map<String, Object> overrides = new HashMap<>();
        overrides.put("pulsar.consumer.multipleTopics", true);
        overrides.put("pulsar.consumer.topicsPattern", formatTopicName("test-(1|2)"));
        Config consumerConfig = PulsarMockApplication.readConfigWithOverrides("integration-multiprod-test.conf", overrides);
        Consumer<byte[]> consumer = app.createConsumer(app.client, consumerConfig);

        logger.debug("Consumer topic: " + consumer.getTopic());

        final String firstPayload = "to-topic1";
        producer.send(firstPayload.getBytes());

        final String secondPayload = "to-topic2";
        secondProducer.send(secondPayload.getBytes());

        Set<String> correctPayloads = new HashSet<>(Arrays.asList(firstPayload, secondPayload));
        readAndValidateMsg(consumer, correctPayloads);

        secondProducer.close();
        app.close();
    }

    private void readAndValidateMsg(Consumer<byte[]> consumer, Set<String> correctPayloads) throws Exception {
        logger.info("Reading messages from Pulsar");
        Set<String> received = new HashSet<>();
        //Pulsar consumer doesn't guarantee in which order messages come when reading multiple topics.
        //They should be in order when reading from the same topic.
        while (received.size() < correctPayloads.size()) {
            Message<byte[]> msg = consumer.receive(5, TimeUnit.SECONDS);
            assertNotNull(msg);
            String receivedPayload = new String(msg.getData(), Charset.defaultCharset());
            logger.info("Received: " + receivedPayload);
            received.add(receivedPayload);
        }
        assertEquals(correctPayloads, received);
    }

    @Test
    public void testPulsarAutoClose() throws Exception {
        Config base = PulsarMockApplication.readConfig(CONFIG_FILE);

        Producer<byte[]> producer;
        Consumer<byte[]> consumer;
        Jedis jedis;
        try(PulsarApplication app = PulsarMockApplication.newInstance(base, redis, pulsar)) {
            logger.info("Pulsar Application created within try-with-resources-block");
            assertNotNull(app);

            producer = app.getContext().getSingleProducer();
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
        Config config = PulsarMockApplication.readConfigWithOverrides(CONFIG_FILE, invalid);
        testInitFailure(config);
    }

    @Test
    public void testInitFailureOnPulsar() {
        Map<String, Object> invalid = new HashMap<>();
        invalid.put("pulsar.producer.topic", "illegal://topic name");
        Config config = PulsarMockApplication.readConfigWithOverrides(CONFIG_FILE, invalid);
        testInitFailure(config);
    }

    @Test
    public void testInitFailureOnInvalidTopicsPattern() {
        Map<String, Object> overrides = new HashMap<>();
        overrides.put("pulsar.consumer.multipleTopics", true);
        overrides.put("pulsar.consumer.topicsPattern", "?transitdata/pubtrans/departure"); // ? is invalid in this regex
        Config config = PulsarMockApplication.readConfigWithOverrides(CONFIG_FILE, overrides);
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
