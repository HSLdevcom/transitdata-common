package fi.hsl.common.transitdata;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Keys and corresponding values that are shared in the Transitdata pipeline.
 */
public class TransitdataProperties {
    private TransitdataProperties() {}
    private static final Pattern matchTimestampSecondsAndMillisecondsPattern = Pattern.compile("\\d{2}\\.\\d{3}Z$");

    public static final String REDIS_PREFIX_JPP = "jpp:";
    public static final String REDIS_PREFIX_DVJ = "dvj:";
    public static final String REDIS_PREFIX_JORE_ID = "jore:";
    public static final String REDIS_PREFIX_METRO = "metro:";

    public static final String KEY_PROTOBUF_SCHEMA = "protobuf-schema";
    public static final String KEY_SCHEMA_VERSION = "schema-version";
    public static final String KEY_DVJ_ID = "dvj-id";

    /**
     * Suffix for MQTT topic when publishing with pulsar-mqtt-gateway
     */
    public static final String KEY_MQTT_TOPIC = "mqtt-topic";

    public static final String KEY_ROUTE_NAME = "route-name";
    public static final String KEY_DIRECTION = "direction";
    public static final String KEY_START_TIME = "start-time";
    public static final String KEY_OPERATING_DAY = "operating-day";
    public static final String KEY_STOP_ID = "stop-id";
    public static final String KEY_LAST_CACHE_UPDATE_TIMESTAMP = "cache-update-ts";
    public static final String KEY_START_DATETIME = "start-datetime";
    public static final String KEY_START_STOP_NUMBER = "start-stop-number";

    /**
     * Describes the payload format for each message so that the data can be de-serialized.
     * Each message should contain this as property KEY_PROTOBUF_SCHEMA, along with the KEY_SCHEMA_VERSION.
     * Actual protobuf files can be found in package transitdata.proto
     */
    public enum ProtobufSchema {
        PubtransRoiArrival("pubtrans-roi-arrival"),
        PubtransRoiDeparture("pubtrans-roi-departure"),
        GTFS_TripUpdate("gtfs-trip-update"),
        GTFS_ServiceAlert("gtfs-service-alert"),
        GTFS_VehiclePosition("gtfs-vehicle-position"),
        InternalMessagesTripCancellation("internal-messages-trip-cancellation"),
        InternalMessagesStopEstimate("internal-messages-stop-estimate"),
        MetroAtsEstimate( "metro-ats-estimate"),
        MqttRawMessage("mqtt-raw"),
        HfpData("hfp-data"),
        TransitdataServiceAlert("transitdata-service-alert"),
        StopCancellations("stop-cancellations");
        public final String key;

        ProtobufSchema(String key) {
            this.key = key;
        }

        public String toString() {
            return key;
        }

        public static ProtobufSchema fromString(@NotNull String str) {
            for (ProtobufSchema protobufSchema : ProtobufSchema.values()) {
                if (protobufSchema.key.equals(str)) {
                    return protobufSchema;
                }
            }

            throw new IllegalArgumentException("No protobuf schema found for string "+str);
        }
    }

    /**
     * GTFS-RT convention is to use UTC epoch seconds so let's use the same convention.
     * @See https://developers.google.com/transit/gtfs-realtime/reference/#message_feedheader
     *
     * @return UTC epoch seconds
     */
    public static long currentTimestamp() {
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
        return utc.toEpochSecond();
    }

    @NotNull
    public static String formatJoreId(@NotNull String route, @NotNull String direction, @NotNull String startDate, @NotNull String startTime) {
        return REDIS_PREFIX_JORE_ID + route + "-" + direction + "-" + startDate + "-" + startTime;
    }

    @NotNull
    public static String formatJoreId(@NotNull String route, @NotNull String direction, @NotNull JoreDateTime startDateTime) {
        return formatJoreId(route, direction, startDateTime.getJoreDateString(), startDateTime.getJoreTimeString());
    }

    /**
     * @param stopShortName e.g. MAK
     * @param originalStartDatetime e.g. 2019-12-20T15:12:56.123Z
     * @return
     */
    @NotNull
    public static String formatMetroId(@NotNull final String stopShortName, @NotNull final String originalStartDatetime) {
        // Transform dateTime string: remove milliseconds, change seconds
        // e.g 2019-12-20T15:12:56.123Z --> 2019-12-20T15:12:00Z
        Matcher m = matchTimestampSecondsAndMillisecondsPattern.matcher(originalStartDatetime);
        String fixedStartDateTime = m.replaceFirst("00Z");

        return REDIS_PREFIX_METRO + stopShortName + "_" + fixedStartDateTime;
    }

}
