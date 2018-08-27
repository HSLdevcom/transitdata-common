package fi.hsl.common.transitdata;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

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
}
