package fi.hsl.common.pulsar;

import com.typesafe.config.Config;
import fi.hsl.common.config.ConfigUtils;
import fi.hsl.common.health.HealthServer;
import fi.hsl.common.redis.RedisStore;
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

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

    @BeforeClass
    public static void setUp() throws Exception {
        MockContainers.configurePulsarContainer(pulsar, TENANT, NAMESPACE);

        if (PRINT_PULSAR_LOG) {
            MockContainers.tail(pulsar, logger);
        }
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
        assertNotNull(producer);
        producer.send(payload.getBytes());

        logger.info("Message sent, reading it back");

        Consumer<byte[]> consumer = app.getContext().getConsumer();
        readAndValidateMsg(consumer, new HashSet<>(List.of(payload)));

        assertNotNull(consumer);
        assertTrue(consumer.isConnected());
        assertTrue(producer.isConnected());

        app.close();

        assertFalse(consumer.isConnected());
        assertFalse(producer.isConnected());
    }

    public static String formatTopicName(String topic) {
        return "persistent://" + TENANT + "/" + NAMESPACE + "/" + topic;
    }

    @Test
    public void testPulsarWithMultipleTopics() throws Exception {
        Map<String, Object> o1 = new HashMap<>();
        o1.put("pulsar.consumer.enabled", false);
        o1.put("redisCluster.enabled", false);
        o1.put("pulsar.producer.multipleProducers", true);
        String topic1 = formatTopicName("test-1");
        String topic2 = formatTopicName("test-2");
        o1.put("pulsar.producer.topics", String.join(",", topic1, topic2));
        o1.put("pulsar.producer.topicKeys", topic1 + "=test-1," + topic2 + "=test-2");
        Config producer1Config = PulsarMockApplication.readConfigWithOverrides("integration-multiprod-test.conf", o1);

        PulsarApplication app = PulsarMockApplication.newInstance(producer1Config, redis, pulsar);
        assertNotNull(app);

        assertNotNull(app.getContext().getProducers());
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
        try (PulsarApplication app = PulsarMockApplication.newInstance(base, redis, pulsar)) {
            logger.info("Pulsar Application created within try-with-resources-block");
            assertNotNull(app);

            producer = app.getContext().getSingleProducer();
            assertNotNull(producer);
            assertTrue(producer.isConnected());

            consumer = app.getContext().getConsumer();
            assertNotNull(consumer);
            assertTrue(consumer.isConnected());
        }

        logger.info("Pulsar Application out of scope, all connections should be closed");

        assertFalse(consumer.isConnected());
        assertFalse(producer.isConnected());
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
        try (PulsarApplication app = PulsarMockApplication.newInstance(config, redis, pulsar)) {
            logger.info("You should never see this message, init should throw an exception");
            fail();
        } catch (Exception e) {
            logger.debug("Exception as expected");
        }
    }

    @Test
    public void testHttpServer() throws Exception {
        Config base = PulsarMockApplication.readConfig(CONFIG_FILE);

        PulsarApplication app = PulsarMockApplication.newInstance(base, redis, pulsar);
        assertNotNull(app);

        logger.info("Pulsar Application created, testing HealthServer");

        final Producer<byte[]> producer = app.getContext().getSingleProducer();
        final Consumer<byte[]> consumer = app.getContext().getConsumer();
        final HealthServer healthServer = app.getContext().getHealthServer();

        assertNotNull(consumer);
        assertTrue(consumer.isConnected());
        assertNotNull(producer);
        assertTrue(producer.isConnected());

        logger.info("Creating health check function");
        final BooleanSupplier healthCheck = () -> {
            boolean status = true;
            status &= producer.isConnected();
            status &= consumer.isConnected();
            return status;
        };
        assertNotNull(healthServer);
        healthServer.addCheck(healthCheck);

        String url = "http://localhost:" + healthServer.port + healthServer.endpoint;

        logger.info("Checking health");
        HttpResponse<String> response = makeGetRequest(url);
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
        assertEquals("OK", getContent(response));

        response = makeGetRequest(url);
        assertEquals(HttpURLConnection.HTTP_UNAVAILABLE, response.statusCode());
        assertEquals("FAIL", getContent(response));

        response = makeGetRequest(url);
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
        assertEquals("OK", getContent(response));

        logger.info("Closing Pulsar consumer and checking health");
        consumer.close();
        assertFalse(consumer.isConnected());

        response = makeGetRequest(url);
        assertEquals(HttpURLConnection.HTTP_UNAVAILABLE, response.statusCode());
        assertEquals("FAIL", getContent(response));

        response = makePostRequest(url);
        assertEquals(HttpURLConnection.HTTP_BAD_METHOD, response.statusCode());
        assertEquals("Method Not Allowed", getContent(response));

        url = "http://localhost:" + healthServer.port + "/foo";
        response = makeGetRequest(url);
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, response.statusCode());
        assertEquals("Not Found", getContent(response));

        url = "http://localhost:" + healthServer.port + healthServer.endpoint + "foo";
        response = makeGetRequest(url);
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, response.statusCode());
        assertEquals("Not Found", getContent(response));

        app.close();
        assertFalse(consumer.isConnected());
        assertFalse(producer.isConnected());
    }

    private HttpResponse<String> makeGetRequest(final String url) {
        return makeRequest("GET", url);
    }

    private HttpResponse<String> makePostRequest(final String url) {
        return makeRequest("POST", url);
    }

    private HttpResponse<String> makeRequest(final String method, final String url) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request;
        if (method.equalsIgnoreCase("post")) {
            request = HttpRequest.newBuilder(URI.create(url)).POST(HttpRequest.BodyPublishers.noBody()).build();
        } else {
            request = HttpRequest.newBuilder(URI.create(url)).GET().build();
        }
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getContent(final HttpResponse<String> response) {
        return response.body();
    }
}
