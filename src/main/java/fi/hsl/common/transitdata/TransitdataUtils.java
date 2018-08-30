package fi.hsl.common.transitdata;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

public class TransitdataUtils {
    private TransitdataUtils() {}

    /**
     * GTFS-RT convention is to use UTC epoch seconds so let's use the same convention.
     * @See https://developers.google.com/transit/gtfs-realtime/reference/#message_feedheader
     *
     * @return UTC epoch seconds
     */
    public static long currentMessageTimestamp() {
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
        return utc.toEpochSecond();
    }


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
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static Optional<Integer> getIntEnv(String name) {
        return getEnv(name).flatMap(TransitdataUtils::safeParseInt);
    }

}
