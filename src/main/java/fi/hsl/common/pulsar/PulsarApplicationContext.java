package fi.hsl.common.pulsar;

import com.typesafe.config.Config;
import fi.hsl.common.health.HealthServer;
import fi.hsl.common.redis.RedisStore;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class PulsarApplicationContext {

    private Config config;

    private Consumer<byte[]> consumer;
    private Map<String, Producer<byte[]>> producers;
    private PulsarClient client;
    private PulsarAdmin admin;
    private RedisStore redisStore;
    private HealthServer healthServer;

    @Nullable
    public Config getConfig() {
        return config;
    }

    protected void setConfig(@NotNull Config config) {
        this.config = config;
    }

    @Nullable
    public Consumer<byte[]> getConsumer() {
        return consumer;
    }

    protected void setConsumer(@Nullable Consumer<byte[]> consumer) {
        this.consumer = consumer;
    }

    /**
     * @deprecated Use {@link #getSingleProducer()} instead
     */
    @Deprecated
    @Nullable
    public Producer<byte[]> getProducer() {
        return getSingleProducer();
    }

    @Nullable
    public Producer<byte[]> getSingleProducer() {
        if (getProducers().size() != 1) {
            throw new IllegalStateException("Cannot get single producer when there are multiple producers (" + String.join(", ", getProducers().keySet()) + ")");
        }
        return getProducers().values().stream().findFirst().get();
    }

    /**
     * @deprecated Use {@link #setSingleProducer(Producer)} instead
     */
    @Deprecated
    protected void setProducer(@Nullable Producer<byte[]> producer) {
        setSingleProducer(producer);
    }

    protected void setSingleProducer(@Nullable Producer<byte[]> producer) {
        this.getProducers().clear();
        getProducers().put(producer.getTopic(), producer);
    }

    @Nullable
    public PulsarClient getClient() {
        return client;
    }

    protected void setClient(@NotNull PulsarClient client) {
        this.client = client;
    }

    @Nullable
    public RedisStore getRedisStore() {
        return redisStore;
    }

    public void setRedisStore(RedisStore redisStore) {
        this.redisStore = redisStore;
    }

    @Nullable
    public PulsarAdmin getAdmin() {
        return admin;
    }

    protected void setAdmin(@Nullable PulsarAdmin admin) {
        this.admin = admin;
    }

    @Nullable
    public HealthServer getHealthServer() {
        return healthServer;
    }

    protected void setHealthServer(@Nullable HealthServer healthServer) {
        this.healthServer = healthServer;
    }

    /**
     * Gets the map of producers for this application. Last part of the topic is used as a key in the map, for example the key for topic <pre>transitdata-dev/gtfs-rt/tripupdate</pre> would be <pre>tripupdate</pre>.
     */
    @Nullable
    public Map<@NotNull String, @NotNull Producer<byte[]>> getProducers() {
        return producers;
    }

    public void setProducers(Map<@NotNull String, @NotNull Producer<byte[]>> producers) {
        this.producers = producers;
    }
}
