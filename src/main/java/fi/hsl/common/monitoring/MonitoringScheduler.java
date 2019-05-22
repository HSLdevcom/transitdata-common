package fi.hsl.common.monitoring;

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
    protected static final Logger log = LoggerFactory.getLogger(MonitoringScheduler.class);

    protected List<AbstractMonitoringHandler> handlers = new ArrayList<>();
    protected ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();;
    public long delay;
    public long interval;
    public Producer producer;
    public Runnable task;

    public MonitoringScheduler(final PulsarApplicationContext context, final long delay, final long interval, final Runnable task) {
        this.producer = context.getProducer();
        this.delay = delay;
        this.interval = interval;
        this.task = task;
    }

    public MonitoringScheduler(final PulsarApplicationContext context, final long delay, final long interval) {
        this.producer = context.getProducer();
        this.delay = delay;
        this.interval = interval;
        this.task = createDefaultTask();
    }

    public boolean addHandler(final AbstractMonitoringHandler handler) {
        if (!handlers.contains(handler)) {
            return handlers.add(handler);
        }
        return false;
    }

    public boolean removeHandler(final AbstractMonitoringHandler handler) {
        return handlers.remove(handler);
    }

    protected Runnable createDefaultTask() {
        return () -> {
            final long timestamp = System.currentTimeMillis();
            handlers.stream().forEach(handler -> {
                final String key = handler.getKey();
                final double value = handler.getValueAndClear();
                Monitoring.MonitoringMessage message = MonitoringMessageFactory.createMonitoringMessage(timestamp, key, value);
                TypedMessageBuilder<byte[]> messageBuilder = producer.newMessage()
                        .eventTime(timestamp)
                        .value(message.toByteArray());
                messageBuilder.sendAsync()
                        .whenComplete((id, throwable) -> {
                            if (throwable != null) {
                                log.warn("Failed to send Pulsar message.", throwable);
                            }
                        });
            });
        };
    }

    public void start() {
        scheduler.scheduleAtFixedRate(task, delay, interval, TimeUnit.SECONDS);
    }

    public void stop() {
        scheduler.shutdown();
    }
}
