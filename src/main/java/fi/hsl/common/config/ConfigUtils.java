package fi.hsl.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class ConfigUtils {
    private static final Logger log = LoggerFactory.getLogger(ConfigUtils.class);

    private ConfigUtils() {}

    public static String getEnvOrThrow(String name) throws IllegalArgumentException {
        return Optional.ofNullable(System.getenv(name))
                .orElseThrow(() -> new IllegalArgumentException("Missing required env variable " + name));
    }

    public static Optional<String> getEnv(String name) {
        return Optional.ofNullable(System.getenv(name));
    }

    public static Optional<Integer> safeParseInt(String value) {
        try {
            int n = Integer.parseInt(value);
            return Optional.of(n);
        }
        catch (NumberFormatException e) {
            log.error("Failed to parseJson int from " + value, e);
            return Optional.empty();
        }
    }

    public static Optional<Integer> getIntEnv(String name) {
        return getEnv(name).flatMap(ConfigUtils::safeParseInt);
    }

}
