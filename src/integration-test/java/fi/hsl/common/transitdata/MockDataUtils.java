package fi.hsl.common.transitdata;

import nl.flotsam.xeger.Xeger;

public class MockDataUtils {
    public static final String JORE_ROUTE_NAME_REGEX = "[0-9]{4}[A-Z]{1}[A-Z0-9]{0,1}";

    private MockDataUtils() {}

    public static String generateValidRouteName() {
        Xeger generator = new Xeger(JORE_ROUTE_NAME_REGEX);
        return generator.generate();
    }
}
