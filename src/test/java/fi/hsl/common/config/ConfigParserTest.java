package fi.hsl.common.config;

import com.typesafe.config.Config;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ConfigParserTest {
    @Test
    public void readFromResources() {
        Config config = ConfigParser.createConfig("test.conf");
        assertNotNull(config);

        assertEquals(config.getString("test.foo"), "bar");
        assertTrue(config.getBoolean("test.baz"));

    }
}
