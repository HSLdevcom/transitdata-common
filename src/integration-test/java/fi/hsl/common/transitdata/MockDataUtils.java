package fi.hsl.common.transitdata;

import fi.hsl.common.transitdata.proto.PubtransTableProtos;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MockDataUtils {
    public static final String JORE_ROUTE_NAME_REGEX = "[0-9]{4}[A-Z]{1}[A-Z0-9]{0,1}";

    //16 chars of numbers
    public static final long ID_MIN = 1000000000000000L;
    public static final long ID_MAX = 9999999999999999L;

    private MockDataUtils() {}

    public static String generateValidRouteName() {
        long prefix = generateLongWithMinAndMax(0, 9999);
        char c = generateRandomUppercaseAsciiChar();

        String route = String.format("%04d%c", prefix, c);
        double random = Math.random();
        if (random > 0.66666) {
            route += Long.toString(generateLongWithMinAndMax(0, 9));
        }
        else if (random > 0.3333) {
            route += generateRandomUppercaseAsciiChar();
        }
        return route;
    }

    public static long generateValidJoreId() {
        return generateLongWithMinAndMax(ID_MIN, ID_MAX);
    }

    public static int generateValidStopSequenceId() {
        return (int)generateLongWithMinAndMax(0, 50);
    }

    public static List<Integer> generateStopSequenceList(int length) {
        return IntStream.range(0, length).boxed().collect(Collectors.toList());
    }

    private static long generateLongWithMinAndMax(long min, long max) {
        long add = (long)(Math.random() * (max - min));
        return min + add;
    }

    private static char generateRandomUppercaseAsciiChar() {
        return (char) generateLongWithMinAndMax(65, 90); //From uppercase A to Z
    }

    public static long generateValidDateTimeMs() {
        final long START_OF_2018 = 1514764800000L;
        final long END_OF_2018 = 1546300799000L;

        return generateLongWithMinAndMax(START_OF_2018, END_OF_2018);
    }

    public static PubtransTableProtos.Common.Builder generateValidCommon() {
        return generateValidCommon(generateValidJoreId());
    }

    public static PubtransTableProtos.Common.Builder generateValidCommon(long dvjId) {
        return generateValidCommon(dvjId, generateValidStopSequenceId());
    }

    public static PubtransTableProtos.Common.Builder generateValidCommon(long dvjId, int stopSequence) {
        return generateValidCommon(dvjId, stopSequence, generateValidDateTimeMs());
    }

    public static PubtransTableProtos.Common.Builder generateValidCommon(long dvjId, int stopSequence, long targetDateTimeMs) {
        return generateValidCommon(dvjId, stopSequence, targetDateTimeMs, System.currentTimeMillis());
    }

    public static PubtransTableProtos.Common.Builder generateValidCommon(long dvjId, int stopSequence, long targetDateTimeMs, long lastModifiedMs) {
        return commonBoilerplate()
                .setIsOnDatedVehicleJourneyId(dvjId)
                .setJourneyPatternSequenceNumber(stopSequence)
                .setTargetUtcDateTimeMs(targetDateTimeMs)
                .setLastModifiedUtcDateTimeMs(lastModifiedMs);
    }

    public static PubtransTableProtos.Common.Builder commonBoilerplate() {
        PubtransTableProtos.Common.Builder commonBuilder = PubtransTableProtos.Common.newBuilder();
        commonBuilder.setIsTargetedAtJourneyPatternPointGid(generateValidJoreId());

        commonBuilder.setSchemaVersion(commonBuilder.getSchemaVersion());

        commonBuilder.setId(generateValidJoreId());
        commonBuilder.setIsTimetabledAtJourneyPatternPointGid(generateValidJoreId());
        commonBuilder.setVisitCountNumber(2);
        commonBuilder.setIsValidYesNo(true);

        setIsViaPoint(commonBuilder, false);
        setIsSkipped(commonBuilder, false);

        return commonBuilder;
    }

    public static void setIsSkipped(PubtransTableProtos.Common.Builder builder, boolean skipped) {
        if (skipped)
            builder.setState(3L);
        else
            builder.setState(2L);
    }

    public static void setIsViaPoint(PubtransTableProtos.Common.Builder builder, boolean isViaPoint) {
        if (isViaPoint)
            builder.setType(0);
        else
            builder.setType(1);
    }
}
