package ch.epfl.javelo.projection;



import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.*;

public class FunctionsTest {

    final double DELTA  = 1e-6;

    @Test
    void constantWorksWithArbitraryValues() {

        var rng  = newRandom();
        for(int i = 0; i < RANDOM_ITERATIONS; ++i) {
            double rand = rng.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE);

            DoubleUnaryOperator randConst = ch.epfl.javelo.projection.Functions.constant(rand);
            double actual = randConst.applyAsDouble(rng.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE));
            System.out.println(actual);
            assertEquals(rand, actual);
        }
    }
    @Test
    void sampledWorksOnNormalValues() {
        float[] samples = {5.0F, 17F, 23F, 2.34F, 55f, 11f };
        double xMax = 10.0;
        DoubleUnaryOperator sampling = ch.epfl.javelo.projection.Functions.sampled(samples, xMax);
        double actual = sampling.applyAsDouble(1 );
        double expected = 11.0f;

        assertEquals(expected, actual);

        actual = sampling.applyAsDouble(3.27);
        expected = Math.fma(3, 3.27 , 11); //attention

        assertEquals(expected, actual);

         actual = sampling.applyAsDouble(9.87);
         expected = (-22.0 * 9.87) + 231;

        assertEquals(expected, actual, 1e-6 );

    }

    @Test
    void sampledWorksOnLargeArray() {
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
        }

        DoubleUnaryOperator sampling = ch.epfl.javelo.projection.Functions.sampled(arr ,xMax );

        double x = 100.5;
        double actual = sampling.applyAsDouble(x);
        int x0 = (int) Math.floor(x);
        int x1 = (int) Math.ceil(x);
        double y0 = arr[x0];
        double y1 = arr[x1];
        double slope = (y1 - y0) / (x1 - x0);
        double yIntercept = Math.fma(-slope, x1, y1);
        double expected = Math.fma(slope, x, yIntercept);
        assertEquals(expected, actual, DELTA);
        }


    @Test
    void sampledWorksOnZero() {
        float[] samples = {5.0F, 17F, 23F, 2.34F, 55f, 11f };
        double xMax = 10.0;
        DoubleUnaryOperator sampling = ch.epfl.javelo.projection.Functions.sampled(samples, xMax);
        double actual = sampling.applyAsDouble(0 );
        double expected = 5.0f;

        assertEquals(expected, actual);
    }
    @Test
    void sampledWorksOnXMax() {
        float[] samples = {5.0F, 17F, 23F, 2.34F, 55f, 11f };
        double xMax = 10.0;
        DoubleUnaryOperator sampling = ch.epfl.javelo.projection.Functions.sampled(samples, xMax);
        double actual = sampling.applyAsDouble(10 );
        double expected = 11.0f;

        assertEquals(expected, actual);
    }

    @Test
    void sampledThrowsOnIllegalLength() {
        float[] samples = {5.0F};
        double xMax = 10.0;

        assertThrows(IllegalArgumentException.class, () -> {
            DoubleUnaryOperator sampling = ch.epfl.javelo.projection.Functions.sampled(samples, xMax);
        });

    }

    @Test
    void sampledThrowsOnIllegalXMax() {
        float[] samples = {5.0F, 17F, 23F, 2.34F, 55f, 11f };
        double xMax = 0;
        double finalXMax = xMax;
        assertThrows(IllegalArgumentException.class, () -> {
            DoubleUnaryOperator sampling = ch.epfl.javelo.projection.Functions.sampled(samples, finalXMax);

        });

        xMax = -71f;
        double finalXMax1 = xMax;
        assertThrows(IllegalArgumentException.class, () -> {
            DoubleUnaryOperator sampling = ch.epfl.javelo.projection.Functions.sampled(samples, finalXMax1);

        });
    }


}
