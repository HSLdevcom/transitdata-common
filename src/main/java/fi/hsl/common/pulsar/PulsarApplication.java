package fi.hsl.common.pulsar;

import com.typesafe.config.Config;
import fi.hsl.common.health.HealthServer;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.regex.Pattern;

public class PulsarApplication implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(PulsarApplication.class);

    Config config;

    PulsarApplicationContext context;

    Consumer<byte[]> consumer;
    Producer<byte[]> producer;
    PulsarClient client;
    PulsarAdmin admin;
    Jedis jedis;
    HealthServer healthServer;

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

        if (config.getBoolean("pulsar.admin.enabled")) {
            admin = createAdmin(
                    config.getString("pulsar.admin.host"),
                    config.getInt("pulsar.admin.port")
            );
        }

        if (config.getBoolean("redis.enabled")) {
            int connTimeOutSecs = 2;
            if (config.hasPath("redis.connTimeOutSecs")) {
                connTimeOutSecs = config.getInt("redis.connTimeOutSecs");
            }
            jedis = createRedisClient(
                    config.getString("redis.host"),
                    config.getInt("redis.port"),
                    connTimeOutSecs);
        }

        if (config.getBoolean("health.enabled")) {
            final int port = config.getInt("health.port");
            final String endpoint = config.getString("health.endpoint");

            final BooleanSupplier healthCheck = () -> {
                boolean status = true;
                if (producer != null) status &= producer.isConnected();
                if (consumer != null) status &= consumer.isConnected();
                if (jedis != null) status &= jedis.isConnected();
                return status;
            };

            final BooleanSupplier customRedisConnHealthCheck = () -> {
                boolean connOk = false;
                synchronized (jedis) {
                    try {
                        String maybePong = jedis.ping();
                        if (maybePong.equals("PONG")) {
                            connOk = true;
                        } else {
                            log.error("jedis.ping() returned: {}", maybePong);
                        }
                    } catch (Exception e) {
                        log.error("Exception in custom health check for redis connection", e);
                    }
                    return connOk;
                }
            };

            healthServer = new HealthServer(port, endpoint);
            healthServer.addCheck(healthCheck);

            if (config.hasPath("redis.customHealthCheckEnabled")) {
                if (config.getBoolean("redis.customHealthCheckEnabled")) {
                    log.info("Adding custom health check for Redis connection");
                    healthServer.addCheck(customRedisConnHealthCheck);
                }
            }
        }

        return createContext(config, client, consumer, producer, jedis, admin, healthServer);
    }

    protected Jedis createRedisClient(String redisHost, int port, int connTimeOutSecs) {
        log.info("Connecting to Redis at " + redisHost + ":" + port + " with connection timeout of (s): "+ connTimeOutSecs);
        int timeOutMs = connTimeOutSecs * 1000;
        Jedis jedis = new Jedis(redisHost, port, timeOutMs);
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

    protected PulsarApplicationContext createContext(Config config, PulsarClient client,
                                                     Consumer<byte[]> consumer, Producer<byte[]> producer,
                                                     Jedis jedis, PulsarAdmin admin, HealthServer healthServer) {
        PulsarApplicationContext context = new PulsarApplicationContext();
        context.setConfig(config);
        context.setClient(client);
        context.setConsumer(consumer);
        context.setProducer(producer);
        context.setJedis(jedis);
        context.setAdmin(admin);
        context.setHealthServer(healthServer);
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
            if (config.hasPath("pulsar.consumer.topics")) {
                List<String> topics = config.getStringList("pulsar.consumer.topics");
                log.info("Creating Pulsar consumer for topics: [ {} ]", String.join(", ", topics));
                builder = builder.topics(topics);
            } else {
                String topics = config.getString("pulsar.consumer.topicsPattern");
                log.info("Creating Pulsar consumer for multiple topics using pattern: " + topics);
                Pattern pattern = Pattern.compile(topics);
                builder = builder.topicsPattern(pattern);
            }
        }
        else {
            String topic = config.getString("pulsar.consumer.topic");
            log.info("Creating Pulsar consumer for single topic: " + topic);
            builder = builder.topic(topic);
        }

        if (config.hasPath("pulsar.consumer.ackTimeoutSecs")) {
            long ackTimeOutSecs = config.getLong("pulsar.consumer.ackTimeoutSecs");
            log.info("Setting message redelivery (ackTimeout) time to {} s in pulsar consumer subscription", ackTimeOutSecs);
            builder = builder.ackTimeout(ackTimeOutSecs, TimeUnit.SECONDS);
        }

        Consumer<byte[]> consumer = builder.subscribe();

        if (config.getBoolean("pulsar.consumer.cursor.resetToLatest")) {
            consumer.seek(MessageId.latest);
        }

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

    protected PulsarAdmin createAdmin(String adminHost, int adminPort) throws PulsarClientException {
        final String adminHttpUrl = String.format("http://%s:%d", adminHost, adminPort);
        log.info("Connecting to Pulsar Admin at " + adminHttpUrl);
        return PulsarAdmin.builder()
                .serviceHttpUrl(adminHttpUrl)
                .build();
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
        if (admin != null)
            admin.close();
        if (jedis != null)
            jedis.close();
        if (healthServer != null)
            healthServer.close();
    }
}
