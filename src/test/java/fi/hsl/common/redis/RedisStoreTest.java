package fi.hsl.common.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class RedisStoreTest {

    private Jedis jedis;
    private RedisStore redisStore;

    @BeforeEach
    void setUp() {
        var jedisPool = mock(JedisSentinelPool.class);
        jedis = mock(Jedis.class);
        redisStore = new RedisStore(jedisPool);

        given(jedisPool.getResource()).willReturn(jedis);
    }

    @Test
    void shouldSetValue() {
        // given
        given(jedis.set("k1", "v1")).willReturn("OK");

        // when
        var result = redisStore.setValue("k1", "v1");

        // then
        assertThat(result).isEqualTo("OK");
        then(jedis).should().set("k1", "v1");
    }

    @Test
    void shouldSetExpiringValue() {
        // given
        given(jedis.setex("k1", 60, "v1")).willReturn("OK");

        // when
        var result = redisStore.setExpiringValue("k1", "v1", Duration.ofSeconds(60));

        // then
        assertThat(result).isEqualTo("OK");
        then(jedis).should().setex("k1", 60, "v1");
    }

    @Test
    void shouldSetValues() {
        // given
        var values = Map.of("f1", "v1");
        given(jedis.hmset("k1", values)).willReturn("OK");

        // when
        var result = redisStore.setValues("k1", values);

        // then
        assertThat(result).isEqualTo("OK");
        then(jedis).should().hmset("k1", values);
    }

    @Test
    void shouldSetExpiringValues() {
        // given
        var values = Map.of("f1", "v1");
        given(jedis.hmset("k1", values)).willReturn("OK");
        given(jedis.expire("k1", 60)).willReturn(1L);

        // when
        var result = redisStore.setExpiringValues("k1", values, Duration.ofSeconds(60));

        // then
        assertThat(result).isEqualTo("OK");
        then(jedis).should().hmset("k1", values);
        then(jedis).should().expire("k1", 60);
    }

    @Test
    void shouldGetValue() {
        // given
        given(jedis.get("k1")).willReturn("value");

        // when
        var result = redisStore.getValue("k1");

        // then
        assertThat(result).contains("value");
    }

    @Test
    void shouldGetEmptyValue() {
        // given
        given(jedis.get("k1")).willReturn(null);

        // when
        var result = redisStore.getValue("k1");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldGetValues() {
        // given
        var data = Map.of("f1", "v1");
        given(jedis.hgetAll("k1")).willReturn(data);

        // when
        var result = redisStore.getValues("k1");

        // then
        assertThat(result).contains(data);
    }

    @Test
    void shouldGetEmptyValues() {
        // given
        given(jedis.hgetAll("k1")).willReturn(Collections.emptyMap());

        // when
        var result = redisStore.getValues("k1");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldGetKeysUsingScan() {
        // given
        var firstScan = new ScanResult<>("1", List.of("k1", "k2"));
        var secondScan = new ScanResult<>("0", List.of("k3"));
        given(jedis.scan(anyString(), any(ScanParams.class))).willReturn(firstScan).willReturn(secondScan);

        // when
        var keys = redisStore.getKeys("prefix", 10);

        // then
        assertThat(keys).containsExactlyInAnyOrder("k1", "k2", "k3");
    }

    @Test
    void shouldGetValuesByKeys() {
        // given
        var transaction = mock(Transaction.class);
        var response1 = mock(Response.class);
        var response2 = mock(Response.class);

        given(jedis.multi()).willReturn(transaction);
        given(transaction.hgetAll("k1")).willReturn(response1);
        given(transaction.hgetAll("k2")).willReturn(response2);
        given(transaction.exec()).willReturn(List.of());
        given(response1.get()).willReturn(Map.of("f1", "v1"));
        given(response2.get()).willReturn(Collections.emptyMap());

        // when
        var result = redisStore.getValuesByKeys(List.of("k1", "k2"));

        // then
        assertThat(result.get("k1")).contains(Map.of("f1", "v1"));
        assertThat(result.get("k2")).isEmpty();
    }

    @Test
    void shouldGetValueByKeys() {
        // given
        var transaction = mock(Transaction.class);
        var response1 = mock(Response.class);
        var response2 = mock(Response.class);

        given(jedis.multi()).willReturn(transaction);
        given(transaction.get("k1")).willReturn(response1);
        given(transaction.get("k2")).willReturn(response2);
        given(transaction.exec()).willReturn(List.of());
        given(response1.get()).willReturn("v1");
        given(response2.get()).willReturn(null);

        // when
        var result = redisStore.getValueByKeys(List.of("k1", "k2"));

        // then
        assertThat(result.get("k1")).contains("v1");
        assertThat(result.get("k2")).isEmpty();
    }

    @Test
    void shouldCheckResponseString() {
        // when / then
        assertThat(redisStore.checkResponse("OK")).isTrue();
        assertThat(redisStore.checkResponse("ok")).isTrue();
        assertThat(redisStore.checkResponse("FAIL")).isFalse();
        assertThat(redisStore.checkResponse((String) null)).isFalse();
    }

    @Test
    void shouldCheckResponseLong() {
        // when / then
        assertThat(redisStore.checkResponse(1L)).isTrue();
        assertThat(redisStore.checkResponse(0L)).isFalse();
        assertThat(redisStore.checkResponse((Long) null)).isFalse();
    }
}
