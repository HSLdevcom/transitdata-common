package fi.hsl.common.gtfsrt;

import com.google.transit.realtime.GtfsRealtime;

import java.util.List;

public class GtfsRtUtils {
    private GtfsRtUtils(){}

    public static GtfsRealtime.FeedMessage createDifferentialFeedMessage(String id, GtfsRealtime.TripUpdate tripUpdate, long timestampUtcSecs) {
        GtfsRealtime.FeedHeader header = createFeedHeader(GtfsRealtime.FeedHeader.Incrementality.DIFFERENTIAL, timestampUtcSecs);

        GtfsRealtime.FeedEntity entity = GtfsRealtime.FeedEntity.newBuilder()
                .setTripUpdate(tripUpdate)
                .setId(id)
                .build();

        return GtfsRealtime.FeedMessage.newBuilder().addEntity(entity).setHeader(header).build();
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
