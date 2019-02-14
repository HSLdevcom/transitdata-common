package fi.hsl.common.pulsar;

import com.typesafe.config.Config;
import fi.hsl.common.config.ConfigUtils;
import fi.hsl.common.transitdata.TransitdataProperties;
import org.apache.pulsar.client.api.*;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PulsarContainer;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * This class provides a convenient way to run integration tests to one
 * single IMessageHandler by creating context for sending messages to Pulsar and
 * letting the IMessageHandler create a corresponding output.
 *
 * This suite works if your application uses the standard pattern
 *  app = PulsarApplication.newInstance(...)
 *  app.launchWithHandler(handlerImpl);
 *
 * Configure the source-producer and sink-consumer via config files in resources.
 */
public class ITBaseTestSuite {

    static final Logger logger = LoggerFactory.getLogger(ITBaseTestSuite.class);

    static final boolean PRINT_PULSAR_LOG = ConfigUtils.getEnv("PRINT_PULSAR_LOG").map(Boolean::parseBoolean).orElse(false);

    private static final String TENANT = "hsl";
    private static final String NAMESPACE = "transitdata";

    protected int recvTimeoutMs = 500;
    public static final long DEFAULT_TEST_TIMEOUT_MS = 10000;

    protected String sourceConfigFilename = "integration-test-source.conf";
    protected String sinkConfigFilename = "integration-test-sink.conf";
    protected String processorConfigFilename = "integration-test-processor.conf";

    @ClassRule
    public static PulsarContainer pulsar = MockContainers.newPulsarContainer();

    @BeforeClass
    public static void setUp() throws Exception {
        MockContainers.configurePulsarContainer(pulsar, TENANT, NAMESPACE);

        if (PRINT_PULSAR_LOG) {
            MockContainers.tail(pulsar, logger);
        }
    }

    protected PulsarApplication createPulsarApp(String config, String testId) throws Exception {
        logger.info("Creating Pulsar Application for config " + config);

        Config base = PulsarMockApplication.readConfigWithTopicOverrides(config, testId);
        assertNotNull(base);
        // No Redis atm. TODO add later if needed
        PulsarApplication app = PulsarMockApplication.newInstance(base, null, pulsar);
        assertNotNull(app);
        return app;
    }


    protected Message<byte[]> readOutputMessage(TestContext context) throws PulsarClientException {
        //Our Pipeline throughput should be few milliseconds but let's not assume it here, can create unwanted assertions.
        //Rather test performance separately with load tests.
        Message<byte[]> received = context.sink.receive(recvTimeoutMs, TimeUnit.MILLISECONDS);
        if (received != null) {
            context.sink.acknowledge(received);
        }
        return received;
    }


    public static class TestContext {
        public Producer<byte[]> source;
        public Consumer<byte[]> sink;
        public PulsarApplication testApp;
    }

    public static abstract class TestLogic {
        public void test(TestContext context) {
            try {
                testImpl(context);
            }
            catch (Exception e) {
                logger.error("Test failed!", e);
                assertTrue(false);
            }
        }

        //Perform your test and assertions in here. Send via source, read via sink.
        abstract void testImpl(TestContext context) throws Exception;
    }

    public void validatePulsarProperties(Message<byte[]> received, String expectedKey, long expectedTime, TransitdataProperties.ProtobufSchema expectedSchema) {
        assertEquals(expectedSchema.toString(), received.getProperty(TransitdataProperties.KEY_PROTOBUF_SCHEMA));
        assertEquals(expectedKey, received.getKey());
        assertEquals(expectedTime, received.getEventTime());

    }

    public void testPulsarMessageHandler(IMessageHandler handler, TestLogic logic, String testId) throws Exception {
        testPulsarMessageHandler(handler, logic, testId, DEFAULT_TEST_TIMEOUT_MS);
    }

    public void testPulsarMessageHandler(IMessageHandler handler, TestLogic logic, String testId, long testTimeoutMs) throws Exception {

        logger.info("Initializing test resources");
        PulsarApplication sourceApp = createPulsarApp(sourceConfigFilename, testId);
        Producer<byte[]> source = sourceApp.getContext().getProducer();
        assertNotNull(source);
        assertTrue(source.isConnected());

        PulsarApplication sinkApp = createPulsarApp(sinkConfigFilename, testId);
        Consumer<byte[]> sink = sinkApp.getContext().getConsumer();
        assertNotNull(sink);
        assertTrue(sink.isConnected());

        PulsarApplication testApp = createPulsarApp(processorConfigFilename, testId);

        TestContext context = new TestContext();
        context.sink = sink;
        context.source = source;
        context.testApp = testApp;

        // This is the "Main"-thread of our IMessageHandler
        Thread t = new Thread() {
            public void run() {
                try {
                    testApp.launchWithHandler(handler);
                }
                catch (Exception e) {
                    //This is expected after test is closed
                    logger.info("Pulsar application throws, as expected. " + e.getMessage());
                }
            }
        };
        t.start();

        logger.info("Test setup done, calling test method");
        // Actual test here:
        logic.test(context);

        logger.info("Test done, all good");

        testApp.close(); // This exits the thread-loop above also
        t.join(testTimeoutMs); // Wait for thread to exit. Use timeout to prevent deadlock for whole the whole test class.
        assertFalse(t.isAlive());

        logger.info("Pulsar read thread finished");

        sourceApp.close();
        sinkApp.close();
        assertFalse(source.isConnected());
        assertFalse(sink.isConnected());
    }


    public static void sendPulsarMessage(Producer<byte[]> producer, String key, long eventTime,
                                         byte[] payload, TransitdataProperties.ProtobufSchema schema,
                                         Map<String, String> properties) throws PulsarClientException {
        TypedMessageBuilder<byte[]> builder = producer.newMessage().value(payload);

        builder.eventTime(eventTime);
        if (key != null)
            builder.key(key);
        if (schema != null)
            builder.property(TransitdataProperties.KEY_PROTOBUF_SCHEMA, schema.toString());
        if (properties != null)
            properties.forEach(builder::property);

        builder.send();
    }
}
