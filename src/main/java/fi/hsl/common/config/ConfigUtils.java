package fi.hsl.common.config;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Optional;
import java.util.Scanner;

public class ConfigUtils {
    private static final Logger log = LoggerFactory.getLogger(ConfigUtils.class);

    private ConfigUtils() {}

    @NotNull
    public static String getEnvOrThrow(@NotNull String name) throws IllegalArgumentException {
        return getEnv(name).orElseThrow(() -> new IllegalArgumentException("Missing required env variable " + name));
    }

    public static Optional<String> getEnv(@NotNull String name) {
        return Optional.ofNullable(System.getenv(name));
    }

    public static Optional<Integer> safeParseInt(@NotNull String value) {
        try {
            int n = Integer.parseInt(value);
            return Optional.of(n);
        }
        catch (NumberFormatException e) {
            log.error("Failed to parse int from " + value, e);
            return Optional.empty();
        }
    }

    public static Optional<Integer> getIntEnv(@NotNull String name) {
        return getEnv(name).flatMap(ConfigUtils::safeParseInt);
    }

    public static String getConnectionStringFromFileOrThrow() throws Exception {
        return getSecretFromFileOrThrow("FILEPATH_CONNECTION_STRING", Optional.empty());
    }

    public static String getConnectionStringFromFileOrThrow(final Optional<String> defaultPath) throws Exception {
        return getSecretFromFileOrThrow("FILEPATH_CONNECTION_STRING", defaultPath);
    }

    public static String getUsernameFromFileOrThrow() throws Exception {
        return getSecretFromFileOrThrow("FILEPATH_USERNAME_SECRET", Optional.empty());
    }

    public static String getUsernameFromFileOrThrow(final Optional<String> defaultPath) throws Exception {
        return getSecretFromFileOrThrow("FILEPATH_USERNAME_SECRET", defaultPath);
    }

    public static String getPasswordFromFileOrThrow() throws Exception {
        return getSecretFromFileOrThrow("FILEPATH_PASSWORD_SECRET", Optional.empty());
    }

    public static String getPasswordFromFileOrThrow(final Optional<String> defaultPath) throws Exception {
        return getSecretFromFileOrThrow("FILEPATH_PASSWORD_SECRET", defaultPath);
    }

    public static String getSecretFromFileOrThrow(@NotNull final String envName) throws Exception {
        return getSecretFromFileOrThrow(envName, Optional.empty());
    }

    public static String getSecretFromFileOrThrow(@NotNull final String envName, final Optional<String> defaultPath) throws Exception {
        String secretFilePath;
        final Optional<String> maybeSecretFilePath = getEnv(envName);
        if (maybeSecretFilePath.isPresent()) {
            secretFilePath = maybeSecretFilePath.get();
        } else if (defaultPath.isPresent()) {
            secretFilePath = defaultPath.get();
        } else {
            throw new Exception("Failed to get path to secret file");
        }

        String secret;
        try {
            secret = new Scanner(new File(secretFilePath)).useDelimiter("\\Z").next();
        } catch (Exception e) {
            log.error("Failed to read secret file", e);
            throw e;
        }

        if (secret.isEmpty()) {
            throw new Exception("Failed to get secret from file");
        }

        return secret;
    }
}
