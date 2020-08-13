package fi.hsl.common.pulsar;

import com.typesafe.config.Config;
import fi.hsl.common.health.HealthServer;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import redis.clients.jedis.Jedis;

import java.util.Map;

public class PulsarApplicationContext {

    private Config config;

    private Consumer<byte[]> consumer;
    private Map<String, Producer<byte[]>> producers;
    private PulsarClient client;
    private PulsarAdmin admin;
    private Jedis jedis;
    private HealthServer healthServer;

    public Config getConfig() {
        return config;
    }

    protected void setConfig(Config config) {
        this.config = config;
    }

    public Consumer<byte[]> getConsumer() {
        return consumer;
    }

    protected void setConsumer(Consumer<byte[]> consumer) {
        this.consumer = consumer;
    }

    @Deprecated
    //Use getSingleProducer instead
    public Producer<byte[]> getProducer() {
        return getSingleProducer();
    }

    public Producer<byte[]> getSingleProducer(){
        return getProducers().values().stream().findFirst().get();
    }
    @Deprecated
    //Use setSingleProducer instead
    protected void setProducer(Producer<byte[]> producer) {
        setSingleProducer(producer);
    }

    protected void setSingleProducer(Producer<byte[]> producer) {
        this.getProducers().clear();
        getProducers().put(producer.getTopic(), producer);
    }

    public PulsarClient getClient() {
        return client;
    }

    protected void setClient(PulsarClient client) {
        this.client = client;
    }

    public Jedis getJedis() {
        return jedis;
    }

    protected void setJedis(Jedis jedis) {
        this.jedis = jedis;
    }

    public PulsarAdmin getAdmin() {
        return admin;
    }

    protected void setAdmin(PulsarAdmin admin) {
        this.admin = admin;
    }

    public HealthServer getHealthServer() {
        return healthServer;
    }

    protected void setHealthServer(HealthServer healthServer) {
        this.healthServer = healthServer;
    }

    public Map<String, Producer<byte[]>> getProducers() {
        return producers;
    }

    public void setProducers(Map<String, Producer<byte[]>> producers) {
        this.producers = producers;
    }
}
