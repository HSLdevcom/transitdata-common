package fi.hsl.common.pulsar;

import org.apache.pulsar.client.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Utility classes for creating a generic Test pipeline for Pulsar Integration tests
 */
public class TestPipeline {
    private TestPipeline() {}
    private static final Logger logger = LoggerFactory.getLogger(TestPipeline.class);

    public static final int DEFAULT_RECEIVE_TIMEOUT_MS = 500;

    public static class TestContext {
        public Producer<byte[]> source;
        public Consumer<byte[]> sink;
        public PulsarApplication testApp;
    }

    public static abstract class TestLogic {
        public void test(TestContext context) {
            try {
                testImpl(context);
            }
            catch (Exception e) {
                logger.error("Test failed!", e);
                assertTrue(false);
            }
        }

        //Perform your test and assertions in here. Send via source, read via sink.
        protected abstract void testImpl(TestContext context) throws Exception;
    }

    public static void validateAcks(int numberOfMessagesSent, TestPipeline.TestContext context) {
        assertEquals(numberOfMessagesSent, context.source.getStats().getNumAcksReceived());
    }


    public static Message<byte[]> readOutputMessage(TestPipeline.TestContext context) throws PulsarClientException {
        return readOutputMessage(context, DEFAULT_RECEIVE_TIMEOUT_MS);
    }

    public static Message<byte[]> readOutputMessage(TestPipeline.TestContext context, int timeoutMs) throws PulsarClientException {
        //Our Pipeline throughput should be few milliseconds but let's not assume it here, can create unwanted assertions.
        //Rather test performance separately with load tests.
        Message<byte[]> received = context.sink.receive(timeoutMs, TimeUnit.MILLISECONDS);
        if (received != null) {
            context.sink.acknowledge(received);
        }
        return received;
    }


    public static class MultiMessageTestLogic extends TestLogic {

        protected final ArrayList<PulsarMessageData> input;
        protected final ArrayList<PulsarMessageData> expectedOutput;

        public MultiMessageTestLogic(ArrayList<PulsarMessageData> in,
                                     ArrayList<PulsarMessageData> out) {
            input = in;
            expectedOutput = out;
            logger.info("Sending {} messages and expecting {} back", input.size(), expectedOutput.size());
        }

        @Override
        public void testImpl(TestContext context) throws Exception {
            //For simplicity let's just send all messages first and then read them back.
            logger.info("Sending {} messages", input.size());
            long now = System.currentTimeMillis();

            for(PulsarMessageData inputData : input) {
                TypedMessageBuilder<byte[]> msg = PulsarMessageData.toPulsarMessage(context.source, inputData);
                msg.sendAsync();
            }
            logger.info("Messages sent in {} ms, reading them back", (System.currentTimeMillis() - now));

            final long expectedCount = expectedOutput.size();
            ArrayList<Message<byte[]>> buffer = new ArrayList<>();
            now = System.currentTimeMillis();
            while (buffer.size() < expectedCount) {
                Message<byte[]> read = readOutputMessage(context);
                assertNotNull("Was expecting more messages but got null!", read);
                buffer.add(read);
            }
            logger.info("{} messages read back in {} ms", buffer.size(), (System.currentTimeMillis() - now));

            assertEquals(expectedCount, buffer.size());
            //All input messages should have been acked.
            validateAcks(input.size(), context);

            validateOutput(buffer);
        }

        public void validateOutput(ArrayList<Message<byte[]>> receivedQueue) {
            assertEquals(expectedOutput.size(), receivedQueue.size());
            ListIterator<Message<byte[]>> itrRecv = receivedQueue.listIterator();
            ListIterator<PulsarMessageData> itrExp = expectedOutput.listIterator();

            while (itrRecv.hasNext()) {
                Message<byte[]> receivedMsg = itrRecv.next();
                PulsarMessageData received = PulsarMessageData.fromPulsarMessage(receivedMsg);
                PulsarMessageData expected = itrExp.next();

                validateMessage(expected, received);
            }
        }

        /**
         * Override this for your own check if needed
         */
        public void validateMessage(PulsarMessageData expected, PulsarMessageData received) {
            assertNotNull(expected);
            assertNotNull(received);
            assertEquals(expected, received);
        }
    }
}
