package fi.hsl.common.metro;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class MetroStopsTest {

    @Test
    public void getShortName() {
        assertEquals("MAK", MetroStops.getShortName("2314601").get());
        assertEquals("MAK", MetroStops.getShortName("2314602").get());
        assertFalse(MetroStops.getShortName("1234567").isPresent());
    }

    @Test
    public void getStopNumbers() {
        final List<String> stopNumbers = MetroStops.getStopNumbers("MAK");
        assertEquals(2, stopNumbers.size());
        assertEquals("2314601", stopNumbers.get(0));
        assertEquals("2314602", stopNumbers.get(1));
        assertTrue(MetroStops.getStopNumbers("FOO").isEmpty());
    }

    @Test
    public void getJoreDirection() {
        assertEquals(1, MetroStops.getJoreDirection("MAK", "VS").get().intValue());
        assertEquals(1, MetroStops.getJoreDirection("TAP", "MM").get().intValue());
        assertEquals(1, MetroStops.getJoreDirection("IK", "VS").get().intValue());
        assertEquals(1, MetroStops.getJoreDirection("IK", "MM").get().intValue());
        assertEquals(2, MetroStops.getJoreDirection("VS", "MAK").get().intValue());
        assertEquals(2, MetroStops.getJoreDirection("MM", "TAP").get().intValue());
        assertEquals(2, MetroStops.getJoreDirection("VS", "IK").get().intValue());
        assertEquals(2, MetroStops.getJoreDirection("MM", "IK").get().intValue());
        assertFalse(MetroStops.getJoreDirection("MAK", "MAK").isPresent());
        assertFalse(MetroStops.getJoreDirection("MAK", "FOO").isPresent());
        assertFalse(MetroStops.getJoreDirection("FOO", "MAK").isPresent());
    }

    @Test
    public void getStopNumber() {
        assertEquals("2211601", MetroStops.getStopNumber("TAP", "MAK", "VS").get());
        assertEquals("1020604", MetroStops.getStopNumber("HY", "MM", "TAP").get());
        assertFalse(MetroStops.getStopNumber("TAP", "MAK", "MAK").isPresent());
        assertFalse(MetroStops.getStopNumber("TAP", "MAK", "FOO").isPresent());
        assertFalse(MetroStops.getStopNumber("FOO", "MAK", "VS").isPresent());
    }
}
