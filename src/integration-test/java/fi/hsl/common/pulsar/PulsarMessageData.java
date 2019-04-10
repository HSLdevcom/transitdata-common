package fi.hsl.common.pulsar;

import fi.hsl.common.transitdata.TransitdataProperties;
import fi.hsl.common.transitdata.TransitdataSchema;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.TypedMessageBuilder;

import java.util.*;

public class PulsarMessageData {
    public byte[] payload;
    public Optional<String> key = Optional.empty();
    public Optional<Long> eventTime = Optional.empty();
    public Map<String, String> properties;

    public PulsarMessageData(byte[] payload, Long eventTime) {
        this(payload, eventTime, null, new HashMap<>());
    }

    public PulsarMessageData(byte[] payload, Long eventTime, String key) {
        this(payload, eventTime, key, new HashMap<>());
    }

    public PulsarMessageData(byte[] payload, Long eventTime, String key, Map<String, String> props) {
        this.payload = payload;
        this.eventTime =  Optional.ofNullable(eventTime);
        this.key = Optional.ofNullable(key);
        this.properties = props;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(payload);
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
        return propertiesEqual(other);
    }

    boolean propertiesEqual(PulsarMessageData other) {
        if (other.properties.size() != this.properties.size())
            return false;

        Iterator<String> keyItr = other.properties.keySet().iterator();
        while(keyItr.hasNext()) {
            String key = keyItr.next();

            Optional<String> otherValue =  Optional.ofNullable(other.properties.get(key));
            Optional<String> myValue =  Optional.ofNullable(this.properties.get(key));
            if (!otherValue.equals(myValue))
                return false;
        }
        return true;
    }

    public static TypedMessageBuilder<byte[]> toPulsarMessage(Producer<byte[]> producer,
                                                              PulsarMessageData data) {
        TypedMessageBuilder<byte[]> builder = producer.newMessage().value(data.payload);
        data.eventTime.ifPresent(builder::eventTime);
        data.key.ifPresent(builder::key);
        data.properties.forEach(builder::property);
        return builder;
    }

    public static PulsarMessageData fromPulsarMessage(Message<byte[]> msg)  {
        PulsarMessageData data = new PulsarMessageData(
                msg.getData(),
                msg.getEventTime(),
                msg.getKey(),
                msg.getProperties());
        return data;
    }
}
