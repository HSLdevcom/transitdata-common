package fi.hsl.common.config;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Scanner;

public class ConfigUtils {
    private static final Logger log = LoggerFactory.getLogger(ConfigUtils.class);

    private ConfigUtils() {
    }

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
        } catch (NumberFormatException e) {
            log.error("Failed to parse int from " + value, e);
            return Optional.empty();
        }
    }

    public static Optional<Integer> getIntEnv(@NotNull String name) {
        return getEnv(name).flatMap(ConfigUtils::safeParseInt);
    }

    @NotNull
    public static String getConnectionStringFromFileOrThrow() throws Exception {
        return getSecretFromFileOrThrow("FILEPATH_CONNECTION_STRING", Optional.empty());
    }

    @NotNull
    public static String getConnectionStringFromFileOrThrow(final Optional<String> defaultPath) throws Exception {
        return getSecretFromFileOrThrow("FILEPATH_CONNECTION_STRING", defaultPath);
    }

    @NotNull
    public static String getUsernameFromFileOrThrow() throws Exception {
        return getSecretFromFileOrThrow("FILEPATH_USERNAME_SECRET", Optional.empty());
    }

    @NotNull
    public static String getUsernameFromFileOrThrow(final Optional<String> defaultPath) throws Exception {
        return getSecretFromFileOrThrow("FILEPATH_USERNAME_SECRET", defaultPath);
    }

    @NotNull
    public static String getPasswordFromFileOrThrow() throws Exception {
        return getSecretFromFileOrThrow("FILEPATH_PASSWORD_SECRET", Optional.empty());
    }

    @NotNull
    public static String getPasswordFromFileOrThrow(final Optional<String> defaultPath) throws Exception {
        return getSecretFromFileOrThrow("FILEPATH_PASSWORD_SECRET", defaultPath);
    }

    @NotNull
    public static String getSecretFromFileOrThrow(@NotNull final String envName) throws Exception {
        return getSecretFromFileOrThrow(envName, Optional.empty());
    }

    @NotNull
    public static String getSecretFromFileOrThrow(@NotNull final String envName, final Optional<String> defaultPath)
            throws Exception {
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
            secret = Files.readString(Path.of(secretFilePath), StandardCharsets.UTF_8).strip();
        } catch (Exception e) {
            log.error("Failed to read secret file ({})", secretFilePath, e);
            throw e;
        }

        if (secret.isEmpty()) {
            throw new Exception("Secret file was empty (" + secretFilePath + ")");
        }

        return secret;
    }
}
