package fi.hsl.common.pulsar;

import com.typesafe.config.Config;
import fi.hsl.common.health.HealthServer;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.Jedis;

public class PulsarApplicationContext {

    private Config config;

    private Consumer<byte[]> consumer;
    private Producer<byte[]> producer;
    private PulsarClient client;
    private PulsarAdmin admin;
    private Jedis jedis;
    private HealthServer healthServer;

    public Config getConfig() {
        return config;
    }

    protected void setConfig(@NotNull Config config) {
        this.config = config;
    }

    public Consumer<byte[]> getConsumer() {
        return consumer;
    }

    protected void setConsumer(@Nullable Consumer<byte[]> consumer) {
        this.consumer = consumer;
    }

    public Producer<byte[]> getProducer() {
        return producer;
    }

    protected void setProducer(@Nullable Producer<byte[]> producer) {
        this.producer = producer;
    }

    public PulsarClient getClient() {
        return client;
    }

    protected void setClient(@NotNull PulsarClient client) {
        this.client = client;
    }

    public Jedis getJedis() {
        return jedis;
    }

    protected void setJedis(@Nullable Jedis jedis) {
        this.jedis = jedis;
    }

    public PulsarAdmin getAdmin() {
        return admin;
    }

    protected void setAdmin(@Nullable PulsarAdmin admin) {
        this.admin = admin;
    }

    public HealthServer getHealthServer() {
        return healthServer;
    }

    protected void setHealthServer(@Nullable HealthServer healthServer) {
        this.healthServer = healthServer;
    }
}
