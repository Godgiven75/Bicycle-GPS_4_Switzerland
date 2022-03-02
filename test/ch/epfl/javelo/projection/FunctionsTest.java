package ch.epfl.javelo.projection;


import ch.epfl.test.TestRandomizer;
import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.Functions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.function.DoubleUnaryOperator;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.*;

public class FunctionsTest {

    @Test
    void constantWorksWithArbitraryValues() {

        var rng  = newRandom();
        for(int i = 0; i < RANDOM_ITERATIONS; ++i) {
            double rand = rng.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE);

            DoubleUnaryOperator randConst = Functions.constant(rand);
            double actual = randConst.applyAsDouble(rng.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE));
            System.out.println(actual);
            assertEquals(rand, actual);
        }
    }
    @Test
    void sampledWorksOnNormalValues() {
        float[] samples = {5.0F, 17F, 23F, 2.34F, 55f, 11f };
        double xMax = 10.0;
        //attention!!!!!!!!!!!
        DoubleUnaryOperator sampling = Functions.sampled(samples, xMax);
        double actual = sampling.applyAsDouble(0);
        double expected = 11.0;
        assertEquals(expected, actual);



    }
}
