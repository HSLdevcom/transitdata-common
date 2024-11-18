package fi.hsl.common.pulsar;

import com.typesafe.config.Config;
import fi.hsl.common.config.ConfigUtils;
import fi.hsl.common.transitdata.TransitdataProperties;
import org.apache.pulsar.client.api.*;
import org.junit.ClassRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PulsarContainer;

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

    protected static final Logger logger = LoggerFactory.getLogger(ITBaseTestSuite.class);

    static final boolean PRINT_PULSAR_LOG = ConfigUtils.getEnv("PRINT_PULSAR_LOG").map(Boolean::parseBoolean).orElse(false);

    private static final String TENANT = "hsl";
    private static final String NAMESPACE = "transitdata";

    public static final long DEFAULT_TEST_TIMEOUT_MS = 10000;

    protected String sourceConfigFilename = "integration-test-source.conf";
    protected String sinkConfigFilename = "integration-test-sink.conf";
    protected String handlerConfigFilename = "integration-test-handler.conf";

    @ClassRule
    public static PulsarContainer pulsar = MockContainers.newPulsarContainer();

    protected static PulsarApplication createPulsarApp(String config, String testId) throws Exception {
        logger.info("Creating Pulsar Application for config " + config);
        Config configObj = PulsarMockApplication.readConfig(config);
        return createPulsarApp(configObj, testId);
    }

    protected static PulsarApplication createPulsarApp(Config config, String testId) throws Exception {
        Config base = PulsarMockApplication.readConfigWithTopicOverrides(config, testId);
        assertNotNull(base);
        // No Redis atm. TODO add later if needed
        PulsarApplication app = PulsarMockApplication.newInstance(base, null, pulsar);
        assertNotNull(app);
        return app;
    }

    public static void validatePulsarProperties(Message<byte[]> received, String expectedKey, long expectedTime, TransitdataProperties.ProtobufSchema expectedSchema) {
        assertEquals(expectedSchema.toString(), received.getProperty(TransitdataProperties.KEY_PROTOBUF_SCHEMA));
        assertEquals(expectedKey, received.getKey());
        assertEquals(expectedTime, received.getEventTime());

    }

    public void testPulsarMessageHandler(IMessageHandler handler, TestPipeline.TestLogic logic, String testId) throws Exception {
        //In case you can use the default config
        PulsarApplication testApp = createPulsarApp(handlerConfigFilename, testId);
        testPulsarMessageHandler(handler, testApp, logic, testId, DEFAULT_TEST_TIMEOUT_MS);
    }

    public void testPulsarMessageHandler(IMessageHandler handler, PulsarApplication testApp, TestPipeline.TestLogic logic, String testId) throws Exception {
        testPulsarMessageHandler(handler, testApp, logic, testId, DEFAULT_TEST_TIMEOUT_MS);
    }

    public void testPulsarMessageHandler(IMessageHandler handler, PulsarApplication testApp, TestPipeline.TestLogic logic, String testId, long testTimeoutMs) throws Exception {

        logger.info("Initializing test resources");
        PulsarApplication sourceApp = createPulsarApp(sourceConfigFilename, testId);
        Producer<byte[]> source = sourceApp.getContext().getSingleProducer();
        assertNotNull(source);
        assertTrue(source.isConnected());

        PulsarApplication sinkApp = createPulsarApp(sinkConfigFilename, testId);
        Consumer<byte[]> sink = sinkApp.getContext().getConsumer();
        assertNotNull(sink);
        assertTrue(sink.isConnected());

        TestPipeline.TestContext context = new TestPipeline.TestContext();
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

}
