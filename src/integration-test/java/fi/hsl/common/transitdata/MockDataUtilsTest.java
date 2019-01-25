package fi.hsl.common.transitdata;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class MockDataUtilsTest {
    static final Logger logger = LoggerFactory.getLogger(MockDataUtilsTest.class);

    @Test
    public void testRouteNameGenerator() {
        for (int n = 0; n < 10000; n++) {
            String route = MockDataUtils.generateValidRouteName();
            //logger.info(route);
            Pattern p = Pattern.compile(MockDataUtils.JORE_ROUTE_NAME_REGEX);
            Matcher matcher = p.matcher(route);
            assertTrue("testing route name " + route, matcher.matches());
        }
    }

    @Test
    public void testJoreIdGenerator() {
        long id = MockDataUtils.generateValidJoreId();
        assertTrue(id > 999999999999999L);
        assertTrue(id < 10000000000000000L);
        logger.info("id " + id);
    }

    @Test
    public void testStopSeqListGenerator() {
        final int length = 5;
        List<Integer> seq = MockDataUtils.generateStopSequenceList(length);
        assertEquals(length, seq.size());

        Integer[] expected = {0, 1, 2, 3, 4};
        assertTrue(Arrays.equals(expected, seq.toArray(new Integer[0])));
    }
}
