package fi.hsl.common.gtfsrt;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.LocalDateTime;

public class JoreDateTime {

    private static final int MINUTE_IN_SECONDS = 60;
    private static final int HOUR_IN_SECONDS = 60 * MINUTE_IN_SECONDS;
    private static final int DAY_IN_SECONDS = 24 * HOUR_IN_SECONDS;
    private static final Pattern pattern = Pattern.compile("^(\\d{2,}):([0-5][0-9]):([0-5][0-9])$");

    private int joreSeconds;
    private LocalDateTime dateTime;

    public JoreDateTime(final String serviceDayStartTimeString, final String dateString, final String timeString) {
        int serviceDayStartTimeSeconds = LocalTime.parse(serviceDayStartTimeString).toSecondOfDay();
        LocalTime time = LocalTime.parse(timeString);
        joreSeconds = time.toSecondOfDay();
        LocalDate date = LocalDate.parse(dateString);
        if (joreSeconds < serviceDayStartTimeSeconds) {
            date = date.plusDays(1);
            joreSeconds += DAY_IN_SECONDS;
        }
        dateTime = LocalDateTime.of(date, time);
    }

    public String getJoreTimeString() {
        return secondsToTimeString(joreSeconds);
    }

    public int getJoreSeconds() {
        return joreSeconds;
    }

    public String getJoreDateString() {
        LocalDate date = dateTime.toLocalDate();
        if (joreSeconds > DAY_IN_SECONDS) {
            date = date.minusDays(1);
        }
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public long getEpochSeconds() {
        return dateTime.toEpochSecond(ZoneOffset.UTC);
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public static int timeStringToSeconds(String timeString) throws ParseException {
        Matcher matcher = pattern.matcher(timeString);
        if (!matcher.matches()) {
            throw new ParseException("Failed to parse provided time string", 0);
        }
        return Integer.parseInt(matcher.group(1)) * HOUR_IN_SECONDS
                + Integer.parseInt(matcher.group(2)) * MINUTE_IN_SECONDS
                + Integer.parseInt(matcher.group(3));
    }

    public static String secondsToTimeString(int seconds) {
        int h = seconds / HOUR_IN_SECONDS;
        int m  = (seconds % HOUR_IN_SECONDS) / MINUTE_IN_SECONDS;
        int s = seconds % MINUTE_IN_SECONDS;
        return String.format("%02d:%02d:%02d", h, m, s);
    }
}
