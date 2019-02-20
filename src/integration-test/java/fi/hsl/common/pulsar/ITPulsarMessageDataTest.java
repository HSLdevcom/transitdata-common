package fi.hsl.common.pulsar;

import fi.hsl.common.transitdata.TransitdataProperties;
import org.junit.Test;

import java.util.HashMap;
import java.util.IdentityHashMap;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;

public class PulsarMessageDataTest {
    @Test
    public void testEqualsWithoutProperties() {
        long ts = System.currentTimeMillis();
        PulsarMessageData data = new PulsarMessageData(
                "test".getBytes(),
                ts, "key");

        PulsarMessageData data2 = new PulsarMessageData(
                "test".getBytes(),
                ts, "key");
        assertEquals(data, data2);
    }

    @Test
    public void testEqualsWithProperties() {
        long ts = System.currentTimeMillis();
        HashMap<String, String> props =  new HashMap<>();
        props.put(TransitdataProperties.KEY_SCHEMA_VERSION, "1");
        props.put(TransitdataProperties.KEY_PROTOBUF_SCHEMA, TransitdataProperties.ProtobufSchema.MqttRawMessage.toString());

        IdentityHashMap<String, String> props2 =  new IdentityHashMap<>();
        props2.put(TransitdataProperties.KEY_PROTOBUF_SCHEMA, TransitdataProperties.ProtobufSchema.MqttRawMessage.toString());
        props2.put(TransitdataProperties.KEY_SCHEMA_VERSION, "1");

        PulsarMessageData data = new PulsarMessageData(
                "test".getBytes(),
                ts, "key", props);

        PulsarMessageData data2 = new PulsarMessageData(
                "test".getBytes(),
                ts, "key", props2);
        assertEquals(data, data2);
    }

    @Test
    public void testEqualsWithEverything() {
        long ts = System.currentTimeMillis();
        HashMap<String, String> props =  new HashMap<>();
        props.put(TransitdataProperties.KEY_SCHEMA_VERSION, "1");
        props.put(TransitdataProperties.KEY_DVJ_ID, "fake-dvj-id");

        IdentityHashMap<String, String> props2 =  new IdentityHashMap<>();
        props2.put(TransitdataProperties.KEY_DVJ_ID, "fake-dvj-id");
        props2.put(TransitdataProperties.KEY_SCHEMA_VERSION, "1");

        PulsarMessageData data = new PulsarMessageData(
                "test".getBytes(),
                ts, "key",
                props, TransitdataProperties.ProtobufSchema.GTFS_TripUpdate);

        PulsarMessageData data2 = new PulsarMessageData(
                "test".getBytes(),
                ts, "key",
                props2, TransitdataProperties.ProtobufSchema.GTFS_TripUpdate);
        assertEquals(data, data2);
    }

    @Test
    public void testEqualsWithDifferentKey() {
        long ts = System.currentTimeMillis();
        PulsarMessageData data = new PulsarMessageData(
                "test".getBytes(),
                ts, "key2");
        PulsarMessageData data2 = new PulsarMessageData(
                "test".getBytes(),
                ts, "key");
        assertFalse(data.equals(data2));
    }

    @Test
    public void testEqualsWithDifferentTimestamp() {
        long ts = System.currentTimeMillis();
        long ts2 = ts + 1;
        PulsarMessageData data = new PulsarMessageData(
                "test".getBytes(),
                ts, "key");
        PulsarMessageData data2 = new PulsarMessageData(
                "test".getBytes(),
                ts2, "key");
        assertFalse(data.equals(data2));
    }

    @Test
    public void testEqualsWithDifferentPayload() {
        long ts = System.currentTimeMillis();
        PulsarMessageData data = new PulsarMessageData(
                "test2".getBytes(),
                ts, "key");
        PulsarMessageData data2 = new PulsarMessageData(
                "test".getBytes(),
                ts, "key");
        assertFalse(data.equals(data2));
    }

    @Test
    public void testEqualsWithDifferentProperties() {
        long ts = System.currentTimeMillis();
        HashMap<String, String> props =  new HashMap<>();
        props.put(TransitdataProperties.KEY_SCHEMA_VERSION, "1");
        props.put(TransitdataProperties.KEY_PROTOBUF_SCHEMA, TransitdataProperties.ProtobufSchema.MqttRawMessage.toString());

        IdentityHashMap<String, String> props2 =  new IdentityHashMap<>();
        props2.put(TransitdataProperties.KEY_PROTOBUF_SCHEMA, TransitdataProperties.ProtobufSchema.HfpData.toString());
        props2.put(TransitdataProperties.KEY_SCHEMA_VERSION, "1");

        PulsarMessageData data = new PulsarMessageData(
                "test".getBytes(),
                ts, "key", props);

        PulsarMessageData data2 = new PulsarMessageData(
                "test".getBytes(),
                ts, "key", props2);
        assertFalse(data.equals(data2));
    }

    @Test
    public void testEqualsWithDifferentSchema() {
        long ts = System.currentTimeMillis();
        HashMap<String, String> props =  new HashMap<>();
        props.put(TransitdataProperties.KEY_SCHEMA_VERSION, "1");
        props.put(TransitdataProperties.KEY_DVJ_ID, "fake-dvj-id");

        IdentityHashMap<String, String> props2 =  new IdentityHashMap<>();
        props2.put(TransitdataProperties.KEY_DVJ_ID, "fake-dvj-id");
        props2.put(TransitdataProperties.KEY_SCHEMA_VERSION, "1");

        PulsarMessageData data = new PulsarMessageData(
                "test".getBytes(),
                ts, "key",
                props, TransitdataProperties.ProtobufSchema.GTFS_TripUpdate);

        PulsarMessageData data2 = new PulsarMessageData(
                "test".getBytes(),
                ts, "key",
                props2, TransitdataProperties.ProtobufSchema.GTFS_ServiceAlert);
        assertFalse(data.equals(data2));
    }

}
