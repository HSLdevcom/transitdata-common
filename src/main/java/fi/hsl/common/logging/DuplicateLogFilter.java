package fi.hsl.common.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DuplicateLogFilter extends Filter<ILoggingEvent> {
    private final ConcurrentMap<String, Long> messageTimestamps = new ConcurrentHashMap<>();
    private long suppressIntervalMillis = 0;
    private long logCacheMaxAgeMillis = 0;

    public void setSuppressIntervalMillis(long suppressIntervalMillis) {
        this.suppressIntervalMillis = suppressIntervalMillis;
    }

    public void setLogMaxAgeInMillis(long logMaxAgeInMillis) {
        this.logCacheMaxAgeMillis = logMaxAgeInMillis;
    }

    @Override
    public FilterReply decide(ILoggingEvent event) {
        String message = event.getFormattedMessage();
        long currentTimeMillis = System.currentTimeMillis();

        Long lastTimestamp = messageTimestamps.put(message, currentTimeMillis);
        if (lastTimestamp != null && (currentTimeMillis - lastTimestamp) < suppressIntervalMillis) {
            return FilterReply.DENY;
        }

        // Evict old entries (example: entries older than 1 hour)
        cleanMapByLogAge(currentTimeMillis);

        return FilterReply.NEUTRAL;
    }

    private void cleanMapByLogAge(long currentTime) {
        if (logCacheMaxAgeMillis > 0) {
            messageTimestamps.entrySet().removeIf(entry -> (currentTime - entry.getValue()) > logCacheMaxAgeMillis);
        }
    }
}