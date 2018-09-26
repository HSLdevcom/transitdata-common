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

}
