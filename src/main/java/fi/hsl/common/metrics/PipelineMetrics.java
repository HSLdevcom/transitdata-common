package fi.hsl.common.metrics;

import fi.hsl.common.transitdata.TransitdataProperties;
import io.prometheus.client.Histogram;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.TypedMessageBuilder;

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

    /**
     * Resolve the pipeline-ingress timestamp (epoch ms) for a message being produced: forwarded from the incoming
     * message's source timestamp when present, otherwise the current time. Pass null for a source component that has
     * no incoming Pulsar message.
     */
    public static long resolveSourceTimestampMs(final Message<?> incoming) {
        if (incoming != null) {
            final String raw = incoming.getProperty(TransitdataProperties.KEY_SOURCE_MESSAGE_TIMESTAMP_MS);
            if (raw != null && !raw.isEmpty()) {
                try {
                    return Long.parseLong(raw);
                } catch (final NumberFormatException ignored) {
                    // malformed value, fall through to current time
                }
            }
        }
        return System.currentTimeMillis();
    }

    /**
     * Stamp the pipeline-ingress (source) timestamp on an outgoing message so it propagates through the pipeline. The
     * first producer in a chain stamps the current time; downstream stages forward the value unchanged. Pass the
     * incoming message to forward, or null at a source component.
     */
    public static void stampSourceTimestamp(final TypedMessageBuilder<?> out, final Message<?> incoming) {
        out.property(TransitdataProperties.KEY_SOURCE_MESSAGE_TIMESTAMP_MS,
                String.valueOf(resolveSourceTimestampMs(incoming)));
    }

    /**
     * Observe pipeline latency as now minus the message's source (pipeline-ingress) timestamp. No observation is
     * recorded when the message carries no valid source timestamp.
     */
    public static void observeLatencyFromSource(final String pipeline, final Message<?> message) {
        if (message == null) {
            return;
        }
        final String raw = message.getProperty(TransitdataProperties.KEY_SOURCE_MESSAGE_TIMESTAMP_MS);
        if (raw == null || raw.isEmpty()) {
            return;
        }
        try {
            final long sourceTs = Long.parseLong(raw);
            if (sourceTs > 0) {
                observeLatency(pipeline, (System.currentTimeMillis() - sourceTs) / 1000.0);
            }
        } catch (final NumberFormatException ignored) {
            // no observation for malformed timestamp
        }
    }
}
