package fi.hsl.common.transitdata;

import nl.flotsam.xeger.Xeger;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MockDataUtils {
    public static final String JORE_ROUTE_NAME_REGEX = "[0-9]{4}[A-Z]{1}[A-Z0-9]{0,1}";
    public static final String ID_REGEX = "[0-9]{16}";

    //16 chars of numbers
    public static final long ID_MIN = 1000000000000000L;
    public static final long ID_MAX = 9999999999999999L;

    private MockDataUtils() {}

    public static String generateValidRouteName() {
        Xeger generator = new Xeger(JORE_ROUTE_NAME_REGEX);
        return generator.generate();
    }

    public static long generateValidJoreId() {
        return generateLongWithMinAndMax(ID_MIN, ID_MAX);
        //Xeger generator = new Xeger(DVJ_ID_REGEX);
        //return Long.parseLong(generator.generate());
    }

    public static int generateStopSequenceId() {
        return (int)generateLongWithMinAndMax(0, 50);
    }

    public static List<Integer> generateStopSequenceList(int length) {
        return IntStream.range(0, length).boxed().collect(Collectors.toList());
    }

    private static long generateLongWithMinAndMax(long min, long max) {
        long add = (long)(Math.random() * (max - min));
        return min + add;
    }

}
