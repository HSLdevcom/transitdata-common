package fi.hsl.common.pulsar;

import org.apache.pulsar.client.api.Message;

public interface IMessageHandler {
    //Handler is expected to ack the message. TODO think about this
    //Throwing will abort the application main loop
    void handleMessage(Message received) throws Exception;
}
