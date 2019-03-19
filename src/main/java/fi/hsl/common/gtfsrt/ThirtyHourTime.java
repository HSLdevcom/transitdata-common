package fi.hsl.common.gtfsrt;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ThirtyHourTime {

    private static final int MINUTE_IN_SECONDS = 60;
    private static final int HOUR_IN_SECONDS = 60 * MINUTE_IN_SECONDS;
    private static final int DAY_IN_SECONDS = 24 * HOUR_IN_SECONDS;
    private static final Pattern pattern = Pattern.compile("^(\\d{2,}):([0-5][0-9]):([0-5][0-9])$");

    private int seconds;

    public ThirtyHourTime(final int serviceDayStartTimeSeconds, final int seconds) {
        this.seconds = seconds < serviceDayStartTimeSeconds ? DAY_IN_SECONDS + seconds : seconds;
    }

    public ThirtyHourTime(final int serviceDayStartTimeSeconds, String timeString) throws ParseException {
        this(serviceDayStartTimeSeconds, timeStringToSeconds(timeString));
    }

    public String getTimeString() {
        return secondsToTimeString(seconds);
    }

    public int getSeconds() {
        return seconds;
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
