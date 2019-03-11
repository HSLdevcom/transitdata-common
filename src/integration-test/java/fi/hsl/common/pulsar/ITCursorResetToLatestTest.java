package fi.hsl.common.pulsar;

import com.typesafe.config.Config;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.TypedMessageBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ITCursorResetToLatestTest extends ITBaseTestSuite {
    static final Logger logger = LoggerFactory.getLogger(ITCursorResetToLatestTest.class);

    private ArrayList<PulsarMessageData> getInitialMessages() {
        ArrayList<PulsarMessageData> list = new ArrayList<>();
        long ts = System.currentTimeMillis();
        for (int n = 0; n < 10; ++n) {
            String msg = "testmsg1_" + n;
            String key = "key1_" + n;
            PulsarMessageData data = new PulsarMessageData(msg.getBytes(), ts++, key);
            list.add(data);
        }
        return list;
    }

    private ArrayList<PulsarMessageData> getRuntimeMessages() {
        ArrayList<PulsarMessageData> list = new ArrayList<>();
        long ts = System.currentTimeMillis();
        for (int n = 0; n < 10; ++n) {
            String msg = "testmsg2_" + n;
            String key = "key2_" + n;
            PulsarMessageData data = new PulsarMessageData(msg.getBytes(), ts++, key);
            list.add(data);
        }
        return list;
    }

    private void sendInitialMessages(String testId, ArrayList<PulsarMessageData> messages) throws Exception {
        final String config = "integration-test-source.conf";
        PulsarApplication app = createPulsarApp(config, testId);
        Producer<byte[]> producer = app.getContext().getProducer();
        assertNotNull(producer);
        assertTrue(producer.isConnected());
        assertNull(app.getContext().getConsumer());
        for (PulsarMessageData message : messages) {
            TypedMessageBuilder<byte[]> msg = PulsarMessageData.toPulsarMessage(producer, message);
            msg.send();
        }
        app.close();
        assertFalse(producer.isConnected());
    }

    // test that output has only initial messages
    @Test
    public void testInitialMessages() throws Exception {
        final String testId = "-test-initial-messages";
        final String config = "integration-test-handler.conf";
        PulsarApplication app = createPulsarApp(config, testId);
        // send initial messages to topic
        ArrayList<PulsarMessageData> initialMessages = getInitialMessages();
        sendInitialMessages(testId, initialMessages);
        // create input and output messages
        ArrayList<PulsarMessageData> input =  new ArrayList<>();
        ArrayList<PulsarMessageData> output =  initialMessages;
        // test output
        NoopMessageHandler handler = new NoopMessageHandler(app.getContext());
        TestPipeline.MultiMessageTestLogic logic = new TestPipeline.MultiMessageTestLogic(input, output);
        testPulsarMessageHandler(handler, app, logic, testId);
    }

    // test that output has initial messages + runtime messages
    @Test
    public void testWithoutCursorResetToLatest() throws Exception {
        final String testId = "-test-without-cursor-reset-to-latest";
        final String config = "integration-test-handler.conf";
        PulsarApplication app = createPulsarApp(config, testId);
        // send initial messages to topic
        ArrayList<PulsarMessageData> initialMessages = getInitialMessages();
        ArrayList<PulsarMessageData> runtimeMessages = getRuntimeMessages();
        sendInitialMessages(testId, initialMessages);
        // create input and output messages
        ArrayList<PulsarMessageData> input = runtimeMessages;
        ArrayList<PulsarMessageData> output =  new ArrayList<>();
        output.addAll(initialMessages);
        output.addAll(runtimeMessages);
        // test output
        NoopMessageHandler handler = new NoopMessageHandler(app.getContext());
        TestPipeline.MultiMessageTestLogic logic = new TestPipeline.MultiMessageTestLogic(input, output);
        testPulsarMessageHandler(handler, app, logic, testId);
    }

    // test that output has only runtime messages
    @Test
    public void testWithCursorResetToLatest() throws Exception {
        final String testId = "-test-with-cursor-reset-to-latest";
        final String config = "integration-test-handler.conf";
        Config appConfig = PulsarMockApplication.readConfigWithOverride(config, "pulsar.consumer.cursor.resetToLatest", true);
        // send initial messages to topic
        ArrayList<PulsarMessageData> initialMessages = getInitialMessages();
        ArrayList<PulsarMessageData> runtimeMessages = getRuntimeMessages();
        sendInitialMessages(testId, initialMessages);
        // create input and output messages
        ArrayList<PulsarMessageData> input = runtimeMessages;
        ArrayList<PulsarMessageData> output =  runtimeMessages;
        // test output
        PulsarApplication app = createPulsarApp(appConfig, testId);
        NoopMessageHandler handler = new NoopMessageHandler(app.getContext());
        TestPipeline.MultiMessageTestLogic logic = new TestPipeline.MultiMessageTestLogic(input, output);
        testPulsarMessageHandler(handler, app, logic, testId);
    }

    // message handler which just passes messages unmodified
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

}
