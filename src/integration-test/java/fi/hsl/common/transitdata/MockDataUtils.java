package fi.hsl.common.transitdata;

import com.google.transit.realtime.GtfsRealtime;
import fi.hsl.common.transitdata.proto.InternalMessages;
import fi.hsl.common.transitdata.proto.PubtransTableProtos;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MockDataUtils {
    public static final String JORE_ROUTE_NAME_REGEX = "[0-9]{4}[A-Z]{1}[A-Z0-9]{0,1}";

    //16 chars of numbers
    public static final long ID_MIN = 1000000000000000L;
    public static final long ID_MAX = 9999999999999999L;

    public static final SimpleDateFormat START_TIME_FORMAT = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

    public static final String MOCK_START_DATE = "20180101";
    public static final String MOCK_START_TIME = "11:22:00";

    public static final int MOCK_DIRECTION_ID = PubtransFactory.JORE_DIRECTION_ID_INBOUND; // Let's use inbound so it differs from GTFS-RT constants.
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

    public static PubtransTableProtos.ROIArrival mockROIArrival(long dvjId, String routeName, long targetDateTimeMs) throws Exception {
        return mockROIArrival(dvjId, routeName, MOCK_DIRECTION_ID, generateValidStopSequenceId(), targetDateTimeMs);
    }

    public static PubtransTableProtos.ROIArrival mockROIArrival(long dvjId, String routeName, int joreDirection, int stopSequence, long targetDateTimeMs) throws Exception {
        PubtransTableProtos.Common common = generateValidCommon(dvjId, stopSequence, targetDateTimeMs).build();
        PubtransTableProtos.DOITripInfo info = mockDOITripInfo(dvjId, routeName, joreDirection);
        return mockROIArrival(common, info);
    }

    public static PubtransTableProtos.ROIArrival mockROIArrival(PubtransTableProtos.Common common, PubtransTableProtos.DOITripInfo info) throws Exception {
        PubtransTableProtos.ROIArrival.Builder builder = PubtransTableProtos.ROIArrival.newBuilder();
        builder.setSchemaVersion(builder.getSchemaVersion());
        builder.setCommon(common);
        builder.setTripInfo(info);
        return builder.build();
    }

    public static PubtransTableProtos.ROIDeparture mockROIDeparture(long dvjId, String routeName, long targetDateTimeMs) throws Exception {
        return mockROIDeparture(dvjId, routeName, MOCK_DIRECTION_ID, generateValidStopSequenceId(), targetDateTimeMs);
    }

    public static PubtransTableProtos.ROIDeparture mockROIDeparture(long dvjId, String routeName, int joreDirection, int stopSequence, long targetDateTimeMs) throws Exception {
        PubtransTableProtos.Common common = generateValidCommon(dvjId, stopSequence, targetDateTimeMs).build();
        PubtransTableProtos.DOITripInfo info = mockDOITripInfo(dvjId, routeName, joreDirection);
        return mockROIDeparture(common, info);
    }

    public static PubtransTableProtos.ROIDeparture mockROIDeparture(PubtransTableProtos.Common common, PubtransTableProtos.DOITripInfo info) throws Exception {
        PubtransTableProtos.ROIDeparture.Builder builder = PubtransTableProtos.ROIDeparture.newBuilder();
        builder.setSchemaVersion(builder.getSchemaVersion());
        builder.setCommon(common);
        builder.setTripInfo(info);

        builder.setHasDestinationDisplayId(0);
        builder.setHasDestinationStopAreaGid(0);
        builder.setHasServiceRequirementId(0);
        return builder.build();
    }

    public static InternalMessages.StopEstimate mockStopEstimate(String routeName) throws Exception {
        return mockStopEstimate(generateValidJoreId(), routeName);
    }

    public static InternalMessages.StopEstimate mockStopEstimate(InternalMessages.StopEstimate.Type eventType, long startTimeEpoch) throws Exception {
        return mockStopEstimate(generateValidJoreId(), eventType, 0, 0, startTimeEpoch);
    }

    public static InternalMessages.StopEstimate mockStopEstimate(long dvjId, String routeName) throws Exception {
        PubtransTableProtos.DOITripInfo mockTripInfo = mockDOITripInfo(dvjId, routeName);
        PubtransTableProtos.Common mockCommon = MockDataUtils.generateValidCommon(dvjId).build();
        return PubtransFactory.createStopEstimate(mockCommon, mockTripInfo, InternalMessages.StopEstimate.Type.ARRIVAL);
    }

    public static InternalMessages.StopEstimate mockStopEstimate(long dvjId,
                                                                 InternalMessages.StopEstimate.Type eventType,
                                                                 long stopId,
                                                                 int stopSequence,
                                                                 long startTimeEpochMs) throws Exception {
        PubtransTableProtos.Common common = MockDataUtils.generateValidCommon(dvjId, stopSequence, startTimeEpochMs).build();
        PubtransTableProtos.DOITripInfo mockTripInfo = mockDOITripInfo(dvjId, generateValidRouteName(),
                stopId, startTimeEpochMs);
        return PubtransFactory.createStopEstimate(common, mockTripInfo, eventType);
    }

    public static PubtransTableProtos.DOITripInfo mockDOITripInfo(long dvjId, String routeName) {
        return mockDOITripInfo(dvjId, routeName, generateValidJoreId());
    }

    public static PubtransTableProtos.DOITripInfo mockDOITripInfo(long dvjId, String routeName, int joreDirection) {
        long stopId = generateValidJoreId();
        return mockDOITripInfo(dvjId,
                routeName,
                stopId,
                joreDirection,
                MOCK_START_DATE, MOCK_START_TIME);
    }

    public static PubtransTableProtos.DOITripInfo mockDOITripInfo(long dvjId, String routeName, long stopId) {
        return mockDOITripInfo(dvjId,
                routeName,
                stopId,
                MOCK_DIRECTION_ID,
                MOCK_START_DATE, MOCK_START_TIME);
    }

    public static PubtransTableProtos.DOITripInfo mockDOITripInfo(long dvjId, String routeName, long stopId, long startTimeEpochMs) {
        String startTimeAsString = START_TIME_FORMAT.format(startTimeEpochMs);
        String[] dateAndTime = startTimeAsString.split(" ");
        String operatingDay = dateAndTime[0];
        String startTime = dateAndTime[1];
        return mockDOITripInfo(dvjId,
                routeName,
                stopId,
                MOCK_DIRECTION_ID,
                operatingDay,
                startTime);
    }


    public static PubtransTableProtos.DOITripInfo mockDOITripInfo(long dvjId, String routeName, long stopId,
                                                           int joreDirection, String startDate,
                                                           String startTime) {
        PubtransTableProtos.DOITripInfo.Builder builder = PubtransTableProtos.DOITripInfo.newBuilder();
        builder.setStopId(Long.toString(stopId));
        builder.setDirectionId(joreDirection);
        builder.setRouteId(routeName);
        builder.setStartTime(startTime);
        builder.setOperatingDay(startDate);
        builder.setDvjId(dvjId);
        return builder.build();
    }

    public static InternalMessages.TripCancellation mockTripCancellation(String routeId) {
        return mockTripCancellation(routeId, MOCK_DIRECTION_ID, MOCK_START_DATE, MOCK_START_TIME, InternalMessages.TripCancellation.Status.CANCELED);
    }

    public static InternalMessages.TripCancellation mockTripCancellation(String routeId, int joreDirectionId,
                                                                         LocalDateTime startTime) {
        return mockTripCancellation(routeId, joreDirectionId, startTime, InternalMessages.TripCancellation.Status.CANCELED);
    }

    public static InternalMessages.TripCancellation mockTripCancellation(String routeId, int joreDirectionId,
                                                                         LocalDateTime startTime,
                                                                         InternalMessages.TripCancellation.Status status) {

        String date = DateTimeFormatter.ofPattern("yyyyMMdd").format(startTime);
        String time = DateTimeFormatter.ofPattern("HH:mm:ss").format(startTime);
        return mockTripCancellation(routeId, joreDirectionId, date, time, status);
    }

    public static InternalMessages.TripCancellation mockTripCancellation(String routeId, int joreDirectionId,
                                                                         String startDate, String startTime,
                                                                         InternalMessages.TripCancellation.Status status) {

        InternalMessages.TripCancellation.Builder tripCancellationBuilder = InternalMessages.TripCancellation.newBuilder();

        tripCancellationBuilder.setRouteId(routeId)
                .setDirectionId(joreDirectionId)
                .setStartDate(startDate)
                .setStartTime(startTime)
                .setSchemaVersion(tripCancellationBuilder.getSchemaVersion())
                .setStatus(status);

        return tripCancellationBuilder.build();
    }

}
