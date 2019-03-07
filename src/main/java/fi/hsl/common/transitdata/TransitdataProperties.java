package fi.hsl.common.transitdata;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Keys and corresponding values that are shared in the Transitdata pipeline.
 */
public class TransitdataProperties {
    private TransitdataProperties() {}

    public static final String REDIS_PREFIX_JPP = "jpp:";
    public static final String REDIS_PREFIX_DVJ = "dvj:";
    public static final String REDIS_PREFIX_JORE_ID = "jore:";

    public static final String KEY_PROTOBUF_SCHEMA = "protobuf-schema";
    public static final String KEY_SCHEMA_VERSION = "schema-version";
    public static final String KEY_DVJ_ID = "dvj-id";

    public static final String KEY_ROUTE_NAME = "route-name";
    public static final String KEY_DIRECTION = "direction";
    public static final String KEY_START_TIME = "start-time";
    public static final String KEY_OPERATING_DAY = "operating-day";
    public static final String KEY_STOP_ID = "stop-id";
    public static final String KEY_LAST_CACHE_UPDATE_TIMESTAMP = "cache-update-ts";

    /**
     * Describes the payload format for each message so that the data can be de-serialized.
     * Each message should contain this as property KEY_PROTOBUF_SCHEMA, along with the KEY_SCHEMA_VERSION.
     * Actual protobuf files can be found in package transitdata.proto
     */
    public enum ProtobufSchema {
        PubtransRoiArrival,
        PubtransRoiDeparture,
        GTFS_TripUpdate,
        GTFS_ServiceAlert,
        GTFS_VehiclePosition,
        InternalMessagesTripCancellation,
        InternalMessagesStopEstimate,
        MqttRawMessage,
        HfpData;

        public String toString() {
            switch (this) {
                case PubtransRoiArrival: return "pubtrans-roi-arrival";
                case PubtransRoiDeparture: return "pubtrans-roi-departure";
                case GTFS_TripUpdate: return "gtfs-trip-update";
                case GTFS_ServiceAlert: return "gtfs-service-alert";
                case GTFS_VehiclePosition: return "gtfs-vehicle-position";
                case InternalMessagesTripCancellation: return "internal-messages-trip-cancellation";
                case InternalMessagesStopEstimate: return "internal-messages-stop-estimate";
                case MqttRawMessage: return "mqtt-raw";
                case HfpData: return "hfp-data";
                default: throw new IllegalArgumentException();
            }
        }

        public static ProtobufSchema fromString(String str) {
            if (str.equals(PubtransRoiArrival.toString())) {
                return PubtransRoiArrival;
            }
            else if (str.equals(PubtransRoiDeparture.toString())) {
                return PubtransRoiDeparture;
            }
            else if (str.equals(GTFS_TripUpdate.toString())) {
                return GTFS_TripUpdate;
            }
            else if (str.equals(GTFS_ServiceAlert.toString())) {
                return GTFS_ServiceAlert;
            }
            else if (str.equals(GTFS_VehiclePosition.toString())) {
                return GTFS_VehiclePosition;
            }
            else if (str.equals(InternalMessagesTripCancellation.toString())) {
                return InternalMessagesTripCancellation;
            }
            else if (str.equals(InternalMessagesStopEstimate.toString())) {
                return InternalMessagesStopEstimate;
            }
            else if (str.equals(MqttRawMessage.toString())) {
                return MqttRawMessage;
            }
            else if (str.equals(HfpData.toString())) {
                return HfpData;
            }
            else {
                throw new IllegalArgumentException();
            }
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

    public static String formatJoreId(String route, String direction, String startDate, String startTime) {
        return REDIS_PREFIX_JORE_ID + route + "-" + direction + "-" + startDate + "-" + startTime;
    }
}
