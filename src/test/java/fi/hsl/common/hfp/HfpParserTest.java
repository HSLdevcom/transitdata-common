package fi.hsl.common.hfp;

import fi.hsl.common.hfp.proto.Hfp;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.Scanner;

import static org.junit.Assert.*;

public class HfpParserTest {

    @Test
    public void parseTimestampSafely() {
        Timestamp ts = HfpParser.safeParseTimestamp("2018-04-05T17:38:36Z").get();
        assertEquals(1522949916000L, ts.getTime());

        assertFalse(HfpParser.safeParseTimestamp("2018-04-05T17:38:36").isPresent());//Missing time zone

        assertFalse(HfpParser.safeParseTimestamp("datetime").isPresent());
        assertFalse(HfpParser.safeParseTimestamp(null).isPresent());
    }

    @Test
    public void parseSampleVpJsonFile() throws Exception {
        HfpJson hfp = parseJsonFromResources("hfp-sample-vp.json");
        assertEquals("81", hfp.payload.desi);
        assertEquals("2", hfp.payload.dir);
        assertTrue(22 == hfp.payload.oper);
        assertTrue(792 == hfp.payload.veh);
        assertEquals("2018-04-05T17:38:36Z", hfp.payload.tst);
        assertTrue(1522949916 == hfp.payload.tsi);
        assertTrue(0.16 - hfp.payload.spd < 0.00001f);
        assertTrue(225 == hfp.payload.hdg);
        assertTrue(60.194481 - hfp.payload.lat < 0.00001f);
        assertTrue(25.03095 - hfp.payload.longitude < 0.00001f);
        assertTrue(0 == hfp.payload.acc);
        assertTrue(-25 == hfp.payload.dl);
        assertTrue(2819 == hfp.payload.odo);
        assertTrue(0 == hfp.payload.drst);
        assertEquals("2018-04-05", hfp.payload.oday);
        assertTrue(636 == hfp.payload.jrn);
        assertTrue(112 == hfp.payload.line);
        assertEquals("20:25", hfp.payload.start);
        assertTrue(10 == hfp.payload.occu);
        assertTrue(1 == hfp.payload.seq);
        assertEquals("2018-04-05T17:38:36Z", hfp.payload.ttarr);
        assertEquals("2018-04-05T17:38:36Z", hfp.payload.ttdep);
        assertTrue(0 == hfp.payload.dr_type);
    }

    @Test
    public void parseSamplePasJsonFile() throws Exception {
        HfpJson hfp = parseJsonFromResources("hfp-sample-pas.json");
        assertEquals("413", hfp.payload.desi);
        assertEquals("2", hfp.payload.dir);
        assertEquals(22, (int)hfp.payload.oper);
        assertEquals(817, (int)hfp.payload.veh);
        assertEquals("2019-06-27T11:53:24.541Z", hfp.payload.tst);
        assertEquals(1561636404, hfp.payload.tsi);
        assertEquals(11.33, hfp.payload.spd, 0.00001f);
        assertEquals(245, (int)hfp.payload.hdg);
        assertEquals(60.274556, hfp.payload.lat, 0.00001f);
        assertEquals(24.840979, hfp.payload.longitude, 0.00001f);
        assertEquals(-0.15, hfp.payload.acc, 0.00001f);
        assertEquals(102, (int)hfp.payload.dl);
        assertEquals(15160, hfp.payload.odo, 0.00001f);
        assertEquals(0, (int)hfp.payload.drst);
        assertEquals("2019-06-27", hfp.payload.oday);
        assertEquals(6, (int)hfp.payload.jrn);
        assertEquals(835, (int)hfp.payload.line);
        assertEquals("14:05", hfp.payload.start);
        assertEquals("GPS", hfp.payload.loc);
        assertEquals(4170203, (int)hfp.payload.stop);
        assertEquals("4413", hfp.payload.route);
        assertEquals(0, (int)hfp.payload.occu);
    }

    @Test
    public void parseSampleTlrJsonFile() throws Exception {
        HfpJson hfp = parseJsonFromResources("hfp-tlr-sample.json");
        assertEquals("112", hfp.payload.desi);
        assertEquals("1", hfp.payload.dir);
        assertTrue(22 == hfp.payload.oper);
        assertTrue(754 == hfp.payload.veh);
        assertEquals("2019-10-21T07:16:36.992Z", hfp.payload.tst);
        assertTrue(1571642196 == hfp.payload.tsi);
        assertTrue(11.51 - hfp.payload.spd < 0.00001f);
        assertTrue(241 == hfp.payload.hdg);
        assertTrue(60.174129 - hfp.payload.lat < 0.00001f);
        assertTrue(24.796323 - hfp.payload.longitude < 0.00001f);
        assertTrue(0.58 == hfp.payload.acc);
        assertTrue(-28 == hfp.payload.dl);
        assertTrue(653 == hfp.payload.odo);
        assertTrue(0 == hfp.payload.drst);
        assertEquals("2019-10-21", hfp.payload.oday);
        assertTrue(102 == hfp.payload.jrn);
        assertTrue(227 == hfp.payload.line);
        assertEquals("10:15", hfp.payload.start);
        assertEquals("GPS", hfp.payload.loc);
        assertEquals(null, hfp.payload.stop);
        assertEquals("2112", hfp.payload.route);
        assertTrue(0 == hfp.payload.occu);
        assertTrue(2107 == hfp.payload.sid);
        assertTrue(894 == hfp.payload.signal_groupid);
        assertTrue(1 == hfp.payload.tlp_signalgroupnbr);
        assertEquals("NORMAL", hfp.payload.tlp_requesttype);
        assertTrue(100 == hfp.payload.tlp_requestid);
        assertEquals("normal", hfp.payload.tlp_prioritylevel);
        assertEquals(null, hfp.payload.tlp_line_configid);
        assertEquals(null, hfp.payload.tlp_point_configid);
        assertTrue(461200 == hfp.payload.tlp_frequency);
        assertEquals("KAR", hfp.payload.tlp_protocol);
        assertTrue(1 == hfp.payload.tlp_att_seq);
        assertEquals("AHEAD", hfp.payload.tlp_reason);
    }

    @Test
    public void parseValidTlrJsonFileToProtobuf() throws Exception {
        HfpJson json = parseJsonFromResources("hfp-tlr-sample.json");
        Hfp.Payload hfp = HfpParser.parsePayload(json);
        assertEquals("10:15", hfp.getStart());
        assertTrue(true == hfp.hasLoc());
        assertTrue(Hfp.Payload.LocationQualityMethod.GPS == hfp.getLoc());
        assertTrue(0 == hfp.getStop());
        assertEquals("2112", hfp.getRoute());
        assertTrue(0 == hfp.getOccu());
        assertTrue(2107 == hfp.getSid());
        assertTrue(894 == hfp.getSignalGroupid());
        assertTrue(1 == hfp.getTlpSignalgroupnbr());
        assertTrue(true == hfp.hasTlpRequesttype());
        assertEquals(Hfp.Payload.TlpRequestType.NORMAL, hfp.getTlpRequesttype());
        assertTrue(100 == hfp.getTlpRequestid());
        assertTrue(true == hfp.hasTlpPrioritylevel());
        assertEquals(Hfp.Payload.TlpPriorityLevel.normal, hfp.getTlpPrioritylevel());
        assertEquals(false, hfp.hasTlpLineConfigid());
        assertEquals(false, hfp.hasTlpPointConfigid());
        assertTrue(461200 == hfp.getTlpFrequency());
        assertEquals("KAR", hfp.getTlpProtocol());
        assertTrue(1 == hfp.getTlpAttSeq());
        assertTrue(true == hfp.hasTlpReason());
        assertEquals(Hfp.Payload.TlpReason.AHEAD, hfp.getTlpReason());
        assertTrue(true == hfp.hasTlpDecision());
        assertEquals(Hfp.Payload.TlpDecision.NAK, hfp.getTlpDecision());
    }

    @Test
    public void parseInvalidTlrFieldsJsonFileToProtobuf() throws Exception {
        HfpJson json = parseJsonFromResources("hfp-tlr-sample-invalid.json");
        Hfp.Payload hfp = HfpParser.parsePayload(json);
        assertEquals(false, hfp.hasLoc());
        assertEquals(false, hfp.hasTlpRequesttype());
        assertEquals(false, hfp.hasTlpPrioritylevel());
        assertEquals(false, hfp.hasTlpProtocol());
        assertEquals(false, hfp.hasTlpReason());
        assertEquals(false, hfp.hasTlpDecision());
    }

     @Test
    public void parseMissingTlrFiledsJsonFileToProtobuf() throws Exception {
        HfpJson json = parseJsonFromResources("hfp-tlr-sample-missing.json");
        Hfp.Payload hfp = HfpParser.parsePayload(json);
        assertEquals(false, hfp.hasLoc());
        assertEquals(false, hfp.hasTlpRequesttype());
        assertEquals(false, hfp.hasTlpPrioritylevel());
        assertEquals(false, hfp.hasTlpProtocol());
        assertEquals(false, hfp.hasTlpReason());
        assertEquals(false, hfp.hasTlpDecision());
    }

    @Test
    public void parseSampleJsonFileToProtobuf() throws Exception {
        HfpJson json = parseJsonFromResources("hfp-sample-vp.json");
        Hfp.Payload hfp = HfpParser.parsePayload(json);

        assertEquals("81", hfp.getDesi());
        assertEquals("2", hfp.getDir());
        assertTrue(22 == hfp.getOper());
        assertTrue(792 == hfp.getVeh());
        assertEquals("2018-04-05T17:38:36Z", hfp.getTst());
        assertTrue(1522949916 == hfp.getTsi());
        assertTrue(0.16 - hfp.getSpd() < 0.00001f);
        assertTrue(225 == hfp.getHdg());
        assertTrue(60.194481 - hfp.getLat() < 0.00001f);
        assertTrue(25.03095 - hfp.getLong() < 0.00001f);
        assertTrue(0 == hfp.getAcc());
        assertTrue(-25 == hfp.getDl());
        assertTrue(2819 == hfp.getOdo());
        assertTrue(0 == hfp.getDrst());
        assertEquals("2018-04-05", hfp.getOday());
        assertTrue(636 == hfp.getJrn());
        assertTrue(112 == hfp.getLine());
        assertEquals("20:25", hfp.getStart());
        assertTrue(10 == hfp.getOccu());
        assertTrue(1 == hfp.getSeq());
        assertEquals("2018-04-05T17:38:36Z", hfp.getTtarr());
        assertEquals("2018-04-05T17:38:36Z", hfp.getTtdep());
        assertTrue(0 == hfp.getDrType());
    }

    @Test
    public void parseGarbageDataToProtobuf() throws Exception {
        HfpJson json = parseJsonFromResources("garbage-data.json");
        Hfp.Payload hfp = HfpParser.parsePayload(json);

        assertFalse(hfp.hasDesi());
        assertFalse(hfp.hasDir());
        assertFalse(hfp.hasOper());
        assertFalse(hfp.hasVeh());
        assertEquals("2018-04-05T17:38:36Z", hfp.getTst());
        assertTrue(1522949916 == hfp.getTsi());
        assertTrue(-10.0 - hfp.getSpd() < 0.00001f);
        assertFalse(hfp.hasHdg());
        assertFalse(hfp.hasLat());
        assertTrue(25.03095 - hfp.getLong() < 0.00001f);
        assertTrue(100 - hfp.getAcc() < 0.00001f);
        assertTrue(-25 == hfp.getDl());
        assertTrue(2819 == hfp.getOdo());
        assertTrue(-100 == hfp.getDrst());
        assertFalse(hfp.hasOday());
        assertTrue(636 == hfp.getJrn());
        assertTrue(112 == hfp.getLine());
        assertEquals("0:0", hfp.getStart());
    }

    private HfpJson parseJsonFromResources(String filename) throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource(filename);

        String content = new Scanner(url.openStream(), "UTF-8").useDelimiter("\\A").next();

        HfpJson hfp = HfpParser.newInstance().parseJson(content.getBytes("UTF-8"));
        assertNotNull(hfp);
        return hfp;
    }

    @Test(expected = HfpParser.InvalidHfpPayloadException.class)
    public void parsingInvalidHfpPayloadThrowsException() throws IOException, HfpParser.InvalidHfpPayloadException {
        HfpJson hfpJson = HfpParser.newInstance().parseJson("{\"VP\":{\"tst\":null}}".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void parseTopic() throws Exception {
        Hfp.Topic meta = parseAndValidateTopic("/hfp/v1/journey/ongoing/bus/0022/00854/4555B/2/Leppävaara/19:56/4150264/5/60;24/28/65/06");
        assertEquals(Hfp.Topic.JourneyType.journey, meta.getJourneyType());
        assertEquals(Hfp.Topic.TemporalType.ongoing, meta.getTemporalType());
        assertEquals(Hfp.Topic.TransportMode.bus, meta.getTransportMode());
        assertEquals(22, meta.getOperatorId());
        assertEquals(854, meta.getVehicleNumber());
        assertEquals(HfpParser.createUniqueVehicleId(22, 854), meta.getUniqueVehicleId());

        assertEquals("4555B", meta.getRouteId());
        assertEquals(2, (int)meta.getDirectionId());
        assertEquals("Leppävaara", meta.getHeadsign());
        assertEquals(LocalTime.of(19, 56), HfpParser.safeParseLocalTime(meta.getStartTime()).get());
        assertEquals("4150264", meta.getNextStop());
        assertEquals(5, meta.getGeohashLevel());

        assertTrue(60.260 - meta.getLatitude() < 0.00001);
        assertTrue(24.856 - meta.getLongitude() < 0.00001);
    }

    @Test
    public void parseMissingGeohash() throws Exception {
        ///hfp/v1/journey/ongoing/bus/0012/01328/4560/1/Myyrmäki/04:57/4160299/0////
        Hfp.Topic meta = parseAndValidateTopic("/hfp/v1/journey/ongoing/bus/0022/00854/4555B/2/Leppävaara/19:56/4150264/0////");
        assertEquals(0, meta.getGeohashLevel());
        assertFalse(meta.hasLatitude());
        assertFalse(meta.hasLongitude());
    }

    @Test
    public void parseGeohashWithOverloadedZeroLevel() throws Exception {
        Hfp.Topic meta = parseAndValidateTopic("/hfp/v1/journey/ongoing/bus/0012/01825/1039/2/Kamppi/05:36/1320105/0/60;24/28/44/12");
        assertEquals(0, meta.getGeohashLevel());
        assertTrue(60.241 - meta.getLatitude() < 0.00001);
        assertTrue(24.842 - meta.getLongitude() < 0.00001);
    }

    @Test
    public void parseTopicWhenItemsMissing() throws Exception {
        Hfp.Topic meta = parseAndValidateTopic("/hfp/v1/journey/ongoing/bus/0022/00854//////////");
        assertEquals(Hfp.Topic.JourneyType.journey, meta.getJourneyType());
        assertEquals(Hfp.Topic.TemporalType.ongoing, meta.getTemporalType());
        assertEquals(Hfp.Topic.TransportMode.bus, meta.getTransportMode());

        assertEquals(22, meta.getOperatorId());
        assertEquals(854, meta.getVehicleNumber());
        assertEquals(HfpParser.createUniqueVehicleId(22, 854), meta.getUniqueVehicleId());

        assertFalse(meta.hasRouteId());
        assertFalse(meta.hasDirectionId());
        assertFalse(meta.hasHeadsign());
        assertFalse(meta.hasStartTime());
        assertFalse(meta.hasNextStop());
        assertFalse(meta.hasGeohashLevel());

        assertFalse(meta.hasLatitude());
        assertFalse(meta.hasLongitude());
    }

    @Test
    public void parseTopicWhenPrefixLonger() throws Exception {
        Hfp.Topic meta = parseAndValidateTopic("/hsldevcom/public/hfp/v1/deadrun/upcoming/tram/0022/00854////08:08///60;24/28/65/06");
        assertEquals(Hfp.Topic.JourneyType.deadrun, meta.getJourneyType());
        assertEquals(Hfp.Topic.TemporalType.upcoming, meta.getTemporalType());
        assertEquals(Hfp.Topic.TransportMode.tram, meta.getTransportMode());

        assertEquals(22, meta.getOperatorId());
        assertEquals(854, meta.getVehicleNumber());
        assertEquals(HfpParser.createUniqueVehicleId(22, 854), meta.getUniqueVehicleId());

        assertFalse(meta.hasRouteId());
        assertFalse(meta.hasDirectionId());
        assertFalse(meta.hasHeadsign());
        assertEquals(LocalTime.of(8, 8), HfpParser.safeParseLocalTime(meta.getStartTime()).get());
        assertFalse(meta.hasNextStop());
        assertFalse(meta.hasGeohashLevel());

        assertTrue(60.260 - meta.getLatitude() < 0.00001);
        assertTrue(24.856 - meta.getLongitude() < 0.00001);

    }

    private Hfp.Topic parseAndValidateTopic(String topic) throws Exception {
        long now = System.currentTimeMillis();
        Hfp.Topic hfpTopic = HfpParser.parseTopic(topic, now);
        assertEquals(now, hfpTopic.getReceivedAt());
        assertEquals("v1", hfpTopic.getTopicVersion());
        return hfpTopic;
    }

    @Test
    public void testTopicPrefixParsing() throws Exception {
        String prefix = parseTopicPrefix("/hfp/v1/journey/ongoing/bus/0022/00854/4555B/2/Leppävaara/19:56/4150264/5/60;24/28/65/06");
        assertEquals("/hfp", prefix);
        String emptyPrefix = parseTopicPrefix("/v1/journey/ongoing/bus/0022/00854/4555B/2/Leppävaara/19:56/4150264/5/60;24/28/65/06");
        assertEquals("/", emptyPrefix);
        String longerPrefix = parseTopicPrefix("/hsldevcomm/public/hfp/v1/journey/ongoing/bus/0022/00854/4555B/2/Leppävaara/19:56/4150264/5/60;24/28/65/06");
        assertEquals("/hsldevcomm/public/hfp", longerPrefix);

    }

    @Test
    public void testSafeValueOfWithValidValue() {
        assertTrue(HfpParser.safeValueOf(Hfp.Topic.JourneyType.class, "journey").isPresent());
    }

    @Test
    public void testSafeValueOfWithInvalidValue() {
        assertFalse(HfpParser.safeValueOf(Hfp.Topic.JourneyType.class, "invalid_journey_type").isPresent());
    }

    private String parseTopicPrefix(String topic) throws Exception {
        final String[] allParts = topic.split("/");
        int versionIndex = HfpParser.findVersionIndex(allParts);
        return HfpParser.joinFirstNParts(allParts, versionIndex, "/");
    }
}
