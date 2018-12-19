package fi.hsl.common.pulsar;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import fi.hsl.common.config.ConfigParser;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PulsarContainer;

import java.util.HashMap;
import java.util.Map;

public class PulsarMockApplication extends PulsarApplication {

    PulsarMockApplication() {
    }

    public static PulsarApplication newInstance(Config baseConfig, GenericContainer redis, PulsarContainer pulsar) throws Exception {
        PulsarApplication app = null;
        try {
            Map<String, Object> overrides = new HashMap<>();
            if (pulsar != null) {
                overrides.put("pulsar.host", pulsar.getContainerIpAddress());
                overrides.put("pulsar.port", pulsar.getMappedPort(PulsarContainer.BROKER_PORT));
            }
            if (redis != null) {
                overrides.put("redis.host", redis.getContainerIpAddress());
                overrides.put("redis.port", redis.getMappedPort(baseConfig.getInt("redis.port")));
            }

            Config config = ConfigFactory.parseMap(overrides);

            Config merged = ConfigParser.mergeConfigs(baseConfig, config);

            app = new PulsarMockApplication();
            app.context = app.initialize(merged);
            return app;
        }
        catch (Exception e) {
            if (app != null) {
                app.close();
            }
            throw e;
        }
    }

    public static Config readConfig(String filename) {
        return ConfigParser.createConfig(filename);
    }

    public static Config readConfigWithOverride(String filename, String key, Object value) {
        Map<String, Object> overrides = new HashMap<>();
        overrides.put(key, value);
        return readConfigWithOverrides(filename, overrides);
    }

    public static Config readConfigWithOverrides(String filename, Map<String, Object> overrides) {
        Config configOverrides = ConfigFactory.parseMap(overrides);
        return ConfigParser.mergeConfigs(readConfig(filename), configOverrides);
    }

    /**
     * Occasionally it's good to create unique topic names so we can segregate the tests easier.
     */
    public static Config readConfigWithTopicOverrides(String filename, String suffix) {
        Config config = readConfig(filename);
        Map<String, Object> overrides = new HashMap<>();
        if (config.getBoolean("pulsar.producer.enabled")) {
            String topic = config.getString("pulsar.producer.topic");
            overrides.put("pulsar.producer.topic", topic + suffix);
        }
        if (config.getBoolean("pulsar.consumer.enabled")) {
            String topic = config.getString("pulsar.consumer.topic");
            overrides.put("pulsar.consumer.topic", topic + suffix);
        }
        return readConfigWithOverrides(filename, overrides);
    }

}
