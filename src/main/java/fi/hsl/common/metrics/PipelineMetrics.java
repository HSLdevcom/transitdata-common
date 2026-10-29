package fi.hsl.common.metrics;

import io.prometheus.client.Histogram;

public final class PipelineMetrics {

    private static final double[] LATENCY_BUCKETS = {0.05, 0.1, 0.25, 0.5, 1, 2, 5, 10, 30, 60, 120};

    private static final Histogram E2E_LATENCY = Histogram.build().name("transitdata_e2e_latency_seconds")
            .help("Internal processing latency from ingest to produced output, per pipeline.").labelNames("pipeline")
            .buckets(LATENCY_BUCKETS).register();

    private static final Histogram PUBLISH_DURATION = Histogram.build()
            .name("transitdata_feed_publish_duration_seconds")
            .help("Time to build and write a full feed dump, per pipeline.").labelNames("pipeline")
            .buckets(LATENCY_BUCKETS).register();

    private PipelineMetrics() {
    }

    public static void observeLatency(final String pipeline, final double seconds) {
        E2E_LATENCY.labels(pipeline).observe(seconds);
    }

    public static void observePublishDuration(final String pipeline, final double seconds) {
        PUBLISH_DURATION.labels(pipeline).observe(seconds);
    }
}
