package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
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

    @Test
    public void elevationAtWorks() {
        final double DELTA  = 1e-6;
        //Vérifications à faire manuellement pour ne pas utiliser la même méthode de calcul que dans la méthode...
        var rng = newRandom();
        List<Float> l = new ArrayList<>();
        for(int i = 0; i < RANDOM_ITERATIONS; ++i) {
            l.add((float)rng.nextInt(0, 1000));
        }


        float xMax = 1000;

        final float[] arr = new float[l.size()];
        int index = 0;
        for (final Float value: l) {
            arr[index++] = value;
            //System.out.println(index + " " + value);
        }



        DoubleUnaryOperator sampling = ch.epfl.javelo.Functions.sampled(arr ,xMax );

        double x = 100.5;
        double actual = sampling.applyAsDouble(x);

        int x0 = (int) Math.floor(x);
        int x1 = x0 + 1;

        double y0 = arr[x0];
        double y1 = arr[x1];

        System.out.println(y0);
        System.out.println(y1);

        double step = xMax /  (arr.length -1) ;
        System.out.println(xMax);
        System.out.println(arr.length - 1);
        System.out.println(step);
        double slope = (y1 - y0) / (step);
        System.out.println("slope " +slope);
        double yIntercept = Math.fma(-slope, x1 , y1);
        System.out.println(yIntercept);

        double expected = Math.fma(slope, x , yIntercept);


        assertEquals(expected, actual, DELTA);

    }



}
