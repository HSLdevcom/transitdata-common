package fi.hsl.common.pulsar;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.typesafe.config.Config;
import fi.hsl.common.health.HealthServer;
import fi.hsl.common.redis.RedisUtils;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.api.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static fi.hsl.common.redis.RedisUtils.createJedisClient;
import static fi.hsl.common.redis.RedisUtils.extractUsernameFromToken;

public class PulsarApplication implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(PulsarApplication.class);

    Config config;

    PulsarApplicationContext context;

    Consumer<byte[]> consumer;
    Map<String, @NotNull Producer<byte[]>> producers;
    PulsarClient client;
    PulsarAdmin admin;
    Jedis jedis;
    HealthServer healthServer;

    PulsarApplication() {
    }

    @NotNull
    public static PulsarApplication newInstance(@NotNull Config config) throws Exception {
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

    @NotNull
    public PulsarApplicationContext getContext() {
        return context;
    }

    @NotNull
    public PulsarApplicationContext initialize(@NotNull Config config) throws Exception {
        this.config = config;

        client = createPulsarClient(
            config.getString("pulsar.host"),
            config.getInt("pulsar.port")
        );

        if (config.getBoolean("pulsar.producer.enabled")) {
            producers = createProducers(client, config);
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

            final BooleanSupplier pulsarHealthCheck = () -> {
                boolean status = true;
                if (producers != null && producers.values().stream().anyMatch(producer ->  !producer.isConnected())  ){
                    status = false;
                    log.error("HealthCheck: Pulsar producer is not connected");
                }
                if (consumer != null && !consumer.isConnected()) {
                    status = false;
                    log.error("HealthCheck: Pulsar consumer is not connected");
                }
                return status;
            };

            healthServer = new HealthServer(port, endpoint);
            healthServer.addCheck(pulsarHealthCheck);

            final BooleanSupplier jedisConnHealthCheck = () -> {
                // this doesn't seem to work very reliably
                return jedis.isConnected();
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

            if (config.hasPath("redis.customHealthCheckEnabled")) {
                if (config.getBoolean("redis.customHealthCheckEnabled")) {
                    log.info("Adding custom health check for Redis connection");
                    healthServer.addCheck(customRedisConnHealthCheck);
                }
            } else if (jedis != null) {
                healthServer.addCheck(jedisConnHealthCheck);
            }
        }

        return createContext(config, client, consumer, producers, jedis, admin, healthServer);
    }

    @NotNull
    protected Jedis createRedisClient(@NotNull String redisHost, int port, int connTimeOutSecs) {
        log.info("Connecting to Redis at " + redisHost + ":" + port + " with connection timeout of (s): "+ connTimeOutSecs);
        
        //Construct a Token Credential from Identity library, e.g. DefaultAzureCredential / ClientSecretCredential / Client CertificateCredential / ManagedIdentityCredential etc.
        DefaultAzureCredential defaultAzureCredential = new DefaultAzureCredentialBuilder().build();

        // Fetch a Microsoft Entra token to be used for authentication. This token will be used as the password.
        TokenRequestContext trc = new TokenRequestContext().addScopes("https://redis.azure.com/.default");
        RedisUtils.TokenRefreshCache tokenRefreshCache = new RedisUtils.TokenRefreshCache(defaultAzureCredential, trc);
        AccessToken accessToken = tokenRefreshCache.getAccessToken();

        // SSL connection is required.
        boolean useSsl = true;
        String username = extractUsernameFromToken(accessToken.getToken());
        
        // Create Jedis client and connect to the Azure Cache for Redis over the TLS/SSL port using the access token as password.
        // Note: Cache Host Name, Port, Microsoft Entra access token and SSL connections are required below.
        Jedis jedis = createJedisClient(redisHost, port, username, accessToken, useSsl);

        // Configure the jedis instance for proactive authentication before token expires.
        tokenRefreshCache.setJedisInstanceToAuthenticate(jedis);
        
        int maxTries = 3;
        int i = 0;
        
        while (i < maxTries) {
            try {
                // Set a value against your key in the Redis cache.
                jedis.set("Az:key", "testValue");
                System.out.println(jedis.get("Az:key"));
                break;
            } catch (JedisException e) {
                // Handle The Exception as required in your application.
                e.printStackTrace();
                
                // For Exceptions containing Invalid Username Password / Permissions not granted error messages, look at troubleshooting section at the end of document.
                
                // Check if the client is broken, if it is then close and recreate it to create a new healthy connection.
                if (jedis.isBroken()) {
                    jedis.close();
                    accessToken = tokenRefreshCache.getAccessToken();
                    jedis = createJedisClient(redisHost, port, username, accessToken, useSsl);
                    
                    // Configure the jedis instance for proactive authentication before token expires.
                    tokenRefreshCache.setJedisInstanceToAuthenticate(jedis);
                }
            }
            i++;
        }
        
        log.info("Redis connected: " + jedis.isConnected());
        return jedis;
    }

    @NotNull
    protected PulsarClient createPulsarClient(@NotNull String pulsarHost, int pulsarPort) throws Exception {
        final String pulsarUrl = String.format("pulsar://%s:%d", pulsarHost, pulsarPort);

        log.info("Connecting to Pulsar at " + pulsarUrl);
        return PulsarClient.builder()
                .serviceUrl(pulsarUrl)
                .build();
    }

    @NotNull
    protected PulsarApplicationContext createContext(@NotNull Config config, @NotNull PulsarClient client,
                @Nullable Consumer<byte[]> consumer, @Nullable Map<@NotNull String, @NotNull Producer<byte[]>> producers,
                @Nullable Jedis jedis, @Nullable PulsarAdmin admin, @Nullable HealthServer healthServer) {
        PulsarApplicationContext context = new PulsarApplicationContext();
        context.setConfig(config);
        context.setClient(client);
        context.setConsumer(consumer);
        context.setProducers(producers);
        context.setJedis(jedis);
        context.setAdmin(admin);
        context.setHealthServer(healthServer);
        return context;
    }

    @NotNull
    protected Consumer<byte[]> createConsumer(@NotNull PulsarClient client, @NotNull Config config) throws PulsarClientException {
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
                String topicsString = config.getString("pulsar.consumer.topics");
                List<String> topics = Arrays.asList(topicsString.split(","));
                log.info("Creating Pulsar consumer for topics: [ {} ]", String.join(", ", topics));
                builder = builder.topics(topics);
            } else {
                String topics = config.getString("pulsar.consumer.topicsPattern");
                log.info("Creating Pulsar consumer for multiple topics using pattern: " + topics);
                Pattern pattern = Pattern.compile(topics);
                builder = builder.topicsPattern(pattern);
            }
        } else {
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

    @NotNull
    protected Map<@NotNull String, @NotNull Producer<byte[]>> createProducers(@NotNull PulsarClient client, @NotNull Config config) throws PulsarClientException {
        int queueSize = config.getInt("pulsar.producer.queueSize");
        boolean blockIfFull = config.getBoolean("pulsar.producer.blockIfFull");
        Map<String, Producer<byte[]>> producers = new HashMap<>();

        if (config.hasPath("pulsar.producer.multipleProducers") && config.getBoolean("pulsar.producer.multipleProducers")) {
            //topic key format: topic1=key1,topic2=key2...
            Map<String, String> topicKeys = Arrays.stream(config.getString("pulsar.producer.topicKeys").split(","))
                    .collect(Collectors.toMap((String s) -> s.split("=")[0], (String s) -> s.split("=")[1]));
            List<String> topics = Arrays.asList(config.getString("pulsar.producer.topics").split(","));
            log.info("Creating Pulsar producers for topics: [ {} ]", String.join(", ", topics));

            for (String topic : topics) {
                Producer<byte[]> producer = createProducer(topic, queueSize, blockIfFull);
                log.info("Pulsar producer created to topic " + topic);

                String topicKey = topicKeys.get(topic);
                producers.put(topicKey, producer);
            }
        } else {
            String topic = config.getString("pulsar.producer.topic");
            Producer<byte[]> producer = createProducer(topic, queueSize, blockIfFull);
            producers.put(producer.getTopic(), producer);
            log.info("Pulsar producer created to topic " + topic);
        }
        return producers;
    }

    private Producer<byte[]> createProducer(final String topic, final int queueSize, final boolean blockIfFull) throws PulsarClientException {
        return client.newProducer()
                .compressionType(CompressionType.LZ4)
                .maxPendingMessages(queueSize)
                .topic(topic)
                .enableBatching(false)
                .blockIfQueueFull(blockIfFull)
                .create();
    }

    @NotNull
    protected PulsarAdmin createAdmin(@NotNull String adminHost, int adminPort) throws PulsarClientException {
        final String adminHttpUrl = String.format("http://%s:%d", adminHost, adminPort);
        log.info("Connecting to Pulsar Admin at " + adminHttpUrl);
        return PulsarAdmin.builder()
                .serviceHttpUrl(adminHttpUrl)
                .build();
    }

    public void launchWithHandler(@NotNull IMessageHandler handler) throws Exception {
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

        if (producers != null){
            for (Producer producer : producers.values()) {
                try {
                    producer.close();
                } catch (PulsarClientException e) {
                    log.error("Failed to close pulsar producer", e);
                }
            }
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
