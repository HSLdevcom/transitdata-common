package fi.hsl.common.health;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HealthServerTest {

    private HealthServer healthServer;
    private final int testPort = 0;
    private final String testEndpoint = "/healthz";

    private static class CountingWrapper implements BooleanSupplier {

        private final BooleanSupplier delegate;
        private final AtomicInteger callCount = new AtomicInteger(0);

        public CountingWrapper(BooleanSupplier delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean getAsBoolean() {
            callCount.incrementAndGet();
            return delegate.getAsBoolean();
        }

        public int getCallCount() {
            return callCount.get();
        }
    }

    @Before
    public void setUp() throws IOException {
        healthServer = new HealthServer(testPort, testEndpoint);
    }

    @After
    public void tearDown() {
        if (healthServer != null) {
            healthServer.close();
        }
    }

    @Test
    public void singleUnhealthyCheckReturnsFalse() {
        CountingWrapper unhealthyCheck = new CountingWrapper(() -> false);
        healthServer.addCheck(unhealthyCheck);
        boolean healthStatus = healthServer.checkHealth();
        assertFalse("Health status should be false when one check is unhealthy.", healthStatus);
        assertEquals("UnhealthyCheck should have been called once.", 1, unhealthyCheck.getCallCount());
    }

    @Test
    public void allHealthyChecksReturnsTrue() {
        CountingWrapper healthyCheck1 = new CountingWrapper(() -> true);
        CountingWrapper healthyCheck2 = new CountingWrapper(() -> true);
        healthServer.addCheck(healthyCheck1);
        healthServer.addCheck(healthyCheck2);
        boolean healthStatus = healthServer.checkHealth();
        assertTrue("Health status should be true when all checks are healthy.", healthStatus);
        assertEquals("HealthyCheck1 should have been called once.", 1, healthyCheck1.getCallCount());
        assertEquals("HealthyCheck2 should have been called once.", 1, healthyCheck2.getCallCount());
    }

    @Test
    public void testCheckHealth_CheckThrowsException_ReturnsFalse() {
        final RuntimeException testException = new RuntimeException("Simulated check failure");
        CountingWrapper exceptionThrowingCheck = new CountingWrapper(() -> {
            throw testException;
        });
        healthServer.addCheck(exceptionThrowingCheck);
        boolean healthStatus = healthServer.checkHealth();
        assertFalse("Health status should be false when a check throws an exception.", healthStatus);
        assertEquals("ExceptionThrowingCheck should have been called once.", 1, exceptionThrowingCheck.getCallCount());
    }
}
