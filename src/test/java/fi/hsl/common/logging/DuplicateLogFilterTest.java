package fi.hsl.common.logging;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DuplicateLogFilterTest {

    private TestAppender testAppender;
    private Logger logger;

    @Before
    public void setUp() {
        // Set up the logger and attach the test appender
        logger = (Logger) LoggerFactory.getLogger("testLogger");
        logger.setAdditive(false);

        testAppender = new TestAppender();
        DuplicateLogFilter filter = new DuplicateLogFilter();
        filter.setSuppressIntervalMillis(1000);
        testAppender.addFilter(filter); // Add the filter here
        testAppender.start();
        logger.addAppender(testAppender);
    }

    @Test
    public void testDuplicateLogFilter() {
        // Log messages
        logger.info("Test message");
        logger.info("Test message"); // Duplicate
        logger.info("Another message");

        // Verify captured logs
        List<String> loggedMessages = testAppender.getLoggedMessages();
        assertEquals(2, loggedMessages.size());
        assertEquals("Test message", loggedMessages.get(0));
        assertEquals("Another message", loggedMessages.get(1));
    }

    // Custom appender to capture log events
    private static class TestAppender extends AppenderBase<ILoggingEvent> {
        private final List<String> loggedMessages = new ArrayList<>();

        @Override
        protected void append(ILoggingEvent eventObject) {
            loggedMessages.add(eventObject.getFormattedMessage());
        }

        public List<String> getLoggedMessages() {
            return loggedMessages;
        }
    }
}