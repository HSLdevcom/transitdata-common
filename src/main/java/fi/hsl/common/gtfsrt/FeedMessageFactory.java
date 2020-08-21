package fi.hsl.common.gtfsrt;

import com.google.transit.realtime.GtfsRealtime;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FeedMessageFactory {
    private FeedMessageFactory(){}

    @NotNull
    private static GtfsRealtime.FeedMessage createDifferentialFeedMessage(@NotNull GtfsRealtime.FeedEntity entity, long timestampUtcSecs) {
        return GtfsRealtime.FeedMessage.newBuilder()
            .addEntity(entity)
            .setHeader(createFeedHeader(GtfsRealtime.FeedHeader.Incrementality.DIFFERENTIAL, timestampUtcSecs))
            .build();
    }

    @NotNull
    public static GtfsRealtime.FeedMessage createDifferentialFeedMessage(@NotNull String id, @NotNull GtfsRealtime.VehiclePosition vehiclePosition, long timestampUtcSecs) {
        GtfsRealtime.FeedEntity entity = GtfsRealtime.FeedEntity.newBuilder()
            .setVehicle(vehiclePosition)
            .setId(id)
            .build();

        return createDifferentialFeedMessage(entity, timestampUtcSecs);
    }

    @NotNull
    public static GtfsRealtime.FeedMessage createDifferentialFeedMessage(@NotNull String id, @NotNull GtfsRealtime.TripUpdate tripUpdate, long timestampUtcSecs) {
        GtfsRealtime.FeedEntity entity = GtfsRealtime.FeedEntity.newBuilder()
                .setTripUpdate(tripUpdate)
                .setId(id)
                .build();

        return createDifferentialFeedMessage(entity, timestampUtcSecs);
    }


    @NotNull
    public static GtfsRealtime.FeedMessage createFullFeedMessage(@NotNull List<GtfsRealtime.FeedEntity> entities, long timestampUtcSecs) {
        GtfsRealtime.FeedHeader header = createFeedHeader(GtfsRealtime.FeedHeader.Incrementality.FULL_DATASET, timestampUtcSecs);

        return GtfsRealtime.FeedMessage.newBuilder()
                .addAllEntity(entities)
                .setHeader(header)
                .build();
    }

    @NotNull
    public static GtfsRealtime.FeedHeader createFeedHeader(@NotNull GtfsRealtime.FeedHeader.Incrementality inc, long timestampUtcSecs) {
        return GtfsRealtime.FeedHeader.newBuilder()
                .setGtfsRealtimeVersion("2.0")
                .setIncrementality(inc)
                .setTimestamp(timestampUtcSecs)
                .build();
    }

}
