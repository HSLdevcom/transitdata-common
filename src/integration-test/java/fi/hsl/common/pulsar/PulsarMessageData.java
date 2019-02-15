package fi.hsl.common.pulsar;

import fi.hsl.common.transitdata.TransitdataProperties;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.TypedMessageBuilder;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class PulsarMessageData {
    public byte[] payload;
    public Optional<String> key = Optional.empty();
    public Optional<Long> eventTime = Optional.empty();
    public Optional<Map<String, String>> properties = Optional.empty();
    public Optional<TransitdataProperties.ProtobufSchema> schema = Optional.empty();

    public PulsarMessageData(byte[] payload, Long eventTime) {
        this(payload, eventTime, null, null, null);
    }

    public PulsarMessageData(byte[] payload, Long eventTime, String key) {
        this(payload, eventTime, key, null, null);
    }

    public PulsarMessageData(byte[] payload, Long eventTime, String key, Map<String, String> props) {
        this(payload, eventTime, key, props, null);
    }

    public PulsarMessageData(byte[] payload, Long eventTime, String key, TransitdataProperties.ProtobufSchema schema) {
        this(payload, eventTime, key, null, schema);
    }

    public PulsarMessageData(byte[] payload, Long eventTime, String key, Map<String, String> props, TransitdataProperties.ProtobufSchema schema) {
        this.payload = payload;
        this.eventTime =  Optional.ofNullable(eventTime);
        this.key = Optional.ofNullable(key);
        this.properties = Optional.ofNullable(props);
        this.schema = Optional.ofNullable(schema);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        PulsarMessageData other = (PulsarMessageData)o;
        if (!Arrays.equals(payload, other.payload))
            return false;
        if (!other.key.equals(this.key))
            return false;
        if (!other.eventTime.equals(this.eventTime))
            return false;
        if (!other.properties.equals(this.properties))
            return false;
        if (!other.schema.equals(this.schema))
            return false;
        return true;
    }

    public static TypedMessageBuilder<byte[]> toPulsarMessage(Producer<byte[]> producer,
                                                              PulsarMessageData data) throws PulsarClientException {
        TypedMessageBuilder<byte[]> builder = producer.newMessage().value(data.payload);
        data.eventTime.ifPresent(builder::eventTime);
        data.key.ifPresent(builder::key);
        data.schema.ifPresent(
                s -> builder.property(TransitdataProperties.KEY_PROTOBUF_SCHEMA, s.toString())
        );
        data.properties.ifPresent(map -> map.forEach(builder::property));
        return builder;
    }

}
