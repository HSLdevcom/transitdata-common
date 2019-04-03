package fi.hsl.common.metro;

import com.typesafe.config.Config;
import fi.hsl.common.config.ConfigParser;
import org.apache.pulsar.shade.com.google.common.collect.ArrayListMultimap;

import java.util.HashMap;
import java.util.List;

public class MetroStops {
    private static final MetroStops ourInstance = new MetroStops();

    private static final HashMap<String, String> shortNameByStopNumber = new HashMap<>();
    private static final ArrayListMultimap<String, String> stopNumbersByShortName = ArrayListMultimap.create();

    public static MetroStops getInstance() {
        return ourInstance;
    }

    static {
        final Config stopsConfig = ConfigParser.createConfig("metro_stops.conf");
        stopsConfig.getObjectList("metroStops")
                .forEach(stopConfigObject -> {
                    final Config stopConfig = stopConfigObject.toConfig();
                    final String shortName = stopConfig.getString("shortName");
                    final List<String> stopNumbers = stopConfig.getStringList("stopNumbers");
                    stopNumbers.forEach(stopNumber -> {
                        shortNameByStopNumber.put(stopNumber, shortName);
                        stopNumbersByShortName.put(shortName, stopNumber);
                    });
                });
    }

    private MetroStops() {}

    public static String getShortName(final String stopNumber) {
        return shortNameByStopNumber.get(stopNumber);
    }

    public static List<String> getStopNumbers(final String shortName) {
        return stopNumbersByShortName.get(shortName);
    }
}
