package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ElevationProfileTest {

    @Test
    public void checkElevationProfilePreconditions() {
        assertThrows(IllegalArgumentException.class, () -> {new ElevationProfile(0, new float[]{0, 1, 2});});
        assertThrows(IllegalArgumentException.class, () -> new ElevationProfile(3, new float[]{0}));
    }

    @Test
    public void lengthWorks() {
        ElevationProfile e = new ElevationProfile(4.3, new float[]{0, 1, 2, 5});
        double expected = 4.3;
        assertEquals(expected, e.length());
    }

    @Test
    public void minElevationReturnsMinAltitudeInMeters() {
        ElevationProfile e = new ElevationProfile(4.3, new float[]{0.1f, 98234, 4.6f, 3});
        double expected = 0.1;
        assertEquals(expected, e.minElevation(), 1e-3);
    }

    @Test
    public void maxElevationReturnsMaxAltitudeInMeters() {
        ElevationProfile e = new ElevationProfile(4.3, new float[]{0.1f, 98234, 4.6f, 3});
        double expected = 98234;
        assertEquals(expected, e.maxElevation(), 1e-3);
    }

    @Test
    public void totalAscentWorks() {
        ElevationProfile e = new ElevationProfile(4.3, new float[]{-0.1f, 98234, 4.4f, -3});
        double expected = 98234+0.1;
        assertEquals(expected, e.totalAscent(), 1e-2);
        ElevationProfile e2 = new ElevationProfile(4.3, new float[]{5, -2, 4, -3});
        double expected2 = 6;
        assertEquals(expected2, e2.totalAscent(), 1e-2);
    }

    @Test
    public void totalDescentWorks() {
        ElevationProfile e = new ElevationProfile(4.3, new float[]{-0.1f, 98234, 4.4f, -3});
        double expected = -(4.4f-98234 + -3-4.4f);
        assertEquals(expected, e.totalDescent(), 1e-2);
    }




}
