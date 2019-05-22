package fi.hsl.common.monitoring;

public abstract class AbstractMonitoringHandler<T> {

    protected String key;

    protected AbstractMonitoringHandler() {}

    public AbstractMonitoringHandler(final String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public final synchronized void handleMessage(final T message) { handleMessageImpl(message); }

    protected abstract void handleMessageImpl(final T message);

    public final synchronized double getValue() {
        return getValueImpl();
    }

    protected abstract double getValueImpl();

    public final synchronized void clearValue() {
        clearValueImpl();
    }

    protected abstract void clearValueImpl();

    public final synchronized double getValueAndClear() {
        return getValueAndClearImpl();
    }

    protected abstract double getValueAndClearImpl();
}
