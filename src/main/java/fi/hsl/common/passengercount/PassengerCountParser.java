package fi.hsl.common.passengercount;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.ParsingException;
import fi.hsl.common.passengercount.json.*;
import fi.hsl.common.passengercount.proto.PassengerCount;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.regex.Pattern;

public class PassengerCountParser {
    private static final Logger log = LoggerFactory.getLogger(PassengerCountParser.class);

    static final Pattern topicVersionRegex = Pattern.compile("(^v\\d+|dev)");

    DslJson<Object> dslJson = new DslJson<>(
            new DslJson.Settings<>()
                    .allowArrayFormat(true)
                    .includeServiceLoader());

    @NotNull
    public static PassengerCountParser newInstance() {
        return new PassengerCountParser();
    }

    @NotNull
    public Optional<PassengerCount.Payload> parsePayload(@NotNull ApcJson json) {
        final Apc payload = json.apc;

        // Required attributes
        PassengerCount.Payload.Builder payloadBuilder = PassengerCount.Payload.newBuilder();
        if (payload.desi != null) {
            payloadBuilder.setDesi(payload.desi);
        }
        if (payload.dir != null) {
            payloadBuilder.setDir(payload.dir);
        }

        if (payload.oper == null) {
            log.warn("Value for oper is null");
            //oper value is mandatory -> return empty
            return Optional.empty();
        }
        payloadBuilder.setOper(payload.oper);

        if (payload.veh == null) {
            log.warn("Value for veh is null");
            //veh value is mandatory -> return empty
            return Optional.empty();
        }
        payloadBuilder.setVeh(payload.veh);

        payloadBuilder.setTst(payload.tst.getTime());
        payloadBuilder.setTsi(payload.tsi);

        if (payload.lat == null) {
            log.warn("Value for lat is null for vehicle {}/{}", payload.oper, payload.veh);
        } else {
            payloadBuilder.setLat(payload.lat);
        }

        if (payload.lon == null) {
            log.warn("Value for lon is null for vehicle {}/{}", payload.oper, payload.veh);
        } else {
            payloadBuilder.setLong(payload.lon);
        }

        if (payload.odo == null) {
            log.warn("Value for odo is null for vehicle {}/{}", payload.oper, payload.veh);
        } else {
            payloadBuilder.setOdo(payload.odo);
        }

        payloadBuilder.setOday(payload.oday);

        if (payload.jrn == null) {
            log.warn("Value for jrn is null for vehicle {}/{}", payload.oper, payload.veh);
        } else {
            payloadBuilder.setJrn(payload.jrn);
        }

        if (payload.line == null) {
            log.warn("Value for line is null for vehicle {}/{}", payload.oper, payload.veh);
        } else {
            payloadBuilder.setLine(payload.line);
        }

        if (payload.start != null) {
            payloadBuilder.setStart(payload.start);
        }
        if (payload.loc != null) {
            payloadBuilder.setLoc(payload.loc);
        }
        if (payload.stop != null) {
            payloadBuilder.setStop(payload.stop);
        }
        if (payload.route != null) {
            payloadBuilder.setRoute(payload.route);
        }

        if (payload.vehiclecounts == null) {
            log.warn("Field 'vehiclecounts' is null for vehicle {}/{}", payload.oper, payload.veh);
            return Optional.empty();
        }

        PassengerCount.VehicleCounts.Builder vehicleBuilder = PassengerCount.VehicleCounts.newBuilder();
        vehicleBuilder.setVehicleLoad(payload.vehiclecounts.vehicleload);

        if (payload.vehiclecounts.vehicleloadratio == null) {
            log.warn("Value for vehicleloadratio is null for vehicle {}/{}", payload.oper, payload.veh);
        } else {
            vehicleBuilder.setVehicleLoadRatio(payload.vehiclecounts.vehicleloadratio);
        }

        vehicleBuilder.setCountQuality(payload.vehiclecounts.countquality);
        if (payload.vehiclecounts.extensions != null) {
            vehicleBuilder.setExtensions(payload.vehiclecounts.extensions);
        }

        if (payload.vehiclecounts.doorcounts != null) {
            for (DoorCount doorcount : payload.vehiclecounts.doorcounts) {
                PassengerCount.DoorCount.Builder doorCountBuilder = PassengerCount.DoorCount.newBuilder();
                doorCountBuilder.setDoor(doorcount.door);

                for (Count count : doorcount.count) {
                    PassengerCount.Count.Builder countBuilder = PassengerCount.Count.newBuilder();
                    countBuilder.setIn(count.in);
                    countBuilder.setOut(count.out);
                    countBuilder.setClazz(count.clazz);
                    doorCountBuilder.addCount(countBuilder);
                }
                vehicleBuilder.addDoorCounts(doorCountBuilder);
            }
        } else {
            log.warn("Field 'doorcounts' is null for vehicle {}/{}", payload.oper, payload.veh);
            return Optional.empty();
        }

        payloadBuilder.setVehicleCounts(vehicleBuilder);
        return Optional.of(payloadBuilder.build());
    }

    @NotNull
    public static Optional<PassengerCount.Topic> safeParseTopic(@NotNull String topic) {
        try {
            return Optional.of(parseTopic(topic));
        } catch (Exception e) {
            log.error("Failed to parse topic " + topic, e);
            return Optional.empty();
        }
    }

    @NotNull
    public static Optional<PassengerCount.Topic> safeParseTopic(@NotNull String topic, long receivedAtMs) {
        try {
            return Optional.of(parseTopic(topic, receivedAtMs));
        } catch (Exception e) {
            log.error("Failed to parse topic " + topic, e);
            return Optional.empty();
        }
    }

    @NotNull
    public static PassengerCount.Topic parseTopic(@NotNull String topic) throws InvalidAPCTopicException {
        return parseTopic(topic, System.currentTimeMillis());
    }

    @NotNull
    public static PassengerCount.Topic parseTopic(@NotNull String topic, long receivedMs)
            throws InvalidAPCTopicException {
        //log.debug("Parsing metadata from topic: " + topic);

        final String[] parts = topic.split("/", -1); //-1 to include empty substrings

        final PassengerCount.Topic.Builder builder = PassengerCount.Topic.newBuilder();

        builder.setSchemaVersion(builder.getSchemaVersion());
        builder.setReceivedAt(receivedMs);

        int versionIndex = findVersionIndex(parts);
        if (versionIndex < 0) {
            throw new InvalidAPCTopicException("Failed to find topic version from topic " + topic);
        }
        builder.setTopicPrefix(joinFirstNParts(parts, versionIndex, "/"));
        int index = versionIndex;
        final String versionStr = parts[index++];
        builder.setTopicVersion(versionStr);

        final PassengerCount.Topic.JourneyType journeyType = safeValueOf(PassengerCount.Topic.JourneyType.class,
                parts[index++]).orElseThrow(() -> new InvalidAPCTopicException("Unknown journey type: " + topic));
        builder.setJourneyType(journeyType);

        final PassengerCount.Topic.TemporalType temporalType = safeValueOf(PassengerCount.Topic.TemporalType.class,
                parts[index++]).orElseThrow(() -> new InvalidAPCTopicException("Unknown temporal type: " + topic));
        builder.setTemporalType(temporalType);

        final PassengerCount.Topic.EventType eventType = safeValueOf(PassengerCount.Topic.EventType.class,
                parts[index++]).orElseThrow(() -> new InvalidAPCTopicException("Unknown event type: " + topic));
        builder.setEventType(eventType);

        final String strTransportMode = parts[index++];
        if (strTransportMode != null && !strTransportMode.isEmpty()) {
            final PassengerCount.Topic.TransportMode transportMode = safeValueOf(
                    PassengerCount.Topic.TransportMode.class, strTransportMode)
                    .orElseThrow(() -> new InvalidAPCTopicException("Unknown transport mode: " + topic));
            builder.setTransportMode(transportMode);
        }

        builder.setOperatorId(Integer.parseInt(parts[index++]));
        builder.setVehicleNumber(Integer.parseInt(parts[index++]));

        return builder.build();
    }

    public ApcJson toJson(PassengerCount.Payload passengerCountPayload) {
        ApcJson apcJson = new ApcJson();
        apcJson.apc = new Apc();
        apcJson.apc.veh = passengerCountPayload.getVeh();
        apcJson.apc.desi = passengerCountPayload.getDesi();
        apcJson.apc.loc = passengerCountPayload.getLoc();
        apcJson.apc.dir = passengerCountPayload.getDir();
        apcJson.apc.oday = passengerCountPayload.getOday();
        apcJson.apc.oper = passengerCountPayload.getOper();
        apcJson.apc.route = passengerCountPayload.getRoute();
        apcJson.apc.start = passengerCountPayload.getStart();
        apcJson.apc.stop = passengerCountPayload.getStop();
        apcJson.apc.tst = new Date(passengerCountPayload.getTst());
        apcJson.apc.jrn = passengerCountPayload.getJrn();
        apcJson.apc.lat = passengerCountPayload.getLat();
        apcJson.apc.line = passengerCountPayload.getLine();
        apcJson.apc.lon = passengerCountPayload.getLong();
        apcJson.apc.odo = passengerCountPayload.getOdo();
        apcJson.apc.tsi = passengerCountPayload.getTsi();

        apcJson.apc.vehiclecounts = new Vehiclecounts();
        apcJson.apc.vehiclecounts.countquality = passengerCountPayload.getVehicleCounts().getCountQuality();
        apcJson.apc.vehiclecounts.vehicleload = passengerCountPayload.getVehicleCounts().getVehicleLoad();
        apcJson.apc.vehiclecounts.extensions = passengerCountPayload.getVehicleCounts().getExtensions();
        apcJson.apc.vehiclecounts.vehicleloadratio = passengerCountPayload.getVehicleCounts().getVehicleLoadRatio();
        apcJson.apc.vehiclecounts.doorcounts = new ArrayList<>();
        for (PassengerCount.DoorCount doorCount : passengerCountPayload.getVehicleCounts().getDoorCountsList()) {
            DoorCount dc = new DoorCount();
            dc.count = new ArrayList<>();
            for (PassengerCount.Count count : doorCount.getCountList()) {
                Count c = new Count();
                c.clazz = count.getClazz();
                c.in = count.getIn();
                c.out = count.getOut();
                dc.count.add(c);
            }
            dc.door = doorCount.getDoor();
            apcJson.apc.vehiclecounts.doorcounts.add(dc);
        }
        return apcJson;
    }

    public OutputStream serializeJson(ApcJson apcJson, OutputStream outputStream) throws IOException {
        dslJson.serialize(apcJson, outputStream);
        return outputStream;
    }

    @Nullable
    public ApcJson parseJson(byte @NotNull [] data) throws IOException, InvalidAPCPayloadException {
        try {
            return dslJson.deserialize(ApcJson.class, data, data.length);
        } catch (IOException ioe) {
            if (ioe instanceof ParsingException) {
                throw new PassengerCountParser.InvalidAPCPayloadException("Failed to parse APC JSON",
                        (ParsingException) ioe);
            } else {
                throw ioe;
            }
        }

    }

    private static OptionalDouble safeParseDouble(String s) {
        try {
            return OptionalDouble.of(Double.parseDouble(s));
        } catch (NumberFormatException nfe) {
            return OptionalDouble.empty();
        }
    }

    private static OptionalInt safeParseInt(String s) {
        try {
            return OptionalInt.of(Integer.parseInt(s));
        } catch (NumberFormatException nfe) {
            return OptionalInt.empty();
        }
    }

    static <E extends Enum<E>> Optional<E> safeValueOf(Class<E> enumType, String value) {
        try {
            return Optional.of(Enum.valueOf(enumType, value));
        } catch (IllegalArgumentException | NullPointerException e) {
            log.debug("Failed to parse value {} for enum {}", value, enumType.getCanonicalName());
            return Optional.empty();
        }
    }

    public static int findVersionIndex(@NotNull String[] parts) {
        for (int n = 0; n < parts.length; n++) {
            String p = parts[n];
            if (topicVersionRegex.matcher(p).matches()) {
                return n;
            }
        }
        return -1;
    }

    @NotNull
    static String joinFirstNParts(@NotNull String[] parts, int upToIndexExcludingThis, @NotNull String delimiter) {
        StringBuffer buffer = new StringBuffer();
        int index = 0;

        buffer.append(delimiter);
        while (index < upToIndexExcludingThis - 1) {
            index++;
            buffer.append(parts[index]);
            buffer.append(delimiter);
        }
        return buffer.toString();
    }

    public static class InvalidAPCTopicException extends Exception {
        private InvalidAPCTopicException(String message) {
            super(message);
        }
    }

    public static class InvalidAPCPayloadException extends Exception {
        private InvalidAPCPayloadException(String message, ParsingException cause) {
            super(message, cause);
        }
    }
}
