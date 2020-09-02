package fi.hsl.common.pulsar;

import org.apache.pulsar.client.api.Message;
import org.jetbrains.annotations.NotNull;

public interface IMessageHandler {
    //Handler is expected to ack the message. TODO think about this
    //Throwing will abort the application main loop
    void handleMessage(@NotNull Message received) throws Exception;
}
