package fi.hsl.common.metro;

import com.typesafe.config.Config;
import fi.hsl.common.config.ConfigParser;
import org.apache.pulsar.shade.com.google.common.collect.ArrayListMultimap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class MetroStops {
    private static final HashMap<String, String> shortNameByStopNumber = new HashMap<>();
    private static final ArrayListMultimap<String, String> stopNumbersByShortName = ArrayListMultimap.create();
    private static final List<String> shortNames = new ArrayList<>();

    static {
        final Config stopsConfig = ConfigParser.createConfig("metro_stops.conf");
        stopsConfig.getObjectList("metroStops")
                .forEach(stopConfigObject -> {
                    final Config stopConfig = stopConfigObject.toConfig();
                    final String shortName = stopConfig.getString("shortName");
                    final List<String> stopNumbers = stopConfig.getStringList("stopNumbers");
                    shortNames.add(shortName);
                    stopNumbers.forEach(stopNumber -> {
                        shortNameByStopNumber.put(stopNumber, shortName);
                        stopNumbersByShortName.put(shortName, stopNumber);
                    });
                });
    }

    private MetroStops() {}

    public static Optional<String> getShortName(final String stopNumber) {
        return Optional.ofNullable(shortNameByStopNumber.get(stopNumber));
    }

    public static Optional<List<String>> getStopNumbers(final String shortName) {
        List<String> stopNumbers = stopNumbersByShortName.get(shortName);
        return stopNumbers.size() > 0 ? Optional.ofNullable(stopNumbers) : Optional.empty();
    }

    public static Optional<Integer> getJoreDirection(final String startStop, final String endStop) {
        final int startStopIndex = shortNames.indexOf(startStop);
        final int endStopIndex = shortNames.indexOf(endStop);
        if (startStopIndex == -1 || endStopIndex == -1 || startStopIndex == endStopIndex) {
            return Optional.empty();
        }
        return Optional.of(startStopIndex < endStopIndex ? 1 : 2);
    }

    public static Optional<String> getStopNumber(final String shortName, final int joreDirection) {
        Optional<List<String>> stopNumbers = getStopNumbers(shortName);
        if (joreDirection < 1 || joreDirection > 2 || !stopNumbers.isPresent()) {
            return Optional.empty();
        }
        // The first stop number corresponds Jore direction 1 and the second stop number corresponds Jore direction 2
        String stopNumber;
        try {
            stopNumber = stopNumbers.get().get(joreDirection - 1);
        } catch (Exception e) {
            return Optional.empty();
        }
        return Optional.ofNullable(stopNumber);
    }

    public static Optional<String> getStopNumber(final String shortName, final String startStop, final String endStop) {
        final Optional<Integer> joreDirection = getJoreDirection(startStop, endStop);
        return joreDirection.isPresent() ? getStopNumber(shortName, joreDirection.get()) : Optional.empty();
    }
}
