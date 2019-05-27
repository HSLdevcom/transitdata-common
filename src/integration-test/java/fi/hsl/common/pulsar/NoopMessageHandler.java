package fi.hsl.common.pulsar;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.Producer;

/**
 *  Message handler which just passes messages unmodified
 */
public class NoopMessageHandler implements IMessageHandler {
    private Consumer<byte[]> consumer;
    private Producer<byte[]> producer;

    public NoopMessageHandler(PulsarApplicationContext context) {
        consumer = context.getConsumer();
        producer = context.getProducer();
    }

    public void handleMessage(Message received) throws Exception {
        consumer.acknowledgeAsync(received);
        producer.newMessage()
                .key(received.getKey())
                .eventTime(received.getEventTime())
                .properties(received.getProperties())
                .value(received.getData())
                .sendAsync();
    }
}
