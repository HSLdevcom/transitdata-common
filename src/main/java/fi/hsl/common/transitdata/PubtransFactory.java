package fi.hsl.common.transitdata;

import fi.hsl.common.transitdata.proto.InternalMessages;
import fi.hsl.common.transitdata.proto.PubtransTableProtos;
import org.jetbrains.annotations.NotNull;

/**
 * This class bundles some datatype-conversion operations and helpers.
 * These are basically application-specific stuff but bundling these to common-library
 * will massively help unit- and integration testing, and sometimes even remove duplicate code.
 */
public class PubtransFactory {

    public static final long PUBTRANS_SKIPPED_STATE = 3L;
    public static final int JORE_DIRECTION_ID_OUTBOUND = 1;
    public static final int JORE_DIRECTION_ID_INBOUND = 2;

    private PubtransFactory() {
    }

    /**
     * Protobuf build()-functions can throw RuntimeException so let's make them visible and flag the possible Exception on function signature also
     */
    @NotNull
    public static InternalMessages.TripInfo createTripInfo(@NotNull PubtransTableProtos.DOITripInfo doiInfo)
            throws Exception {
        InternalMessages.TripInfo.Builder tripBuilder = InternalMessages.TripInfo.newBuilder();
        tripBuilder.setTripId(Long.toString(doiInfo.getDvjId()));
        tripBuilder.setOperatingDay(doiInfo.getOperatingDay());
        tripBuilder.setRouteId(doiInfo.getRouteId());
        tripBuilder.setDirectionId(doiInfo.getDirectionId());//Jore format
        tripBuilder.setStartTime(doiInfo.getStartTime());
        return tripBuilder.build();
    }

    @NotNull
    public static InternalMessages.StopEstimate createStopEstimate(@NotNull PubtransTableProtos.Common common,
            @NotNull PubtransTableProtos.DOITripInfo doiTripInfo,
            @NotNull InternalMessages.StopEstimate.Type arrivalOrDeparture) throws Exception {
        InternalMessages.StopEstimate.Builder builder = InternalMessages.StopEstimate.newBuilder();
        builder.setSchemaVersion(builder.getSchemaVersion());

        InternalMessages.TripInfo tripInfo = createTripInfo(doiTripInfo);
        builder.setTripInfo(tripInfo);

        builder.setStopId(doiTripInfo.getStopId()); //Use to be Long in old internal model
        builder.setTargetedStopId(doiTripInfo.getTargetedStopId());
        builder.setStopSequence(common.getJourneyPatternSequenceNumber());

        InternalMessages.StopEstimate.Status scheduledStatus = (common.getState() == PUBTRANS_SKIPPED_STATE)
                ? InternalMessages.StopEstimate.Status.SKIPPED
                : InternalMessages.StopEstimate.Status.SCHEDULED;

        builder.setStatus(scheduledStatus);

        builder.setType(arrivalOrDeparture);
        builder.setEstimatedTimeUtcMs(common.getTargetUtcDateTimeMs());
        // builder.setScheduledTimeUtcMs(..); // This we don't have here atm, and it's optional.
        builder.setLastModifiedUtcMs(common.getLastModifiedUtcDateTimeMs());
        return builder.build();
    }

    public static int joreDirectionToGtfsDirection(int joreDirection) {
        //Jore is 1-2, Gtfs-RT is 0-1
        if (joreDirection < 1 || joreDirection > 2) {
            throw new IllegalArgumentException("Jore direction has to be 1 or 2");
        }
        return joreDirection - 1;
    }
}
