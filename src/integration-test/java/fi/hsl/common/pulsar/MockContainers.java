package fi.hsl.common.pulsar;

import org.apache.pulsar.client.api.PulsarClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PulsarContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import redis.clients.jedis.Jedis;

public class MockContainers {

    static final Logger logger = LoggerFactory.getLogger(MockContainers.class);

    private MockContainers() {}

    public static GenericContainer newRedisContainer() {
        return new GenericContainer("redis:3.0.2")
                .withExposedPorts(6379);
    }

    public static Jedis newMockJedisConnection() {
        return newMockJedisConnection(newRedisContainer());
    }

    public static Jedis newMockJedisConnection(GenericContainer redis) {
        logger.info("Connecting to Redis container");
        Jedis jedis = new Jedis(redis.getContainerIpAddress(), redis.getMappedPort(6379));
        jedis.connect();
        logger.info("Redis connected");
        return jedis;
    }

    public static PulsarContainer newPulsarContainer() {
        return new PulsarContainer("2.1.0-incubating");
    }

    public static PulsarClient newMockPulsarClient(PulsarContainer pulsar) throws Exception {
        return PulsarClient.builder()
                .serviceUrl(pulsar.getPulsarBrokerUrl())
                .build();
    }

    public static void tail(GenericContainer container, Logger logger) {
        Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(logger);
        container.followOutput(logConsumer);
    }
}
