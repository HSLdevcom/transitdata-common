package fi.hsl.common.health;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class HealthServer {
    private static final Logger log = LoggerFactory.getLogger(HealthServer.class);

    public final int port;
    public final String endpoint;
    public final HttpServer httpServer;
    private List<BooleanSupplier> checks = new ArrayList<>();

    public HealthServer(final int port, final String endpoint) throws IOException {
        this.port = port;
        this.endpoint = endpoint;
        log.info("Creating HealthServer, listening port {}, with endpoint {}", port, endpoint);
        httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpServer.createContext("/", createDefaultHandler());
        httpServer.createContext(endpoint, createHandler());
        httpServer.setExecutor(null);
        httpServer.start();
        log.info("HealthServer started");
    }

    private void writeResponse(final HttpExchange httpExchange, final int responseCode, final String responseBody) throws IOException {
        final byte[] response = responseBody.getBytes("UTF-8");
        httpExchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
        httpExchange.sendResponseHeaders(responseCode, response.length);
        final OutputStream out = httpExchange.getResponseBody();
        out.write(response);
        out.close();
    }

    private HttpHandler createDefaultHandler() {
        return httpExchange -> {
            final int responseCode = 404;
            final String responseBody = "Not Found";
            writeResponse(httpExchange, responseCode, responseBody);
        };
    }

    private HttpHandler createHandler() {
        return httpExchange -> {
            String method = httpExchange.getRequestMethod();
            int responseCode;
            String responseBody;
            if (!method.equals("GET")) {
                responseCode = 405;
                responseBody = "Method Not Allowed";
            } else {
                final boolean isHealthy = checkHealth();
                responseCode = isHealthy ? 200 : 503;
                responseBody = isHealthy ? "OK" : "FAIL";
            }
            writeResponse(httpExchange, responseCode, responseBody);
        };
    }

    public void addCheck(final BooleanSupplier check) {
        if (check != null) {
            checks.add(check);
        }
    }

    public void removeCheck(final BooleanSupplier check) {
        checks.remove(check);
    }

    public void clearChecks() {
        checks.clear();
    }

    public boolean checkHealth() {
        boolean isHealthy = true;
        for (final BooleanSupplier check : checks) {
            isHealthy &= check.getAsBoolean();
        }
        return isHealthy;
    }

    public void close() {
        if (httpServer != null) {
            httpServer.stop(0);
        }
    }
}
