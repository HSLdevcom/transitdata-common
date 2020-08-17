package fi.hsl.common.pulsar;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.Producer;

import java.util.Collection;
import java.util.Set;

/**
 *  Message handler which just passes messages unmodified
 */
public class NoopMessageHandler implements IMessageHandler {
    private Consumer<byte[]> consumer;
    private Collection<Producer<byte[]>> producers;

    public NoopMessageHandler(PulsarApplicationContext context) {
        consumer = context.getConsumer();
        producers = context.getProducers().values();
    }

    public void handleMessage(Message received) throws Exception {
        consumer.acknowledgeAsync(received);
        for(Producer producer : producers){
            producer.newMessage()
                    .key(received.getKey())
                    .eventTime(received.getEventTime())
                    .properties(received.getProperties())
                    .value(received.getData())
                    .sendAsync();
        }
    }
}
