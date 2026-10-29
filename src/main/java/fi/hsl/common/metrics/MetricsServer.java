package fi.hsl.common.metrics;

import io.prometheus.client.exporter.HTTPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MetricsServer implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(MetricsServer.class);

    public final int port;
    private final HTTPServer httpServer;

    public MetricsServer(final int port) throws IOException {
        this.port = port;
        log.info("Starting MetricsServer on port {}, endpoint /metrics", port);
        this.httpServer = new HTTPServer.Builder().withPort(port).build();
    }

    @Override
    public void close() {
        if (httpServer != null) {
            httpServer.close();
        }
    }
}
