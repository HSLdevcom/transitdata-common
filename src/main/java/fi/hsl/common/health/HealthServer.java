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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.BooleanSupplier;

public class HealthServer {
    private static final Logger log = LoggerFactory.getLogger(HealthServer.class);

    public final int port;
    public final String endpoint;
    public final HttpServer httpServer;
    private final ExecutorService healthCheckExecutor =
        Executors.newCachedThreadPool();
    private final List<BooleanSupplier> checks = new CopyOnWriteArrayList<>();

    public HealthServer(final int port, @NotNull final String endpoint) throws IOException {
        this.port = port;
        this.endpoint = endpoint;
        log.info("Creating HealthServer, listening port {}, with endpoint {}", port, endpoint);
        httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpServer.createContext("/", createDefaultHandler());
        httpServer.createContext(endpoint, createHandler());
        httpServer.setExecutor(healthCheckExecutor);
        httpServer.start();
        log.info("HealthServer started");
    }

    private void writeResponse(@NotNull final HttpExchange httpExchange, @NotNull final int responseCode, @NotNull final String responseBody) throws IOException {
        final byte[] response = responseBody.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "text/plain; charset=" + StandardCharsets.UTF_8.name());
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
        try {
            CompletionService<Boolean> executorCompletionService
                    = new ExecutorCompletionService<>(healthCheckExecutor);
            int n = checks.size();
            List<Future<Boolean>> futures = new ArrayList<>(n);
            try {
                for (BooleanSupplier check : checks) {
                    futures.add(executorCompletionService.submit(checkToCallable(check)));
                }
                for (int i = 0; i < n; ++i) {
                    try {
                        Boolean result = executorCompletionService.take().get();
                        if (result == null || !result) {
                            return false; // Return false immediately if any check fails
                        }
                    } catch (ExecutionException ignore) {}
                }
            } finally {
                for (Future<Boolean> f : futures) {
                    f.cancel(true);
                }
            }
            
            return true; // Return true only if all checks pass
        } catch (Exception e) {
            log.error("Exception during health checks", e);
            return false;
        }
    }

    public void close() {
        if (httpServer != null) {
            httpServer.stop(0);
        }
        if (healthCheckExecutor != null) {
            healthCheckExecutor.shutdown();
            try {
                if (!healthCheckExecutor.awaitTermination(3, TimeUnit.SECONDS)) {
                    healthCheckExecutor.shutdownNow();
                }
            } catch (InterruptedException ie) {
                healthCheckExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
    
    private static Callable<Boolean> checkToCallable(BooleanSupplier check) {
        return () -> {
            try {
                return check.getAsBoolean();
            } catch (Exception e) {
                log.error("Exception during health check", e);
                return false;
            }
        };
    }
}
