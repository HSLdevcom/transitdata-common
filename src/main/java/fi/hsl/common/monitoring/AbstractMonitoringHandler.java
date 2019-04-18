package fi.hsl.common.monitoring;

import fi.hsl.common.monitoring.proto.Monitoring;

public abstract class AbstractMonitoringHandler<T> {

    protected String key;

    public AbstractMonitoringHandler(final String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public final synchronized void handleMessageSync(final T message) {
        handleMessage(message);
    }

    protected abstract void handleMessage(final T message);

    public final synchronized double getValueSync() {
        return getValue();
    }

    protected abstract double getValue();

    public final synchronized void clearValueSync() {
        clearValue();
    }

    protected abstract void clearValue();

    public final synchronized double getValueAndClearSync() {
        return getValueAndClear();
    }

    protected abstract double getValueAndClear();
}
