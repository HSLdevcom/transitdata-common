package fi.hsl.common.transitdata;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static junit.framework.TestCase.assertTrue;

public class MockDataUtilsTest {
    static final Logger logger = LoggerFactory.getLogger(MockDataUtilsTest.class);

    @Test
    public void testRouteNameGenerator() {
        String route = MockDataUtils.generateValidRouteName();
        Pattern p = Pattern.compile(MockDataUtils.JORE_ROUTE_NAME_REGEX);
        Matcher matcher = p.matcher(route);
        assertTrue(matcher.matches());
        logger.info(route);
    }
}
