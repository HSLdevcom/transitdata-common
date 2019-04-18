package fi.hsl.common.monitoring;

import fi.hsl.common.monitoring.proto.Monitoring;

public class MonitoringMessageFactory {
    private MonitoringMessageFactory() {}

    public static Monitoring.MonitoringMessage createMonitoringMessage(final long timestamp, final String key, final double value) {
        Monitoring.MonitoringMessage.Builder builder = createMonitoringMessageBuilder(key, value);
        builder.setTimestamp(timestamp);
        return builder.build();
    }

    public static Monitoring.MonitoringMessage createMonitoringMessage(final String key, final double value) {
        Monitoring.MonitoringMessage.Builder builder = createMonitoringMessageBuilder(key, value);
        return builder.build();
    }

    private static Monitoring.MonitoringMessage.Builder createMonitoringMessageBuilder(final String key, final double value) {
        Monitoring.MonitoringMessage.Builder builder = Monitoring.MonitoringMessage.newBuilder();
        builder.setSchemaVersion(builder.getSchemaVersion());
        builder.setKey(key);
        builder.setValue(value);
        return builder;
    }
}
