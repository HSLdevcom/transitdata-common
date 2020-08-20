package fi.hsl.common.transitdata;

import org.jetbrains.annotations.NotNull;

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

    public JoreDateTime(@NotNull final String serviceDayStartTimeString, @NotNull final String date24h, @NotNull final String time24h) {
        int serviceDayStartTimeSeconds = LocalTime.parse(serviceDayStartTimeString).toSecondOfDay();
        LocalDate date = LocalDate.parse(date24h, DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalTime time = LocalTime.parse(time24h);
        joreSeconds = time.toSecondOfDay();
        if (joreSeconds < serviceDayStartTimeSeconds) {
            joreSeconds += DAY_IN_SECONDS;
        }
        dateTime = LocalDateTime.of(date, time);
    }

    @NotNull
    public String getJoreTimeString() {
        return secondsToTimeString(joreSeconds);
    }

    public int getJoreSeconds() {
        return joreSeconds;
    }

    @NotNull
    public String getJoreDateString() {
        LocalDate date = dateTime.toLocalDate();
        if (joreSeconds > DAY_IN_SECONDS) {
            date = date.minusDays(1);
        }
        return date.format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    public long getEpochSeconds() {
        return dateTime.toEpochSecond(ZoneOffset.UTC);
    }

    @NotNull
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public boolean isBefore(@NotNull final JoreDateTime other) {
        return dateTime.isBefore(other.getDateTime());
    }

    public boolean isAfter(@NotNull final JoreDateTime other) {
        return dateTime.isAfter(other.getDateTime());
    }

    public boolean isEqual(@NotNull final JoreDateTime other) {
        return dateTime.isEqual(other.getDateTime());
    }

    public static int timeStringToSeconds(@NotNull String timeString) throws ParseException {
        Matcher matcher = pattern.matcher(timeString);
        if (!matcher.matches()) {
            throw new ParseException("Failed to parse provided time string", 0);
        }
        return Integer.parseInt(matcher.group(1)) * HOUR_IN_SECONDS
                + Integer.parseInt(matcher.group(2)) * MINUTE_IN_SECONDS
                + Integer.parseInt(matcher.group(3));
    }

    @NotNull
    public static String secondsToTimeString(int seconds) {
        int h = seconds / HOUR_IN_SECONDS;
        int m  = (seconds % HOUR_IN_SECONDS) / MINUTE_IN_SECONDS;
        int s = seconds % MINUTE_IN_SECONDS;
        return String.format("%02d:%02d:%02d", h, m, s);
    }
}
