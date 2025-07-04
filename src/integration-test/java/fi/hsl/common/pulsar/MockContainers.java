package fi.hsl.common.pulsar;

import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.common.policies.data.TenantInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PulsarContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import redis.clients.jedis.Jedis;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MockContainers {

    static final Logger logger = LoggerFactory.getLogger(MockContainers.class);

    private MockContainers() {}

    public static GenericContainer newRedisContainer() {
        return new GenericContainer("redis:5.0.6")
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

    /**
     * This can be used for testing when you use default public namespace for topic names.
     * For using the "real" tenant/namespace/topicname pattern @see MockContainers#configurePulsarContainer(PulsarContainer, String, String)
     */
    public static PulsarContainer newPulsarContainer() {
        return new PulsarContainer("3.0.7");
    }

    public static PulsarContainer configurePulsarContainer(PulsarContainer pulsar, final String tenant, final String namespace) throws Exception {
        PulsarAdmin admin = PulsarAdmin.builder()
                .serviceHttpUrl(pulsar.getHttpServiceUrl())
                .build();

        Set<String> clusters = new HashSet<>(List.of("standalone"));
        TenantInfo info = TenantInfo.builder()
                .allowedClusters(clusters)
                .adminRoles(new HashSet<>(List.of("all")))
                .build();
        admin.tenants().createTenant(tenant, info);

        admin.namespaces().createNamespace(tenant + "/" + namespace, clusters);
        logger.info("Pulsar setup done");
        return pulsar;
    }

    public static void tail(GenericContainer container, Logger logger) {
        Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(logger);
        container.followOutput(logConsumer);
    }
}
