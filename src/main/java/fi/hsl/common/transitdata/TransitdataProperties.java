package fi.hsl.common.transitdata;

/**
 * Keys and corresponding values that are shared in the Transitdata pipeline.
 */
public class TransitdataProperties {
    private TransitdataProperties() {}

    /*
    public static final String KEY_DATA_SOURCE = "data-source";

    public enum DataSource {
        PubtransRoiArrival,
        PubtransRoiDeparture,
        OMM_Cancellation,
        OMM_ServiceAlert,
        HFP,
        TripUpdateProcessor;

        public String toString() {
            switch (this) {
                case PubtransRoiArrival: return "pubtrans-roi-arrival";
                default: return "";
            }
        }

        public static DataSource fromString(String str) {
            if (str.equals(PubtransRoiArrival.toString())) {
                return PubtransRoiArrival;
            }
            throw new IllegalArgumentException();
        }
    }*/

    public static final String KEY_PROTOBUF_SCHEMA = "protobuf-schema";
    public static final String KEY_SCHEMA_VERSION = "schema-version";

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

}
