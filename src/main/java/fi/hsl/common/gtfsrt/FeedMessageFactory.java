package fi.hsl.common.gtfsrt;

import com.google.transit.realtime.GtfsRealtime;

import java.util.List;

public class FeedMessageFactory {
    private FeedMessageFactory(){}

    private static GtfsRealtime.FeedMessage createDifferentialFeedMessage(GtfsRealtime.FeedEntity entity, long timestampUtcSecs) {
        return GtfsRealtime.FeedMessage.newBuilder()
            .addEntity(entity)
            .setHeader(createFeedHeader(GtfsRealtime.FeedHeader.Incrementality.DIFFERENTIAL, timestampUtcSecs))
            .build();
    }

    public static GtfsRealtime.FeedMessage createDifferentialFeedMessage(String id, GtfsRealtime.VehiclePosition vehiclePosition, long timestampUtcSecs) {
        GtfsRealtime.FeedEntity entity = GtfsRealtime.FeedEntity.newBuilder()
            .setVehicle(vehiclePosition)
            .setId(id)
            .build();

        return createDifferentialFeedMessage(entity, timestampUtcSecs);
    }

    public static GtfsRealtime.FeedMessage createDifferentialFeedMessage(String id, GtfsRealtime.TripUpdate tripUpdate, long timestampUtcSecs) {
        GtfsRealtime.FeedEntity entity = GtfsRealtime.FeedEntity.newBuilder()
                .setTripUpdate(tripUpdate)
                .setId(id)
                .build();

        return createDifferentialFeedMessage(entity, timestampUtcSecs);
    }


    public static GtfsRealtime.FeedMessage createFullFeedMessage(List<GtfsRealtime.FeedEntity> entities, long timestampUtcSecs) {
        GtfsRealtime.FeedHeader header = createFeedHeader(GtfsRealtime.FeedHeader.Incrementality.FULL_DATASET, timestampUtcSecs);

        return GtfsRealtime.FeedMessage.newBuilder()
                .addAllEntity(entities)
                .setHeader(header)
                .build();
    }

    public static GtfsRealtime.FeedHeader createFeedHeader(GtfsRealtime.FeedHeader.Incrementality inc, long timestampUtcSecs) {
        return GtfsRealtime.FeedHeader.newBuilder()
                .setGtfsRealtimeVersion("2.0")
                .setIncrementality(inc)
                .setTimestamp(timestampUtcSecs)
                .build();
    }

}
