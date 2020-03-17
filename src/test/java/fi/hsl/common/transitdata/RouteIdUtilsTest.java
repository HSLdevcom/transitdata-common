package fi.hsl.common.transitdata;

import org.junit.Test;

import static org.junit.Assert.*;

public class RouteIdUtilsTest {
    @Test
    public void testTrainRouteIdMatch() {
        assertTrue(RouteIdUtils.isTrainRoute("3001K"));
        assertTrue(RouteIdUtils.isTrainRoute("3002U"));
        assertTrue(RouteIdUtils.isTrainRoute("3001"));
        assertTrue(RouteIdUtils.isTrainRoute("3002"));
        assertTrue(RouteIdUtils.isTrainRoute("3002 ABC"));
    }

    @Test
    public void testNonTrainRouteIdsDontMatch() {
        assertFalse(RouteIdUtils.isTrainRoute("3000K"));
        assertFalse(RouteIdUtils.isTrainRoute("3003U"));
        assertFalse(RouteIdUtils.isTrainRoute("3000"));
        assertFalse(RouteIdUtils.isTrainRoute("3003"));
        assertFalse(RouteIdUtils.isTrainRoute("757"));
        assertFalse(RouteIdUtils.isTrainRoute("30002"));
    }

    @Test
    public void testMetroRouteIdMatch() {
        assertTrue(RouteIdUtils.isMetroRoute("31M1"));
        assertTrue(RouteIdUtils.isMetroRoute("31M2"));
        assertTrue(RouteIdUtils.isMetroRoute("31M1B"));
        assertTrue(RouteIdUtils.isMetroRoute("31M2B"));
    }

    @Test
    public void testNonMetroRouteIdsDontMatch() {
        assertFalse(RouteIdUtils.isMetroRoute("2550"));
        assertFalse(RouteIdUtils.isMetroRoute("3001K"));
        assertFalse(RouteIdUtils.isMetroRoute("31M3"));
        assertFalse(RouteIdUtils.isMetroRoute("21M1"));
    }

    @Test
    public void testFerryRouteIdMatch() {
        assertTrue(RouteIdUtils.isFerryRoute("1019"));
        assertTrue(RouteIdUtils.isFerryRoute("1019E"));
    }

    @Test
    public void testNonFerryRouteIdsDontMatch() {
        assertFalse(RouteIdUtils.isFerryRoute("2550"));
        assertFalse(RouteIdUtils.isFerryRoute("3001K"));
        assertFalse(RouteIdUtils.isFerryRoute("31M1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNormalizingInvalidIdThrowsException() {
        RouteIdUtils.normalizeRouteId("100");
    }

    @Test
    public void testNormalizingNormalIdReturnsItself() {
        assertEquals("2550", RouteIdUtils.normalizeRouteId( "2550"));
        assertEquals("3001Z", RouteIdUtils.normalizeRouteId("3001Z"));
    }

    @Test
    public void testNormalizeTramIdVariant() {
        assertEquals("1008", RouteIdUtils.normalizeRouteId("1008 3"));
    }

    @Test
    public void testNormalizeTrainIdVariant() {
        assertEquals("3001Z", RouteIdUtils.normalizeRouteId("3001Z3"));
    }

    @Test
    public void testNormalizeBusIdVariant() {
        assertEquals("9787A", RouteIdUtils.normalizeRouteId("9787A3"));
    }

    @Test
    public void testNormalizingRouteIdWithTwoCharactersReturnsItself() {
        assertEquals("2348BK", RouteIdUtils.normalizeRouteId("2348BK"));
    }
}
