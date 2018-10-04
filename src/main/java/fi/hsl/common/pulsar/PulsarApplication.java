package fi.hsl.common.pulsar;

import com.typesafe.config.Config;
import org.apache.pulsar.client.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class PulsarApplication implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(PulsarApplication.class);

    Config config;

    PulsarApplicationContext context;

    Consumer<byte[]> consumer;
    Producer<byte[]> producer;
    PulsarClient client;
    Jedis jedis;

    PulsarApplication() {
    }

    public static PulsarApplication newInstance(Config config) throws Exception {
        PulsarApplication app = null;
        try {
            app = new PulsarApplication();
            app.context = app.initialize(config);
            return app;
        }
        catch (Exception e) {
            log.error("Failed to create new PulsarApplication instance", e);
            //Let's clear all related resources in case we fail
            if (app != null) {
                app.close();
            }
            throw e;
        }
    }

    public PulsarApplicationContext getContext() {
        return context;
    }

    public PulsarApplicationContext initialize(Config config) throws Exception {
        this.config = config;

        client = createPulsarClient(
            config.getString("pulsar.host"),
            config.getInt("pulsar.port")
        );

        if (config.getBoolean("pulsar.producer.enabled")) {
            producer = createProducer(client, config);
        }

        if (config.getBoolean("pulsar.consumer.enabled")) {
            consumer = createConsumer(client, config);
        }

        if (config.getBoolean("redis.enabled")) {
            jedis = createRedisClient(
                    config.getString("redis.host"),
                    config.getInt("redis.port"));
        }

        return createContext(config, client, consumer, producer, jedis);
    }

    protected Jedis createRedisClient(String redisHost, int port) {
        log.info("Connecting to Redis at " + redisHost + ":" + port);
        Jedis jedis = new Jedis(redisHost, port);
        jedis.connect();
        log.info("Redis connected: " + jedis.isConnected());
        return jedis;
    }

    protected PulsarClient createPulsarClient(String pulsarHost, int pulsarPort) throws Exception {
        final String pulsarUrl = String.format("pulsar://%s:%d", pulsarHost, pulsarPort);

        log.info("Connecting to Pulsar at " + pulsarUrl);
        return PulsarClient.builder()
                .serviceUrl(pulsarUrl)
                .build();
    }

    protected PulsarApplicationContext createContext(Config config, PulsarClient client, Consumer<byte[]> consumer, Producer<byte[]> producer, Jedis jedis) {
        PulsarApplicationContext context = new PulsarApplicationContext();
        context.setConfig(config);
        context.setClient(client);
        context.setConsumer(consumer);
        context.setProducer(producer);
        context.setJedis(jedis);
        return context;
    }

    protected Consumer<byte[]> createConsumer(PulsarClient client, Config config) throws PulsarClientException {
        String subscription = config.getString("pulsar.consumer.subscription");
        SubscriptionType subscriptionType = SubscriptionType.valueOf(config.getString("pulsar.consumer.subscriptionType"));
        boolean readCompacted = subscriptionType != SubscriptionType.Shared; // Shared mode doesn't allow compacted reads
        int queueSize = config.getInt("pulsar.consumer.queueSize");

        ConsumerBuilder<byte[]> builder = client.newConsumer()
                .subscriptionName(subscription)
                .readCompacted(readCompacted)
                .receiverQueueSize(queueSize)
                .subscriptionType(subscriptionType);

        if (config.getBoolean("pulsar.consumer.multipleTopics")) {
            String topics = config.getString("pulsar.consumer.topicsPattern");
            log.info("Creating Pulsar consumer for multiple topics using pattern: " + topics);
            Pattern pattern = Pattern.compile(topics);
            builder = builder.topicsPattern(pattern);
        }
        else {
            String topic = config.getString("pulsar.consumer.topic");
            log.info("Creating Pulsar consumer for single topic: " + topic);
            builder = builder.topic(topic);
        }

        Consumer<byte[]> consumer = builder.subscribe();
        log.info("Pulsar consumer created with subscription " + subscription + " (" + subscriptionType + ")");
        return consumer;
    }

    protected Producer<byte[]> createProducer(PulsarClient client, Config config) throws PulsarClientException {
        int queueSize = config.getInt("pulsar.producer.queueSize");
        boolean blockIfFull = config.getBoolean("pulsar.producer.blockIfFull");
        String topic = config.getString("pulsar.producer.topic");

        Producer<byte[]> producer = client.newProducer()
                .compressionType(CompressionType.LZ4)
                .maxPendingMessages(queueSize)
                .topic(topic)
                .enableBatching(false)
                .blockIfQueueFull(blockIfFull)
                .create();
        log.info("Pulsar producer created to topic " + topic);
        return producer;
    }

    public void launchWithHandler(IMessageHandler handler) throws Exception {
        if (consumer == null) {
            throw new Exception("Consumer disabled, cannot start the handler");
        }
        try {
            while (true) {
                Message msg = consumer.receive(5, TimeUnit.SECONDS);
                if (msg != null) {
                    handler.handleMessage(msg);
                }
                else if (!consumer.isConnected()) {
                    //Pulsar client goes into retry-mode in case the connection is lost after once acquired.
                    //We will rather abort and handle the errors ourselves
                    throw new PulsarClientException("Connection lost");
                }
                //TODO move Ack and possibly message sending to here
            }
        }
        catch (Exception ex) {
            log.error("Exception in main handler loop", ex);
            throw ex;
        }
    }

    public void close() {
        log.info("Closing PulsarApplication resources");
        try {
            if (producer != null)
                producer.close();
        } catch (PulsarClientException e) {
            log.error("Failed to close pulsar producer", e);
        }
        try {
            if (consumer != null)
                consumer.close();
        } catch (PulsarClientException e) {
            log.error("Failed to close pulsar consumer", e);
        }
        try {
            if (client != null)
                client.close();
        } catch (PulsarClientException e) {
            log.error("Failed to close pulsar client", e);
        }
        if (jedis != null)
            jedis.close();
    }
}
