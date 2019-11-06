package fi.hsl.common.hfp;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.ParsingException;
import com.dslplatform.json.runtime.Settings;
import fi.hsl.common.hfp.proto.Hfp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.regex.Pattern;
import java.io.ByteArrayOutputStream;

public class HfpParser {
    private static final Logger log = LoggerFactory.getLogger(HfpParser.class);

    static final Pattern topicVersionRegex = Pattern.compile("(^v\\d+|dev)");

    // Let's use dsl-json (https://github.com/ngs-doo/dsl-json) for performance.
    // Based on this benchmark: https://github.com/fabienrenaud/java-json-benchmark

    //Example: https://github.com/ngs-doo/dsl-json/blob/master/examples/MavenJava8/src/main/java/com/dslplatform/maven/Example.java

    //Note! Apparently not thread safe, for per thread reuse use ThreadLocal pattern or create separate instances
    final DslJson<Object> dslJson = new DslJson<>(Settings.withRuntime().allowArrayFormat(true).includeServiceLoader());

    public static HfpParser newInstance() {
        return new HfpParser();
    }

    /**
     * Methods for parsing the Json Payload
     **/

    public HfpJson parseJson(byte[] data) throws IOException, InvalidHfpPayloadException {
        try {
            return dslJson.deserialize(HfpJson.class, data, data.length);
        } catch (IOException ioe) {
            if (ioe instanceof ParsingException) {
                throw new InvalidHfpPayloadException("Failed to parse HFP JSON", (ParsingException)ioe);
            } else {
                throw ioe;
            }
        }
    }

    public String serializeToString(final HfpJson json) throws IOException {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        dslJson.serialize(json, os);
        return os.toString("UTF-8");
    }

    public byte[] serializeToByteArray(final HfpJson json) throws IOException {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        dslJson.serialize(json, os);
        return os.toByteArray();
    }

    public Optional<Hfp.Payload> safeParse(byte[] data) {
        try {
            HfpJson json = parseJson(data);
            return Optional.of(parsePayload(json));
        }
        catch (Exception e) {
            log.error("Failed to parse Json message {}", new String(data));
            return Optional.empty();
        }
    }

    public static Hfp.Payload parsePayload(HfpJson json) {
        final HfpJson.Payload payload = json.payload;

        Hfp.Payload.Builder builder = Hfp.Payload.newBuilder();
        // Required attributes
        builder.setSchemaVersion(builder.getSchemaVersion());
        HfpValidator.validateString(payload.tst).ifPresent(builder::setTst); // TODO add validation for offsetdatetime format
        builder.setTsi(payload.tsi);

        // Optional attributes
        HfpValidator.validateString(payload.desi).ifPresent(builder::setDesi);
        HfpValidator.validateString(payload.dir).ifPresent(builder::setDir);
        if (payload.oper != null)
            builder.setOper(payload.oper);
        if (payload.veh != null)
            builder.setVeh(payload.veh);
        if (payload.spd != null)
            builder.setSpd(payload.spd);
        if (payload.hdg != null)
            builder.setHdg(payload.hdg);
        if (payload.lat != null)
            builder.setLat(payload.lat);
        if (payload.longitude != null)
            builder.setLong(payload.longitude);
        if (payload.acc != null)
            builder.setAcc(payload.acc);
        if (payload.dl != null)
            builder.setDl(payload.dl);
        if (payload.odo != null)
            builder.setOdo(payload.odo);
        if (payload.drst != null)
            builder.setDrst(payload.drst);
        HfpValidator.validateString(payload.oday).ifPresent(builder::setOday); // TODO add validation for datetime format
        if (payload.jrn != null)
            builder.setJrn(payload.jrn);
        if (payload.line != null)
            builder.setLine(payload.line);
        HfpValidator.validateString(payload.start).ifPresent(builder::setStart); // TODO add validation for localtime format

        if (HfpValidator.validateLocationQualityMethod(payload.loc).isPresent()) {
            final String locStr = payload.loc.equals("N/A") ? "NA" : payload.loc;
            builder.setLoc(Hfp.Payload.LocationQualityMethod.valueOf(locStr));
        }
        if (payload.stop != null)
            builder.setStop(payload.stop);
        HfpValidator.validateString(payload.route).ifPresent(builder::setRoute);
        if (payload.occu != null)
            builder.setOccu(payload.occu);
        if (payload.seq != null)
            builder.setSeq(payload.seq);
        HfpValidator.validateString(payload.ttarr).ifPresent(builder::setTtarr);
        HfpValidator.validateString(payload.ttdep).ifPresent(builder::setTtdep);
        if (payload.dr_type != null)
            builder.setDrType(payload.dr_type);
        if (payload.tlp_requestid != null)
            builder.setTlpRequestid(payload.tlp_requestid);
        HfpValidator.validateTlpRequestType(payload.tlp_requesttype).ifPresent(builder::setTlpRequesttype);
        HfpValidator.validateTlpPriorityLevel(payload.tlp_prioritylevel).ifPresent(builder::setTlpPrioritylevel);
        HfpValidator.validateTlpReason(payload.tlp_reason).ifPresent(builder::setTlpReason);
        if (payload.tlp_att_seq != null)
            builder.setTlpAttSeq(payload.tlp_att_seq);
        HfpValidator.validateTlpDecision(payload.tlp_decision).ifPresent(builder::setTlpDecision);
        if (payload.sid != null)
            builder.setSid(payload.sid);
        if (payload.signal_groupid != null)
            builder.setSignalGroupid(payload.signal_groupid);
        if (payload.tlp_signalgroupnbr != null)
            builder.setTlpSignalgroupnbr(payload.tlp_signalgroupnbr);
        if (payload.tlp_line_configid != null)
            builder.setTlpLineConfigid(payload.tlp_line_configid);
        if (payload.tlp_frequency != null)
            builder.setTlpFrequency(payload.tlp_frequency);
        HfpValidator.validateString(payload.tlp_protocol).ifPresent(builder::setTlpProtocol);
        return builder.build();
    }

    /**
     * Methods for parsing the data from the topic
     */

    public static Optional<Hfp.Topic> safeParseTopic(String topic) {
        try {
            return Optional.of(parseTopic(topic));
        }
        catch (Exception e) {
            log.error("Failed to parse topic " + topic, e);
            return Optional.empty();
        }
    }

    public static Optional<Hfp.Topic> safeParseTopic(String topic, long receivedAtMs) {
        try {
            return Optional.of(parseTopic(topic, receivedAtMs));
        }
        catch (Exception e) {
            log.error("Failed to parse topic " + topic, e);
            return Optional.empty();
        }
    }

    public static Hfp.Topic parseTopic(String topic) throws InvalidHfpTopicException {
        return parseTopic(topic, System.currentTimeMillis());
    }

    public static Hfp.Topic parseTopic(String topic, long receivedAtMs) throws InvalidHfpTopicException {
        //log.debug("Parsing metadata from topic: " + topic);

        final String[] parts = topic.split("/", -1);//-1 to include empty substrings

        final Hfp.Topic.Builder builder = Hfp.Topic.newBuilder();
        builder.setSchemaVersion(builder.getSchemaVersion());

        builder.setReceivedAt(receivedAtMs);
        //We first find the index of version. The prefix topic part can consist of more complicated path
        int versionIndex = findVersionIndex(parts);
        if (versionIndex < 0) {
            throw new InvalidHfpTopicException("Failed to find topic version from topic " + topic);
        }
        builder.setTopicPrefix(joinFirstNParts(parts, versionIndex, "/"));
        int index = versionIndex;
        final String versionStr = parts[index++];
        builder.setTopicVersion(versionStr);

        builder.setJourneyType(Hfp.Topic.JourneyType.valueOf(parts[index++]));
        builder.setTemporalType(Hfp.Topic.TemporalType.valueOf(parts[index++]));

        if (versionStr.equals("v2")) {
            final String eventTypeStr = parts[index++];
            if (eventTypeStr != null && !eventTypeStr.isEmpty()) {
                builder.setEventType(Hfp.Topic.EventType.valueOf(eventTypeStr.toUpperCase()));
            }
        }

        final String strTransportMode = parts[index++];
        if (strTransportMode != null && !strTransportMode.isEmpty()) {
            builder.setTransportMode(Hfp.Topic.TransportMode.valueOf(strTransportMode));
        }
        builder.setOperatorId(Integer.parseInt(parts[index++]));
        builder.setVehicleNumber(Integer.parseInt(parts[index++]));
        builder.setUniqueVehicleId(createUniqueVehicleId(builder.getOperatorId(), builder.getVehicleNumber()));
        if (index + 6 <= parts.length) {
            HfpValidator.validateString(parts[index++]).ifPresent(builder::setRouteId);
            safeParseInt(parts[index++]).ifPresent(builder::setDirectionId);
            HfpValidator.validateString(parts[index++]).ifPresent(builder::setHeadsign);
            HfpValidator.validateString(parts[index++]).ifPresent(builder::setStartTime);
            HfpValidator.validateString(parts[index++]).ifPresent(builder::setNextStop);
            safeParseInt(parts[index++]).ifPresent(builder::setGeohashLevel);
        }
        else {
            log.debug("could not parse Json's first batch of additional fields for topic {}", topic);
        }
        if (index + 4 <= parts.length) {
            Optional<GeoHash> maybeGeoHash = parseGeoHash(parts, index);
            maybeGeoHash.map(hash -> hash.latitude).ifPresent(builder::setLatitude);
            maybeGeoHash.map(hash -> hash.longitude).ifPresent(builder::setLongitude);
        }
        else {
            log.debug("could not parse Json's second batch of additional fields (geohash) for topic {}", topic);
        }
        return builder.build();
    }

    public static class GeoHash {
        public double latitude;
        public double longitude;
    }

    static Optional<GeoHash> parseGeoHash(String[] parts, int startIndex) {
        Optional<GeoHash> maybeGeoHash = Optional.empty();

        int index = startIndex;
        final String firstLatLong = parts[index++];
        if (!firstLatLong.isEmpty()) {
            String[] latLong0 = firstLatLong.split(";");
            if (latLong0.length == 2) {
                StringBuffer latitude = new StringBuffer(latLong0[0]).append(".");
                StringBuffer longitude = new StringBuffer(latLong0[1]).append(".");

                String latLong1 = parts[index++];
                latitude.append(latLong1.substring(0, 1));
                longitude.append(latLong1.substring(1, 2));

                String latLong2 = parts[index++];
                latitude.append(latLong2.substring(0, 1));
                longitude.append(latLong2.substring(1, 2));

                String latLong3 = parts[index++];
                latitude.append(latLong3.substring(0, 1));
                longitude.append(latLong3.substring(1, 2));

                GeoHash geoHash = new GeoHash();
                geoHash.latitude = Double.parseDouble(latitude.toString());
                geoHash.longitude = Double.parseDouble(longitude.toString());
                maybeGeoHash = Optional.of(geoHash);
            }
            else {
                log.debug("Could not parse latitude & longitude from {}", firstLatLong);
            }
        }

        return maybeGeoHash;
    }


    static String createUniqueVehicleId(int ownerOperatorId, int vehicleNumber) {
        return ownerOperatorId + "/" + vehicleNumber;
    }

    static String joinFirstNParts(String[] parts, int upToIndexExcludingThis, String delimiter) {
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

    public static int findVersionIndex(String[] parts) {
        for (int n = 0; n < parts.length; n++) {
            String p = parts[n];
            if (topicVersionRegex.matcher(p).matches()) {
                return n;
            }
        }
        return -1;
    }

    public static Optional<Integer> safeParseInt(String n) {
        if (n == null || n.isEmpty())
            return Optional.empty();
        else {
            try {
                return Optional.of(Integer.parseInt(n));
            }
            catch (NumberFormatException e) {
                log.error("Failed to convert {} to integer", n);
                return Optional.empty();
            }
        }
    }

    public static Optional<Boolean> safeParseBoolean(Integer n) {
        if (n == null)
            return Optional.empty();
        else
            return Optional.of(n != 0);
    }

    public static Optional<Date> safeParseDate(String date) {
        if (date == null)
            return Optional.empty();
        else {
            try {
                return Optional.of(Date.valueOf(date));
            }
            catch (Exception e) {
                log.error("Failed to convert {} to java.sql.Date", date);
                return Optional.empty();
            }
        }
    }

    public static Optional<LocalTime> safeParseLocalTime(String time) {
        if (time == null)
            return Optional.empty();
        else {
            try {
                return Optional.of(LocalTime.parse(time));
            }
            catch (Exception e) {
                log.error("Failed to convert {} to LocalTime", time);
                return Optional.empty();
            }
        }
    }

    public static Optional<Time> safeParseTime(String time) {
        if (time == null) {
            return Optional.empty();
        } else {
            try {
                return Optional.of(Time.valueOf(time + ":00"));
            } catch (Exception var2) {
                log.error("Failed to convert {} to java.sql.Time", time);
                return Optional.empty();
            }
        }
    }

    public static Optional<Timestamp> safeParseTimestamp(String dt) {
        if (dt == null)
            return Optional.empty();
        else {
            try {
                OffsetDateTime offsetDt = OffsetDateTime.parse(dt);
                return Optional.of(new Timestamp(offsetDt.toEpochSecond() * 1000L));
            }
            catch (Exception e) {
                log.error("Failed to convert {} to java.sql.Timestamp", dt);
                return Optional.empty();
            }
        }
    }

    public static class InvalidHfpTopicException extends Exception {
        private InvalidHfpTopicException(String message) {
            super(message);
        }
    }

    public static class InvalidHfpPayloadException extends Exception {
        private InvalidHfpPayloadException(String message, ParsingException cause) {
            super(message, cause);
        }
    }
}
