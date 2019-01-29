package fi.hsl.common.transitdata;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TransitdataPropertiesTest {
    @Test
    public void testProtobufSchemaConverters() {
        testProtobufSchemaConverter(TransitdataProperties.ProtobufSchema.PubtransRoiArrival);
        testProtobufSchemaConverter(TransitdataProperties.ProtobufSchema.PubtransRoiDeparture);
        testProtobufSchemaConverter(TransitdataProperties.ProtobufSchema.GTFS_TripUpdate);
        testProtobufSchemaConverter(TransitdataProperties.ProtobufSchema.GTFS_ServiceAlert);
        testProtobufSchemaConverter(TransitdataProperties.ProtobufSchema.GTFS_VehiclePosition);
        testProtobufSchemaConverter(TransitdataProperties.ProtobufSchema.InternalMessagesTripCancellation);
        testProtobufSchemaConverter(TransitdataProperties.ProtobufSchema.MqttRawMessage);
        testProtobufSchemaConverter(TransitdataProperties.ProtobufSchema.HfpData);
    }

    private void testProtobufSchemaConverter(TransitdataProperties.ProtobufSchema schema) {
        assertEquals(schema, TransitdataProperties.ProtobufSchema.fromString(schema.toString()));
    }
}
