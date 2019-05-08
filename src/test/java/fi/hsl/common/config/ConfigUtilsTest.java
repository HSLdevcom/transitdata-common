package fi.hsl.common.config;

import org.junit.Test;

import java.net.URL;
import java.util.Optional;

import static org.junit.Assert.*;

public class ConfigUtilsTest {
    @Test
    public void getEnvOrThrow1() {
        assertEquals("VAL1", ConfigUtils.getEnvOrThrow("ENV1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getEnvOrThrow2() {
        ConfigUtils.getEnvOrThrow("ENV0");
    }

    @Test
    public void getEnv() {
        Optional<String> maybeEnv = ConfigUtils.getEnv("ENV1");
        assertTrue(maybeEnv.isPresent());
        assertEquals("VAL1", maybeEnv.get());
        maybeEnv = ConfigUtils.getEnv("ENV0");
        assertFalse(maybeEnv.isPresent());
    }

    @Test
    public void safeParseInt() {
        Optional<Integer> maybeInt = ConfigUtils.safeParseInt("123");
        assertTrue(maybeInt.isPresent());
        assertEquals(Integer.valueOf(123), maybeInt.get());
        maybeInt = ConfigUtils.safeParseInt("abc");
        assertFalse(maybeInt.isPresent());
    }

    @Test
    public void getIntEnv() {
        Optional<Integer> maybeInt = ConfigUtils.getIntEnv("ENV2");
        assertTrue(maybeInt.isPresent());
        assertEquals(Integer.valueOf(123), maybeInt.get());
        maybeInt = ConfigUtils.getIntEnv("ENV0");
        assertFalse(maybeInt.isPresent());
        maybeInt = ConfigUtils.getIntEnv("ENV1");
        assertFalse(maybeInt.isPresent());
    }

    @Test
    public void getConnectionStringFromFileOrThrow() throws Exception {
        String secret = ConfigUtils.getConnectionStringFromFileOrThrow();
        assertEquals("secret", secret);
    }

    @Test
    public void getUsernameFromFileOrThrow() throws Exception {
        String secret = ConfigUtils.getUsernameFromFileOrThrow();
        assertEquals("username", secret);
    }

    @Test
    public void getPasswordFromFileOrThrow() throws Exception {
        String secret = ConfigUtils.getPasswordFromFileOrThrow();
        assertEquals("password", secret);
    }

    @Test
    public void getSecretFromFileOrThrow() throws Exception {
        String secret = ConfigUtils.getSecretFromFileOrThrow("FILEPATH_CONNECTION_STRING", Optional.empty());
        assertEquals("secret", secret);
        secret = ConfigUtils.getSecretFromFileOrThrow("ENV0", Optional.of("./src/test/resources/secret.testsecret"));
        assertEquals("secret", secret);
    }

    @Test(expected = Exception.class)
    public void getSecretFromFileOrThrow1() throws Exception {
        ConfigUtils.getSecretFromFileOrThrow(null, Optional.empty());
    }

    @Test(expected = Exception.class)
    public void getSecretFromFileOrThrow2() throws Exception {
        ConfigUtils.getSecretFromFileOrThrow("", Optional.empty());
    }

    @Test(expected = Exception.class)
    public void getSecretFromFileOrThrow3() throws Exception {
        ConfigUtils.getSecretFromFileOrThrow("ENV1", Optional.empty());
    }

    @Test(expected = Exception.class)
    public void getSecretFromFileOrThrow4() throws Exception {
        ConfigUtils.getSecretFromFileOrThrow("ENV0", Optional.empty());
    }

    @Test(expected = Exception.class)
    public void getSecretFromFileOrThrow5() throws Exception {
        ConfigUtils.getSecretFromFileOrThrow("ENV0", Optional.of(""));
    }

    @Test(expected = Exception.class)
    public void getSecretFromFileOrThrow6() throws Exception {
        ConfigUtils.getSecretFromFileOrThrow(null, Optional.of("./src/test/resources/secret.testsecret"));
    }

    @Test(expected = Exception.class)
    public void getSecretFromFileOrThrow7() throws Exception {
        ConfigUtils.getSecretFromFileOrThrow("ENV1", Optional.of("./src/test/resources/secret.testsecret"));
    }
}
