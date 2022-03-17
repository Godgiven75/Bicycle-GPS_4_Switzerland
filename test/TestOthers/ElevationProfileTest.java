package TestOthers;

import ch.epfl.javelo.routing.ElevationProfile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ElevationProfileTest {


    @Test
    void ConstructorThrowsException(){
        assertThrows(IllegalArgumentException.class, () -> new ElevationProfile(-8, new float[]{2,5,8,10}));
        assertThrows(IllegalArgumentException.class, () -> new ElevationProfile(-203182, new float[]{2,5,8,10}));
        assertThrows(IllegalArgumentException.class, () -> new ElevationProfile(0, new float[]{2,5,8,10}));
        assertThrows(IllegalArgumentException.class, () -> new ElevationProfile(5, new float[]{}));
        assertThrows(IllegalArgumentException.class, () -> new ElevationProfile(5, new float[]{1}));
    }

    // Fonctionne à approx. près
    @Test
    void FirstProfileWorks(){
        ElevationProfile firstProfile = new ElevationProfile(10, new float[]{384.75f, 384.6875f, 384.5625f, 384.5f, 384.4375f,
                384.375f, 384.3125f, 384.25f, 384.125f, 384.0625f});
        assertEquals(10, firstProfile.length());
        assertEquals(384.0625f, firstProfile.minElevation());
        assertEquals(384.75f, firstProfile.maxElevation());
        assertEquals(384.75f, firstProfile.elevationAt(0));
        assertEquals(384.0625f, firstProfile.elevationAt(10));
        assertEquals(384.6875f, firstProfile.elevationAt(1));
        assertEquals(0, firstProfile.totalAscent());
        assertEquals(0.6875, firstProfile.totalDescent());
    }

    @Test
    void ThrowElevationProfile()
    {
        assertThrows(IllegalArgumentException.class, () -> new ElevationProfile(-1, new float[]{1,2,3}));
        assertThrows(IllegalArgumentException.class, () -> new ElevationProfile(0, new float[]{1,2,3}));
        assertThrows(IllegalArgumentException.class, () -> new ElevationProfile(10, new float[]{1}));
        assertThrows(IllegalArgumentException.class, () -> new ElevationProfile(10, new float[]{}));
    }

    @Test
    void TestWithCalculationsActualFunction()
    {
        float[] values = new float[]{5,7,0,2,10};
        double length = 20;
        ElevationProfile ep = new ElevationProfile(length, values);
        assertEquals(20, ep.length());
        assertEquals(0, ep.minElevation());
        assertEquals(10, ep.maxElevation());
        assertEquals(12, ep.totalAscent());
        assertEquals(7, ep.totalDescent());
        assertEquals(5, ep.elevationAt(0));
        assertEquals(6, ep.elevationAt(2.5));
        assertEquals(7, ep.elevationAt(5));
        assertEquals(1, ep.elevationAt(12.5));
        assertEquals(10, ep.elevationAt(21));
        assertEquals(5, ep.elevationAt(-1));
        assertEquals(0, ep.elevationAt(10));
    }

    @Test
    void TestWithCalculationsCst()
    {
        float[] values = new float[]{5,5};
        double length = 20;
        ElevationProfile ep = new ElevationProfile(length, values);
        assertEquals(20, ep.length());
        assertEquals(5, ep.minElevation());
        assertEquals(5, ep.maxElevation());
        assertEquals(0, ep.totalAscent());
        assertEquals(0, ep.totalDescent());
        assertEquals(5, ep.elevationAt(2.5));
        assertEquals(5, ep.elevationAt(5));
        assertEquals(5, ep.elevationAt(12.5));
        assertEquals(5, ep.elevationAt(21));
        assertEquals(5, ep.elevationAt(-1));
        assertEquals(5, ep.elevationAt(10));
    }





}
