package fi.hsl.common.redis;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Map;

import static com.typesafe.config.ConfigFactory.parseMap;
import static fi.hsl.common.redis.RedisClusterProperties.redisClusterProperties;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RedisClusterPropertiesTest {

    @Test
    void shouldLoadAllPropertiesWhenPresent() {
        // given
        var config = parseMap(Map.of("redisCluster.masterName", "mymaster", "redisCluster.sentinels",
                "host1:26379,host2:26379", "redisCluster.healthCheck", "true", "redisCluster.idleConnectionTimeout",
                "10s", "redisCluster.minIdleConnections", "5", "redisCluster.maxConnections", "20",
                "redisCluster.maxWait", "2s", "redisCluster.connectionTimeout", "15s", "redisCluster.socketTimeout",
                "30s"));

        // when
        var props = redisClusterProperties(config);

        // then
        assertThat(props.masterName).isEqualTo("mymaster");
        assertThat(props.sentinels).containsExactlyInAnyOrder("host1:26379", "host2:26379");
        assertThat(props.healthCheck).isTrue();
        assertThat(props.idleConnectionTimeout).isEqualTo(Duration.ofSeconds(10));
        assertThat(props.minIdleConnections).isEqualTo(5);
        assertThat(props.maxConnections).isEqualTo(20);
        assertThat(props.maxWait).isEqualTo(Duration.ofSeconds(2));
        assertThat(props.connectionTimeout).isEqualTo(Duration.ofSeconds(15));
        assertThat(props.socketTimeout).isEqualTo(Duration.ofSeconds(30));
    }

    @Test
    void shouldApplyDefaultsWhenOptionalPropertiesMissing() {
        // given
        var config = parseMap(Map.of("redisCluster.masterName", "mymaster", "redisCluster.sentinels", "host1:26379"));

        // when
        var props = redisClusterProperties(config);

        // then
        assertThat(props.masterName).isEqualTo("mymaster");
        assertThat(props.sentinels).containsExactly("host1:26379");
        assertThat(props.healthCheck).isFalse();
        assertThat(props.idleConnectionTimeout).isEqualTo(Duration.ofSeconds(5));
        assertThat(props.minIdleConnections).isEqualTo(12);
        assertThat(props.maxConnections).isEqualTo(16);
        assertThat(props.maxWait).isEqualTo(Duration.ofMillis(500));
        assertThat(props.connectionTimeout).isEqualTo(Duration.ofSeconds(500));
        assertThat(props.socketTimeout).isEqualTo(Duration.ofSeconds(500));
    }

    @Test
    void shouldThrowWhenMasterNameMissing() {
        // given
        var config = parseMap(Map.of("redisCluster.sentinels", "host1:26379"));

        // when / then
        assertThatThrownBy(() -> redisClusterProperties(config)).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("redisCluster.masterName is required");
    }

    @Test
    void shouldThrowWhenSentinelsMissing() {
        // given
        var config = parseMap(Map.of("redisCluster.masterName", "mymaster"));

        // when / then
        assertThatThrownBy(() -> redisClusterProperties(config)).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("redisCluster.sentinels is required");
    }

    @Test
    void shouldThrowWhenSentinelsEmpty() {
        // given
        var config = parseMap(Map.of("redisCluster.masterName", "mymaster", "redisCluster.sentinels", ""));

        // when / then
        assertThatThrownBy(() -> redisClusterProperties(config)).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("sentinels must not be empty");
    }
}
