package fi.hsl.common.gtfsrt;

import org.junit.Test;
import java.text.ParseException;
import static org.junit.Assert.*;

public class ThirtyHourTimeTest {
    @Test
    public void testTimeStringToSeconds1() throws ParseException {
        assertEquals(0, ThirtyHourTime.timeStringToSeconds("00:00:00"));
    }

    @Test
    public void testTimeStringToSeconds2() throws ParseException {
        assertEquals(45296, ThirtyHourTime.timeStringToSeconds("12:34:56"));
    }

    @Test
    public void testTimeStringToSeconds3() throws ParseException {
        assertEquals(131696, ThirtyHourTime.timeStringToSeconds("36:34:56"));
    }

    @Test(expected = ParseException.class)
    public void testTimeStringToSecondsException1() throws ParseException {
        ThirtyHourTime.timeStringToSeconds("asd");
    }

    @Test(expected = ParseException.class)
    public void testTimeStringToSecondsException2() throws ParseException {
        ThirtyHourTime.timeStringToSeconds("-12:34:56");
    }

    @Test(expected = ParseException.class)
    public void testTimeStringToSecondsException3() throws ParseException {
        ThirtyHourTime.timeStringToSeconds("12.0:34:56");
    }

    @Test(expected = ParseException.class)
    public void testTimeStringToSecondsException4() throws ParseException {
        ThirtyHourTime.timeStringToSeconds("12:60:00");
    }

    @Test(expected = ParseException.class)
    public void testTimeStringToSecondsException5() throws ParseException {
        ThirtyHourTime.timeStringToSeconds("1:23:45");
    }

    @Test(expected = ParseException.class)
    public void testTimeStringToSecondsException6() throws ParseException {
        ThirtyHourTime.timeStringToSeconds("12:3:45");
    }

    @Test
    public void testSecondsToTimeString1() {
        assertEquals("00:00:00", ThirtyHourTime.secondsToTimeString(0));
    }

    @Test
    public void testSecondsToTimeString2() {
        assertEquals("12:34:56", ThirtyHourTime.secondsToTimeString(45296));
    }

    @Test
    public void testSecondsToTimeString3() {
        assertEquals("36:34:56", ThirtyHourTime.secondsToTimeString(131696));
    }

    @Test
    public void testThirtyHourTime1() throws ParseException {
        final ThirtyHourTime time = new ThirtyHourTime(0, "12:34:56");
        assertEquals("12:34:56", time.getTimeString());
    }

    @Test
    public void testThirtyHourTime2() throws ParseException {
        final ThirtyHourTime time = new ThirtyHourTime(45296, "12:34:56");
        assertEquals("12:34:56", time.getTimeString());
    }

    @Test
    public void testThirtyHourTime3() throws ParseException {
        final ThirtyHourTime time = new ThirtyHourTime(45297, "12:34:56");
        assertEquals("36:34:56", time.getTimeString());
    }

    @Test
    public void testThirtyHourTime4() throws ParseException {
        final ThirtyHourTime time = new ThirtyHourTime(16200, "00:10:00");
        assertEquals("24:10:00", time.getTimeString());
    }

    @Test
    public void testThirtyHourTime5() throws ParseException {
        final ThirtyHourTime time = new ThirtyHourTime(16200, "04:29:00");
        assertEquals("28:29:00", time.getTimeString());
    }

    @Test
    public void testThirtyHourTime6() throws ParseException {
        final ThirtyHourTime time = new ThirtyHourTime(16200, "04:30:00");
        assertEquals("04:30:00", time.getTimeString());
    }
}