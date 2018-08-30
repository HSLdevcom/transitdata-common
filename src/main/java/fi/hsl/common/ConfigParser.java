package fi.hsl.common;

import java.io.File;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigParser {
    private static final Logger log = LoggerFactory.getLogger(ConfigParser.class);

    private ConfigParser() {}

    /**
     * Create a valid Config from a configuration file and environment variables using default filename "environment.conf".
     *
     * @see #createConfig(String)
     */
    public static Config createConfig() {
        return createConfig("environment.conf");
    }

    /**
     * Create a valid Config from a configuration file and environment variables.
     *
     * <p>If the environment variable CONFIG_PATH is set, it determines the path to the configuration
     * file. Otherwise only the other environment variables affect the configuration.
     *
     * <p>Environment variables override values in the configuration file in case of conflict.
     *
     * @return Complete and valid configuration.
     */
    public static Config createConfig(String filename) {
        Config fileConfig = parseFileConfig();
        Config envConfig = ConfigFactory.parseResources(filename).resolve();
        return mergeConfigs(fileConfig, envConfig);
    }

    /**
     * Parse a Config from the path given by the environment variable CONFIG_PATH. If CONFIG_PATH is
     * unset, return null.
     *
     * @return Either a configuration parsed from the given path or null.
     */
    private static Config parseFileConfig() {
        Config fileConfig = null;
        String configPath = System.getenv("CONFIG_PATH");
        if (configPath != null) {
            try {
                fileConfig = ConfigFactory.parseFile(new File(configPath)).resolve();
            } catch (ConfigException e) {
                log.error("Parsing the configuration file from " + configPath + " failed.", e);
                throw e;
            }
        }
        return fileConfig;
    }

    /**
     * Merge the given Configs and validate the result.
     *
     * <p>envConfig overrides any conflicting keys in fileConfig.
     *
     * @param fileConfig The Config read from a file or null.
     * @param envConfig The Config read from the environment variables.
     * @return The Config resulting from merging fileConfig and envConfig.
     */
    private static Config mergeConfigs(Config fileConfig, Config envConfig) {
        Config fullConfig;
        if (fileConfig != null) {
            fullConfig = envConfig.withFallback(fileConfig);
        } else {
            fullConfig = envConfig;
        }
        fullConfig.resolve();
        try {
            fullConfig.checkValid(ConfigFactory.parseResources("application.conf").resolve());
        } catch (ConfigException.ValidationFailed e) {
            log.error("Validating the given configuration failed.", e);
            throw e;
        }
        return fullConfig;
    }
}
