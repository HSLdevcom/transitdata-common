package fi.hsl.common.transitdata;

import fi.hsl.common.transitdata.proto.InternalMessages;
import fi.hsl.common.transitdata.proto.PubtransTableProtos;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
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
    public static final InternalMessages.TripCancellation.DeviationCasesType MOCK_DEVIATION_CASES_TYPE = InternalMessages.TripCancellation.DeviationCasesType.CANCEL_DEPARTURE;
    public static final InternalMessages.TripCancellation.AffectedDeparturesType MOCK_AFFECTED_DEPARTURES_TYPE = InternalMessages.TripCancellation.AffectedDeparturesType.CANCEL_ENTIRE_DEPARTURE;
    public static final String MOCK_TITLE = "title";
    public static final String MOCK_DESCRIPTION = "description";
    public static final InternalMessages.Category MOCK_CATEGORY = InternalMessages.Category.VEHICLE_BREAKDOWN;
    public static final InternalMessages.TripCancellation.SubCategory MOCK_SUB_CATEGORY = InternalMessages.TripCancellation.SubCategory.BREAK_MALFUNCTION;

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

    public static PubtransTableProtos.Common.Builder mockCommon() {
        return mockCommon(generateValidJoreId());
    }

    public static PubtransTableProtos.Common.Builder mockCommon(long dvjId) {
        return mockCommon(dvjId, generateValidStopSequenceId());
    }

    public static PubtransTableProtos.Common.Builder mockCommon(long dvjId, int stopSequence) {
        return mockCommon(dvjId, stopSequence, generateValidDateTimeMs());
    }

    public static PubtransTableProtos.Common.Builder mockCommon(long dvjId, int stopSequence, long targetDateTimeMs) {
        return mockCommon(dvjId, stopSequence, targetDateTimeMs, System.currentTimeMillis());
    }

    public static PubtransTableProtos.Common.Builder mockCommon(long dvjId, int stopSequence, long targetDateTimeMs, long lastModifiedMs) {
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
        PubtransTableProtos.Common common = mockCommon(dvjId, stopSequence, targetDateTimeMs).build();
        PubtransTableProtos.DOITripInfo info = mockDOITripInfo(dvjId, routeName, joreDirection);
        return mockROIArrival(common, info);
    }

    public static PubtransTableProtos.ROIArrival mockROIArrival(PubtransTableProtos.Common common, PubtransTableProtos.DOITripInfo info) throws Exception {
        return mockROIArrivalBuilder(common, info).build();
    }

    public static PubtransTableProtos.ROIArrival.Builder mockROIArrivalBuilder(PubtransTableProtos.Common common, PubtransTableProtos.DOITripInfo info) {
        PubtransTableProtos.ROIArrival.Builder builder = PubtransTableProtos.ROIArrival.newBuilder();
        builder.setSchemaVersion(builder.getSchemaVersion());
        builder.setCommon(common);
        builder.setTripInfo(info);
        return builder;
    }

    public static PubtransTableProtos.ROIDeparture mockROIDeparture(long dvjId, String routeName, long targetDateTimeMs) throws Exception {
        return mockROIDeparture(dvjId, routeName, MOCK_DIRECTION_ID, generateValidStopSequenceId(), targetDateTimeMs);
    }

    public static PubtransTableProtos.ROIDeparture mockROIDeparture(long dvjId, String routeName, int joreDirection, int stopSequence, long targetDateTimeMs) throws Exception {
        PubtransTableProtos.Common common = mockCommon(dvjId, stopSequence, targetDateTimeMs).build();
        PubtransTableProtos.DOITripInfo info = mockDOITripInfo(dvjId, routeName, joreDirection);
        return mockROIDeparture(common, info);
    }

    public static PubtransTableProtos.ROIDeparture mockROIDeparture(PubtransTableProtos.Common common, PubtransTableProtos.DOITripInfo info) throws Exception {
        return mockROIDepartureBuilder(common, info).build();
    }

    public static PubtransTableProtos.ROIDeparture.Builder mockROIDepartureBuilder(PubtransTableProtos.Common common, PubtransTableProtos.DOITripInfo info) {
        PubtransTableProtos.ROIDeparture.Builder builder = PubtransTableProtos.ROIDeparture.newBuilder();
        builder.setSchemaVersion(builder.getSchemaVersion());
        builder.setCommon(common);
        builder.setTripInfo(info);

        builder.setHasDestinationDisplayId(0);
        builder.setHasDestinationStopAreaGid(0);
        builder.setHasServiceRequirementId(0);
        return builder;
    }

    public static InternalMessages.StopEstimate mockStopEstimate(String routeName) throws Exception {
        return mockStopEstimate(generateValidJoreId(), routeName);
    }

    public static InternalMessages.StopEstimate mockStopEstimate(InternalMessages.StopEstimate.Type eventType, long startTimeEpoch) throws Exception {
        return mockStopEstimate(generateValidJoreId(), eventType, 0, 0, startTimeEpoch);
    }

    public static InternalMessages.StopEstimate mockStopEstimate(long dvjId, String routeName) throws Exception {
        PubtransTableProtos.DOITripInfo mockTripInfo = mockDOITripInfo(dvjId, routeName);
        PubtransTableProtos.Common mockCommon = MockDataUtils.mockCommon(dvjId).build();
        return PubtransFactory.createStopEstimate(mockCommon, mockTripInfo, InternalMessages.StopEstimate.Type.ARRIVAL);
    }

    public static InternalMessages.StopEstimate mockStopEstimate(long dvjId,
                                                                 InternalMessages.StopEstimate.Type eventType,
                                                                 long stopId,
                                                                 int stopSequence,
                                                                 long startTimeEpochMs) throws Exception {
        PubtransTableProtos.Common common = MockDataUtils.mockCommon(dvjId, stopSequence, startTimeEpochMs).build();
        PubtransTableProtos.DOITripInfo mockTripInfo = mockDOITripInfo(dvjId, generateValidRouteName(),
                stopId, startTimeEpochMs);
        return PubtransFactory.createStopEstimate(common, mockTripInfo, eventType);
    }

    public static PubtransTableProtos.DOITripInfo mockDOITripInfo(long dvjId, String routeName) throws Exception {
        return mockDOITripInfo(dvjId, routeName, generateValidJoreId());
    }

    public static PubtransTableProtos.DOITripInfo mockDOITripInfo(long dvjId, String routeName, int joreDirection) throws Exception {
        long stopId = generateValidJoreId();
        return mockDOITripInfo(dvjId,
                routeName,
                stopId,
                joreDirection,
                MOCK_START_DATE, MOCK_START_TIME);
    }

    public static PubtransTableProtos.DOITripInfo mockDOITripInfo(long dvjId, String routeName, long stopId) throws Exception {
        return mockDOITripInfo(dvjId,
                routeName,
                stopId,
                MOCK_DIRECTION_ID,
                MOCK_START_DATE, MOCK_START_TIME);
    }

    public static PubtransTableProtos.DOITripInfo mockDOITripInfo(long dvjId, String routeName,
                                                                  long stopId, long startTimeEpochMs) throws Exception {
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
                                                           int joreDirection, String startDate, String startTime) throws Exception {
        return mockDOITripInfoBuilder(dvjId, routeName, stopId, joreDirection, startDate, startTime).build();
    }

    public static PubtransTableProtos.DOITripInfo.Builder mockDOITripInfoBuilder(long dvjId, String routeName, long stopId,
                                                                  int joreDirection, String startDate,
                                                                  String startTime) {
        PubtransTableProtos.DOITripInfo.Builder builder = PubtransTableProtos.DOITripInfo.newBuilder();
        builder.setStopId(Long.toString(stopId));
        builder.setDirectionId(joreDirection);
        builder.setRouteId(routeName);
        builder.setStartTime(startTime);
        builder.setOperatingDay(startDate);
        builder.setDvjId(dvjId);
        return builder;
    }

    public static InternalMessages.TripCancellation mockTripCancellation(String routeId) throws Exception {
        return mockTripCancellation(
                generateValidJoreId(), routeId,
                MOCK_DIRECTION_ID, MOCK_START_DATE, MOCK_START_TIME,
                InternalMessages.TripCancellation.Status.CANCELED,
                Optional.of(MOCK_DEVIATION_CASES_TYPE), Optional.of(MOCK_AFFECTED_DEPARTURES_TYPE),
                Optional.of(MOCK_TITLE), Optional.of(MOCK_DESCRIPTION),
                Optional.of(MOCK_CATEGORY), Optional.of(MOCK_SUB_CATEGORY));
    }

    public static InternalMessages.TripCancellation mockTripCancellation(long dvjId, String routeId, int joreDirectionId,
                                                                         LocalDateTime startTime) throws Exception {
        return mockTripCancellation(dvjId, routeId, joreDirectionId, startTime, InternalMessages.TripCancellation.Status.CANCELED);
    }

    public static InternalMessages.TripCancellation mockTripCancellation(long dvjId, String routeId, int joreDirectionId,
                                                                         LocalDateTime startTime,
                                                                         InternalMessages.TripCancellation.Status status) throws Exception {

        String date = DateTimeFormatter.ofPattern("yyyyMMdd").format(startTime);
        String time = DateTimeFormatter.ofPattern("HH:mm:ss").format(startTime);
        return mockTripCancellation(dvjId, routeId, joreDirectionId, date, time, status,
                Optional.of(MOCK_DEVIATION_CASES_TYPE), Optional.of(MOCK_AFFECTED_DEPARTURES_TYPE),
                Optional.of(MOCK_TITLE), Optional.of(MOCK_DESCRIPTION),
                Optional.of(MOCK_CATEGORY), Optional.of(MOCK_SUB_CATEGORY));
    }

    public static InternalMessages.TripCancellation mockTripCancellation(long dvjId, String routeId, int joreDirectionId,
                                                                         String startDate, String startTime,
                                                                         InternalMessages.TripCancellation.Status status) throws Exception {
        return mockTripCancellation(dvjId, routeId, joreDirectionId, startDate, startTime, status,
                Optional.of(MOCK_DEVIATION_CASES_TYPE), Optional.of(MOCK_AFFECTED_DEPARTURES_TYPE),
                Optional.of(MOCK_TITLE), Optional.of(MOCK_DESCRIPTION),
                Optional.of(MOCK_CATEGORY), Optional.of(MOCK_SUB_CATEGORY));
    }

    public static InternalMessages.TripCancellation mockTripCancellation(long dvjId, String routeId, int joreDirectionId,
                                                                         String startDate, String startTime,
                                                                         InternalMessages.TripCancellation.Status status,
                                                                         final Optional<InternalMessages.TripCancellation.DeviationCasesType> maybeDeviationCasesType,
                                                                         final Optional<InternalMessages.TripCancellation.AffectedDeparturesType> maybeAffectedDeparturesType,
                                                                         final Optional<String> maybeTitle,
                                                                         final Optional<String> maybeDescription,
                                                                         final Optional<InternalMessages.Category> maybeCategory,
                                                                         final Optional<InternalMessages.TripCancellation.SubCategory> maybeSubCategory) throws Exception {
        return mockTripCancellationBuilder(dvjId, routeId, joreDirectionId, startDate, startTime, status,
                maybeDeviationCasesType, maybeAffectedDeparturesType,
                maybeTitle, maybeDescription,
                maybeCategory, maybeSubCategory).build();
    }

    public static InternalMessages.TripCancellation.Builder mockTripCancellationBuilder(long dvjId, String routeId, int joreDirectionId,
                                                                                        String startDate, String startTime,
                                                                                        InternalMessages.TripCancellation.Status status,
                                                                                        final Optional<InternalMessages.TripCancellation.DeviationCasesType> maybeDeviationCasesType,
                                                                                        final Optional<InternalMessages.TripCancellation.AffectedDeparturesType> maybeAffectedDeparturesType,
                                                                                        final Optional<String> maybeTitle,
                                                                                        final Optional<String> maybeDescription,
                                                                                        final Optional<InternalMessages.Category> maybeCategory,
                                                                                        final Optional<InternalMessages.TripCancellation.SubCategory> maybeSubCategory) {

        InternalMessages.TripCancellation.Builder builder = InternalMessages.TripCancellation.newBuilder();

        builder.setRouteId(routeId)
                .setTripId(Long.toString(dvjId))
                .setDirectionId(joreDirectionId)
                .setStartDate(startDate)
                .setStartTime(startTime)
                .setSchemaVersion(builder.getSchemaVersion())
                .setStatus(status);
        maybeDeviationCasesType.ifPresent(builder::setDeviationCasesType);
        maybeAffectedDeparturesType.ifPresent(builder::setAffectedDeparturesType);
        maybeTitle.ifPresent(builder::setTitle);
        maybeDescription.ifPresent(builder::setDescription);
        maybeCategory.ifPresent(builder::setCategory);
        maybeSubCategory.ifPresent(builder::setSubCategory);
        return builder;
    }

    public static InternalMessages.Bulletin.AffectedEntity mockAffectedEntity(final String entityId) {
        return InternalMessages.Bulletin.AffectedEntity.newBuilder().setEntityId(entityId).build();
    }

    public static InternalMessages.Bulletin.Translation mockTranslation(final String text, final String language) {
        return InternalMessages.Bulletin.Translation.newBuilder().setText(text).setLanguage(language).build();
    }

    public static InternalMessages.Bulletin mockBulletin(final String bulletinId, final long lastModifiedMs,
                                                                        final long validFromMs, final long validToMs,
                                                                        final boolean affectsAllRoutes, final boolean affectsAllStops,
                                                                        final List<InternalMessages.Bulletin.AffectedEntity> affectedRoutes,
                                                                        final List<InternalMessages.Bulletin.AffectedEntity> affectedStops,
                                                                        final Optional<InternalMessages.Category> maybeCategory,
                                                                        final Optional<InternalMessages.Bulletin.Impact> maybeImpact,
                                                                        final Optional<InternalMessages.Bulletin.Priority> maybePriority,
                                                                        final List<InternalMessages.Bulletin.Translation> titles,
                                                                        final List<InternalMessages.Bulletin.Translation> descriptions,
                                                                        final List<InternalMessages.Bulletin.Translation> urls) {
        return mockBulletinBuilder(bulletinId, lastModifiedMs, validFromMs, validToMs, affectsAllRoutes, affectsAllStops,
                affectedRoutes, affectedStops, maybeCategory, maybeImpact, maybePriority, titles, descriptions, urls).build();
    }

    public static InternalMessages.Bulletin.Builder mockBulletinBuilder(final String bulletinId, final long lastModifiedMs,
                                                                        final long validFromMs, final long validToMs,
                                                                        final boolean affectsAllRoutes, final boolean affectsAllStops,
                                                                        final List<InternalMessages.Bulletin.AffectedEntity> affectedRoutes,
                                                                        final List<InternalMessages.Bulletin.AffectedEntity> affectedStops,
                                                                        final Optional<InternalMessages.Category> maybeCategory,
                                                                        final Optional<InternalMessages.Bulletin.Impact> maybeImpact,
                                                                        final Optional<InternalMessages.Bulletin.Priority> maybePriority,
                                                                        final List<InternalMessages.Bulletin.Translation> titles,
                                                                        final List<InternalMessages.Bulletin.Translation> descriptions,
                                                                        final List<InternalMessages.Bulletin.Translation> urls) {
        InternalMessages.Bulletin.Builder builder = InternalMessages.Bulletin.newBuilder();
        builder.setBulletinId(bulletinId)
                .setLastModifiedUtcMs(lastModifiedMs)
                .setValidFromUtcMs(validFromMs)
                .setValidToUtcMs(validToMs)
                .setAffectsAllRoutes(affectsAllRoutes)
                .setAffectsAllStops(affectsAllStops)
                .addAllAffectedRoutes(affectedRoutes)
                .addAllAffectedStops(affectedStops)
                .addAllTitles(titles)
                .addAllDescriptions(descriptions)
                .addAllUrls(urls);
        maybeCategory.ifPresent(builder::setCategory);
        maybeImpact.ifPresent(builder::setImpact);
        maybePriority.ifPresent(builder::setPriority);
        return builder;
    }

    public static InternalMessages.ServiceAlert mockAlert(final List<InternalMessages.Bulletin> bulletins) {
        InternalMessages.ServiceAlert.Builder builder = InternalMessages.ServiceAlert.newBuilder();
        return builder.addAllBulletins(bulletins).setSchemaVersion(builder.getSchemaVersion()).build();
    }
}
