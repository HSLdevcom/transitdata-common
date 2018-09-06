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

    public static final String KEY_PROTOBUF_SCHEMA = "protobuf-schema";
    public static final String KEY_SCHEMA_VERSION = "schema-version";
    public static final String KEY_DVJ_ID = "dvj-id";

    public static final String KEY_ROUTE_NAME = "route-name";
    public static final String KEY_DIRECTION = "direction";
    public static final String KEY_START_TIME = "start-time";
    public static final String KEY_OPERATING_DAY = "operating-day";
    public static final String KEY_STOP_ID = "stop-id";

    /**
     * Describes the payload format for each message so that the data can be de-serialized.
     * Each message should contain this as property KEY_PROTOBUF_SCHEMA, along with the KEY_SCHEMA_VERSION.
     * Actual protobuf files can be found in package transitdata.proto
     */
    public enum ProtobufSchema {
        PubtransRoiArrival,
        PubtransRoiDeparture,
        OMM_Cancellation,
        GTFS_TripUpdate,
        GTFS_ServiceAlert,
        GTFS_VehiclePosition;

        public String toString() {
            switch (this) {
                case PubtransRoiArrival: return "pubtrans-roi-arrival";
                case PubtransRoiDeparture: return "pubtrans-roi-departure";
                case OMM_Cancellation: return "omm-cancellation";
                case GTFS_TripUpdate: return "gtfs-trip-update";
                case GTFS_ServiceAlert: return "gtfs-service-alert";
                case GTFS_VehiclePosition: return "gtfs-vehicle-position";
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
            else if (str.equals(PubtransRoiArrival.toString())) {
                return PubtransRoiArrival;
            }
            else if (str.equals(PubtransRoiArrival.toString())) {
                return PubtransRoiArrival;
            }
            else if (str.equals(PubtransRoiArrival.toString())) {
                return PubtransRoiArrival;
            }
            else if (str.equals(PubtransRoiArrival.toString())) {
                return PubtransRoiArrival;
            }
            else if (str.equals(PubtransRoiArrival.toString())) {
                return PubtransRoiArrival;
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
}
