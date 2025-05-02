package fi.hsl.common.health;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BooleanSupplier;

public class HealthServer {
    private static final Logger log = LoggerFactory.getLogger(HealthServer.class);

    public final int port;
    public final String endpoint;
    public final HttpServer httpServer;
    private List<BooleanSupplier> checks = new ArrayList<>();

    public HealthServer(final int port, @NotNull final String endpoint) throws IOException {
        this.port = port;
        this.endpoint = endpoint;
        log.info("Creating HealthServer, listening port {}, with endpoint {}", port, endpoint);
        httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpServer.createContext("/", createDefaultHandler());
        httpServer.createContext(endpoint, createHandler());
        httpServer.setExecutor(Executors.newSingleThreadExecutor());
        httpServer.start();
        log.info("HealthServer started");
    }

    private void writeResponse(@NotNull final HttpExchange httpExchange, @NotNull final int responseCode, @NotNull final String responseBody) throws IOException {
        final byte[] response = responseBody.getBytes("UTF-8");
        httpExchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
        httpExchange.sendResponseHeaders(responseCode, response.length);
        final OutputStream out = httpExchange.getResponseBody();
        out.write(response);
        out.close();
    }

    @NotNull
    private HttpHandler createDefaultHandler() {
        return httpExchange -> {
            final int responseCode = 404;
            final String responseBody = "Not Found";
            writeResponse(httpExchange, responseCode, responseBody);
        };
    }

    @NotNull
    private HttpHandler createHandler() {
        return httpExchange -> {
            String method = httpExchange.getRequestMethod();
            int responseCode;
            String responseBody;
            final String requestEndpoint = httpExchange.getRequestURI().toString().replaceAll("(?:^\\/|\\/$)", "");
            final String expectedEndpoint = endpoint.replaceAll("(?:^\\/|\\/$)", "");
            if (!expectedEndpoint.equals(requestEndpoint)) {
                responseCode = 404;
                responseBody = "Not Found";
            } else if (!method.equals("GET")) {
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

    public void addCheck(@NotNull final BooleanSupplier check) {
        if (check != null) {
            checks.add(check);
        }
    }

    public void removeCheck(@NotNull final BooleanSupplier check) {
        checks.remove(check);
    }

    public void clearChecks() {
        checks.clear();
    }
    
    public boolean checkHealth() {
        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            List<Future<Boolean>> results = new ArrayList<>();
            for (BooleanSupplier check : checks) {
                results.add(executor.submit(check::getAsBoolean));
            }
            for (Future<Boolean> result : results) {
                if (!result.get()) {
                    return false; // If any check fails, return false
                }
            }
            return true; // All checks passed
        } catch (Exception e) {
            log.error("Exception during health checks", e);
            return false;
        } finally {
            executor.shutdown();
        }
    }

    public void close() {
        if (httpServer != null) {
            httpServer.stop(0);
        }
    }
}
