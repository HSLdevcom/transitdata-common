package fi.hsl.common.transitdata;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RouteIdUtils {
    // Currently route IDs for trains are 3001 and 3002.
    private static final String TRAIN_ROUTE_NAME_REGEX = "^300([12])";
    private static final Pattern TRAIN_ROUTE_PATTERN = Pattern.compile(TRAIN_ROUTE_NAME_REGEX);

    // Currently route IDs for trains are 31M1, 31M2, 31M1B, 31M2B and 31M2M
    private static final String METRO_ROUTE_NAME_REGEX = "^31M([12])([BM])?$";
    private static final Pattern METRO_ROUTE_PATTERN = Pattern.compile(METRO_ROUTE_NAME_REGEX);

    // Currently route IDs for ferries are 1019 and 1019.
    private static final String FERRY_ROUTE_NAME_REGEX = "^1019E?";
    private static final Pattern FERRY_ROUTE_PATTERN = Pattern.compile(FERRY_ROUTE_NAME_REGEX);

    private RouteIdUtils() {}

    /**
     * Checks if route ID is for train route
     * @param routeId
     * @return
     */
    public static boolean isTrainRoute(String routeId) {
        Matcher matcher = TRAIN_ROUTE_PATTERN.matcher(routeId);
        return matcher.find();
    }

    /**
     * Checks if route ID is for metro route
     * @param routeId
     * @return
     */
    public static boolean isMetroRoute(String routeId) {
        Matcher matcher = METRO_ROUTE_PATTERN.matcher(routeId);
        return matcher.find();
    }

    /**
     * Checks if route ID is for ferry route
     * @param routeId
     * @return
     */
    public static boolean isFerryRoute(String routeId) {
        Matcher matcher = FERRY_ROUTE_PATTERN.matcher(routeId);
        return matcher.find();
    }

    /**
     * Normalizes route ID variants, e.g. '1008 3' -> '1008' and '3001Z3' -> '3001Z'
     * @param routeId Route ID
     * @return Normalized route id
     */
    public static String normalizeRouteId(String routeId) {
        if (routeId.length() < 4) {
            throw new IllegalArgumentException("Route ID must be at least 4 characters");
        } else if (routeId.length() <= 5) {
            return routeId;
        } else {
            if (Character.isAlphabetic(routeId.charAt(4)) && !Character.isAlphabetic(routeId.charAt(5))) {
                return routeId.substring(0, 5);
            } else if (Character.isSpaceChar(routeId.charAt(4))) {
                return routeId.substring(0, 4);
            } else {
                return routeId;
            }
        }
    }
}
