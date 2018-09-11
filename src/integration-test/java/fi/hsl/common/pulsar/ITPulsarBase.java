package fi.hsl.common.pulsar;

import com.typesafe.config.Config;
import fi.hsl.common.config.ConfigParser;
import fi.hsl.common.config.ConfigUtils;
import org.apache.pulsar.client.api.Message;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PulsarContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import redis.clients.jedis.Jedis;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class ITPulsarBase {

    static final Logger logger = LoggerFactory.getLogger(ITPulsarBase.class);

    static Jedis jedis;

    static final boolean PRINT_PULSAR_LOG = ConfigUtils.getEnv("PRINT_PULSAR_LOG").map(Boolean::parseBoolean).orElse(false);

    @ClassRule
    public static GenericContainer redis = MockContainers.newRedisContainer();

    @ClassRule
    public static PulsarContainer pulsar = MockContainers.newPulsarContainer();

    @BeforeClass
    public static void setUp() throws Exception {
        jedis = MockContainers.newMockJedisConnection(redis);
        if (PRINT_PULSAR_LOG) {
            MockContainers.tail(pulsar, logger);
        }
    }

    @Test
    public void testRedis() {
        jedis.set("key", "value");
        String value = jedis.get("key");
        assertEquals(value, "value");
    }

    @Test
    public void readConfig() {
        readDefaultConfig();
    }

    private Config readDefaultConfig() {
        Config config = ConfigParser.createConfig("integration-test.conf");
        assertNotNull(config);
        return config;
    }

    @Test
    public void testPulsar() throws Exception {
        Config base = readDefaultConfig();

        PulsarApplication app = PulsarMockApplication.newInstance(base, redis, pulsar);
        assertNotNull(app);

        logger.info("Pulsar Application created, testing to send a message");

        final String payload = "Test-message";
        app.context.getProducer().send(payload.getBytes());

        logger.info("Message sent, reading it back");

        Message<byte[]> msg = app.context.getConsumer().receive(5, TimeUnit.SECONDS);

        assertNotNull(msg);

        String received = new String(msg.getData(), Charset.defaultCharset());
        logger.info("Received: " + received);
        assertEquals(payload, received);

        app.close();
    }
}
