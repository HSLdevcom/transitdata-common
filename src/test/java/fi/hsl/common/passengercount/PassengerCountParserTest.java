package fi.hsl.common.passengercount;

import fi.hsl.common.passengercount.json.APC;
import fi.hsl.common.passengercount.json.APCJson;
import fi.hsl.common.passengercount.proto.PassengerCount;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PassengerCountParserTest {

    @Test
    public void parseJsonTest() throws Exception {
        APC apc = parseJsonFromResources("src/test/resources/passenger-count-sample.json").apc;
        assertEquals("555", apc.desi);
        assertEquals(12, Integer.parseInt(apc.oper));
        assertEquals("GPS", apc.loc);
        assertEquals("regular | defect | other", apc.vehiclecounts.countquality);
        assertEquals(15, apc.vehiclecounts.vehicleload);
        assertEquals(1, apc.vehiclecounts.doorcounts.size());
        assertEquals("door1", apc.vehiclecounts.doorcounts.get(0).door);

    }

    private APCJson parseJsonFromResources(String filename) throws Exception {
        byte[] data = Files.readAllBytes(Paths.get(filename));
        APCJson apcJson = PassengerCountParser.newInstance().parseJson(data);
        assertNotNull(apcJson);
        return apcJson;
    }

    @Test
    public void convertJsonToProtobufMessageTest() throws Exception{
        APCJson apcJson = parseJsonFromResources("src/test/resources/passenger-count-sample.json");
        PassengerCount.Payload payload = PassengerCountParser.newInstance().parsePayload(apcJson).get();
        assertEquals("555", payload.getDesi());
        assertEquals(12, payload.getOper());
        assertEquals("GPS", payload.getLoc());
        assertEquals("regular | defect | other", payload.getVehicleCounts().getCountQuality());
        assertEquals(15, payload.getVehicleCounts().getVehicleLoad());
        assertEquals(1, payload.getVehicleCounts().getDoorCountsCount());
        assertEquals("door1", payload.getVehicleCounts().getDoorCounts(0).getDoor());
    }

    @Test
    public void convertProtobufToJsonTest() throws Exception{
        PassengerCountParser parser = PassengerCountParser.newInstance();
        APCJson apcJson = parseJsonFromResources("src/test/resources/passenger-count-sample.json");
        PassengerCount.Payload payload = parser.parsePayload(apcJson).get();
        APCJson newApcJson = parser.toJson(payload);
        assertEquals(apcJson.apc.desi, newApcJson.apc.desi);
        assertEquals(apcJson.apc.loc, newApcJson.apc.loc);
        assertEquals(apcJson.apc.dir, newApcJson.apc.dir);
        assertEquals(apcJson.apc.oday, newApcJson.apc.oday);
        assertEquals(apcJson.apc.start, newApcJson.apc.start);
        assertEquals(apcJson.apc.route, newApcJson.apc.route);
        assertEquals(apcJson.apc.tst, newApcJson.apc.tst);
        assertEquals(apcJson.apc.vehiclecounts.countquality, newApcJson.apc.vehiclecounts.countquality);
        assertEquals(apcJson.apc.vehiclecounts.extensions, newApcJson.apc.vehiclecounts.extensions);
        assertEquals(apcJson.apc.vehiclecounts.vehicleload, newApcJson.apc.vehiclecounts.vehicleload);
        assertEquals(apcJson.apc.vehiclecounts.doorcounts.get(0).door, newApcJson.apc.vehiclecounts.doorcounts.get(0).door);
        assertEquals(apcJson.apc.vehiclecounts.doorcounts.get(0).count.get(0).clazz, newApcJson.apc.vehiclecounts.doorcounts.get(0).count.get(0).clazz);
        assertEquals(apcJson.apc.vehiclecounts.doorcounts.get(0).count.get(0).in, newApcJson.apc.vehiclecounts.doorcounts.get(0).count.get(0).in);
    }

    @Test
    public void serializeJsonTest() throws Exception {
        OutputStream os = new ByteArrayOutputStream();
        APCJson apcJson = parseJsonFromResources("src/test/resources/passenger-count-sample.json");
        os = PassengerCountParser.newInstance().serializeJson(apcJson, os);
        String serializedJson = os.toString();
        os.close();
        APCJson parsedJson = PassengerCountParser.newInstance().parseJson(serializedJson.getBytes("UTF-8"));
        assertEquals(apcJson.apc.desi, parsedJson.apc.desi);
        assertEquals(apcJson.apc.loc, parsedJson.apc.loc);
        assertEquals(apcJson.apc.dir, parsedJson.apc.dir);
        assertEquals(apcJson.apc.oday, parsedJson.apc.oday);
        assertEquals(apcJson.apc.start, parsedJson.apc.start);
        assertEquals(apcJson.apc.route, parsedJson.apc.route);
        assertEquals(apcJson.apc.tst, parsedJson.apc.tst);
        assertEquals(apcJson.apc.vehiclecounts.countquality, parsedJson.apc.vehiclecounts.countquality);
        assertEquals(apcJson.apc.vehiclecounts.extensions, parsedJson.apc.vehiclecounts.extensions);
        assertEquals(apcJson.apc.vehiclecounts.vehicleload, parsedJson.apc.vehiclecounts.vehicleload);
        assertEquals(apcJson.apc.vehiclecounts.doorcounts.get(0).door, parsedJson.apc.vehiclecounts.doorcounts.get(0).door);
        assertEquals(apcJson.apc.vehiclecounts.doorcounts.get(0).count.get(0).clazz, parsedJson.apc.vehiclecounts.doorcounts.get(0).count.get(0).clazz);
        assertEquals(apcJson.apc.vehiclecounts.doorcounts.get(0).count.get(0).in, parsedJson.apc.vehiclecounts.doorcounts.get(0).count.get(0).in);
    }
}
