package fi.hsl.common.metro;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class MetroStopsTest {

    @Test
    public void getShortName() {
        assertEquals("MAK", MetroStops.getShortName("2314601"));
        assertEquals("MAK", MetroStops.getShortName("2314602"));
    }

    @Test
    public void getStopNumbers() {
        final List<String> stopNumbers = MetroStops.getStopNumbers("MAK");
        assertEquals(stopNumbers.size(), 2);
        assertEquals(stopNumbers.get(0), "2314601");
        assertEquals(stopNumbers.get(1), "2314602");
    }
}
