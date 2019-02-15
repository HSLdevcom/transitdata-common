package fi.hsl.common.transitdata;

import org.apache.pulsar.client.api.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class TransitdataSchema {
    private static final Logger log = LoggerFactory.getLogger(TransitdataSchema.class);

    final public TransitdataProperties.ProtobufSchema schema;
    final public Optional<Integer> schemaVersion;

    public TransitdataSchema(TransitdataProperties.ProtobufSchema schema, Optional<Integer> version) {
        this.schema = schema;
        schemaVersion = version;
    }

    public static Optional<TransitdataSchema> parseFromPulsarMessage(Message received) {
        try {
            String schemaType = received.getProperty(TransitdataProperties.KEY_PROTOBUF_SCHEMA);
            if (schemaType == null)
                return Optional.empty();

            TransitdataProperties.ProtobufSchema schema = TransitdataProperties.ProtobufSchema.fromString(schemaType);

            String version = received.getProperty(TransitdataProperties.KEY_SCHEMA_VERSION);
            Optional<Integer> maybeVersion = Optional.ofNullable(version).map(Integer::parseInt);
            return Optional.of(new TransitdataSchema(schema, maybeVersion));
        }
        catch (Exception e) {
            log.error("Failed to parse protobuf schema", e);
            return Optional.empty();
        }
    }

    public static boolean hasProtobufSchema(Message received, TransitdataProperties.ProtobufSchema expected) {
        return parseFromPulsarMessage(received)
                .map(schema -> schema.schema == expected)
                .orElse(false);
    }

}
