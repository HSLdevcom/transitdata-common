package fi.hsl.common.passengercount;

import fi.hsl.common.hfp.HfpJson;
import fi.hsl.common.hfp.HfpParser;
import fi.hsl.common.passengercount.json.APC;
import fi.hsl.common.passengercount.json.APCJson;
import fi.hsl.common.passengercount.json.PassengerCountParser;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PassengerCountParserTest {

    @Test
    public void parseJson() throws Exception {
        PassengerCountParser parser = PassengerCountParser.newInstance();
        APC apc = parseJsonFromResources("src/test/resources/passenger-count-sample.json").apc;
        assertEquals("555", apc.desi);
        assertEquals(12, apc.oper);
        assertEquals("GPS", apc.loc);
        assertEquals("regular | defect | other", apc.vehiclecounts.countquality);
        assertEquals(15, apc.vehiclecounts.vehicleload);
        assertEquals(1, apc.vehiclecounts.doorcounts.size());
        assertEquals("door1", apc.vehiclecounts.doorcounts.get(0).door);

    }

    private APCJson parseJsonFromResources(String filename) throws Exception {
        byte[] data = Files.readAllBytes(Paths.get("src/test/resources/passenger-count-sample.json"));
        APCJson apcJson = PassengerCountParser.newInstance().parseJson(data);
        assertNotNull(apcJson);
        return apcJson;
    }
}
