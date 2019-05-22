package fi.hsl.common.transitdata;

import org.junit.Test;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.*;

public class JoreDateTimeTest {
    @Test
    public void testTimeStringToSeconds1() throws ParseException {
        assertEquals(0, JoreDateTime.timeStringToSeconds("00:00:00"));
    }

    @Test
    public void testTimeStringToSeconds2() throws ParseException {
        assertEquals(45296, JoreDateTime.timeStringToSeconds("12:34:56"));
    }

    @Test
    public void testTimeStringToSeconds3() throws ParseException {
        assertEquals(131696, JoreDateTime.timeStringToSeconds("36:34:56"));
    }

    @Test(expected = ParseException.class)
    public void testTimeStringToSecondsException1() throws ParseException {
        JoreDateTime.timeStringToSeconds("asd");
    }

    @Test(expected = ParseException.class)
    public void testTimeStringToSecondsException2() throws ParseException {
        JoreDateTime.timeStringToSeconds("-12:34:56");
    }

    @Test(expected = ParseException.class)
    public void testTimeStringToSecondsException3() throws ParseException {
        JoreDateTime.timeStringToSeconds("12.0:34:56");
    }

    @Test(expected = ParseException.class)
    public void testTimeStringToSecondsException4() throws ParseException {
        JoreDateTime.timeStringToSeconds("12:60:00");
    }

    @Test(expected = ParseException.class)
    public void testTimeStringToSecondsException5() throws ParseException {
        JoreDateTime.timeStringToSeconds("1:23:45");
    }

    @Test(expected = ParseException.class)
    public void testTimeStringToSecondsException6() throws ParseException {
        JoreDateTime.timeStringToSeconds("12:3:45");
    }

    @Test(expected = ParseException.class)
    public void testTimeStringToSecondsException7() throws ParseException {
        JoreDateTime.timeStringToSeconds("12:34");
    }

    @Test
    public void testSecondsToTimeString1() {
        assertEquals("00:00:00", JoreDateTime.secondsToTimeString(0));
    }

    @Test
    public void testSecondsToTimeString2() {
        assertEquals("12:34:56", JoreDateTime.secondsToTimeString(45296));
    }

    @Test
    public void testSecondsToTimeString3() {
        assertEquals("36:34:56", JoreDateTime.secondsToTimeString(131696));
    }

    @Test
    public void testJoreDateTime1() {
        final JoreDateTime time = new JoreDateTime("00:00:00", "19700101", "12:34:56");
        assertEquals("12:34:56", time.getJoreTimeString());
    }

    @Test
    public void testJoreDateTime2() {
        final JoreDateTime time = new JoreDateTime("12:34:56", "19700101", "12:34:56");
        assertEquals("12:34:56", time.getJoreTimeString());
    }

    @Test
    public void testJoreDateTime3() {
        final JoreDateTime time = new JoreDateTime("12:34:57", "19700101", "12:34:56");
        assertEquals("36:34:56", time.getJoreTimeString());
    }

    @Test
    public void testJoreDateTime4() {
        final JoreDateTime time = new JoreDateTime("04:30:00", "19700102", "00:10:00");
        assertEquals("24:10:00", time.getJoreTimeString());
        assertEquals("19700101", time.getJoreDateString());
        assertEquals("1970-01-02", time.getDateTime().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        assertEquals(87000, time.getEpochSeconds());
    }

    @Test
    public void testJoreDateTime5() {
        final JoreDateTime time = new JoreDateTime("04:30:00", "19700102", "04:29:00");
        assertEquals("28:29:00", time.getJoreTimeString());
        assertEquals("19700101", time.getJoreDateString());
        assertEquals("1970-01-02", time.getDateTime().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        assertEquals(102540, time.getEpochSeconds());
    }

    @Test
    public void testJoreDateTime6() {
        final JoreDateTime time = new JoreDateTime("04:30:00", "19700102", "04:30:00");
        assertEquals("04:30:00", time.getJoreTimeString());
        assertEquals("19700102", time.getJoreDateString());
        assertEquals("1970-01-02", time.getDateTime().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        assertEquals(102600, time.getEpochSeconds());
    }
}
