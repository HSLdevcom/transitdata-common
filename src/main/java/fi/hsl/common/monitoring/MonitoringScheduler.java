package fi.hsl.common.monitoring;

import com.typesafe.config.Config;
import fi.hsl.common.monitoring.proto.Monitoring;
import fi.hsl.common.pulsar.PulsarApplicationContext;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.TypedMessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MonitoringScheduler {
    protected static final Logger LOGGER = LoggerFactory.getLogger(MonitoringScheduler.class);

    protected static List<AbstractMonitoringHandler> handlers = new ArrayList<>();
    protected static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();;
    protected static long delay;
    protected static long interval;
    protected static Producer producer;
    protected static Runnable runnable;

    protected static MonitoringScheduler ourInstance = new MonitoringScheduler();

    public static MonitoringScheduler getInstance(final PulsarApplicationContext context, final long delay, final long interval, final Runnable runnable) {
        producer = context.getProducer();
        MonitoringScheduler.delay = delay;
        MonitoringScheduler.interval = interval;
        MonitoringScheduler.runnable = runnable;
        return ourInstance;
    }

    public static MonitoringScheduler getInstance(final PulsarApplicationContext context, final long delay, final long interval) {
        return getInstance(context, delay, interval, ourInstance.getRunnable());
    }

    protected MonitoringScheduler() {}

    public boolean addHandler(final AbstractMonitoringHandler handler) {
        if (!handlers.contains(handler)) {
            return handlers.add(handler);
        }
        return false;
    }

    public boolean removeHandler(final AbstractMonitoringHandler handler) {
        return handlers.remove(handler);
    }

    protected Runnable getRunnable() {
        return () -> {
            final long timestamp = System.currentTimeMillis();
            handlers.stream().forEach(handler -> {
                final String key = handler.getKey();
                final double value = handler.getValueAndClearSync();
                Monitoring.MonitoringMessage message = MonitoringMessageFactory.createMonitoringMessage(timestamp, key, value);
                TypedMessageBuilder<byte[]> messageBuilder = producer.newMessage()
                        .eventTime(timestamp)
                        .value(message.toByteArray());
                messageBuilder.sendAsync()
                        .whenComplete((id, throwable) -> {
                            if (throwable != null) {
                                LOGGER.warn("Failed to send Pulsar message.", throwable);
                            }
                        });
            });
        };
    }

    public void startScheduler() {
        scheduler.scheduleAtFixedRate(runnable, delay, interval, TimeUnit.SECONDS);
    }

    public void stopScheduler() {
        scheduler.shutdown();
    }
}
