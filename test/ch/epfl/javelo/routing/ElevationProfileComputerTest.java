package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.DoubleUnaryOperator;

import static ch.epfl.test.TestRandomizer.newRandom;
import static java.lang.Float.NaN;
import static java.lang.Float.isNaN;
import static org.junit.jupiter.api.Assertions.*;

public class ElevationProfileComputerTest {

    @Test
    public void elevationProfileComputerArrayManipulationsWorkOnPresetValues() { // Juste un test de l'agorithme que l'on utilise pour manipuler les tableaux
        float[] arr = new float[]{NaN, 1f, 4f, 6f, NaN, NaN, NaN, 5f};
        float[] expected = new float[]{1f, 1.0f, 4.0f, 6.0f, 5.75f, 5.5f, 5.25f, 5.0f};
        float[] actual = samples(arr);
        assertArrayEquals(expected, actual);
    }
    private static float[] samples(float[] samples) {
        boolean isOnlyNan = true;
        int firstValidSampleIndex = 0;
        for (int i = 0; i < samples.length; ++i) {
            if (!isNaN(samples[i])) {
                firstValidSampleIndex = i;
                Arrays.fill(samples, 0, firstValidSampleIndex, samples[i]);
                isOnlyNan = false;
                break;
            }
        }
        if (isOnlyNan) {
            Arrays.fill(samples, 0, samples.length - 1, 0f);
        }

        // Recherche du dernier échantillon valide du tableau
        for (int i = samples.length - 1; i >= 0;  --i) {
            if (!isNaN(samples[i])) {
                Arrays.fill(samples, samples.length - 1, i, samples[i]);
                break;
            }
        }

        // Parcours du tableau pour trouver les trous intermédiaires et les remplir par interpolation
        for (int i =firstValidSampleIndex; i < samples.length; ++i) {
            if (isNaN(samples[i])) {
                double y0 = samples[i - 1];
                int j = i + 1;
                while (isNaN(samples[j])) {
                    ++j;
                }
                double y1 = samples[j];
                for(int k = i ; k < j; ++k) {
                    double x = (double) (k - i + 1) / (j - i + 1);
                    samples[k] = (float) Math2.interpolate(y0, y1 , x );
                }
                i = j;
            }
        }
        return samples;
    }

    @Test
    public void elevationProfileComputerWorksWithSimpleValues() {

        DoubleUnaryOperator profile0 = Functions.constant(Double.NaN);
        PointCh fromPoint0 = randomPointCh();
        PointCh toPoint0 = randomPointCh();
        Edge e0 = new Edge(0, 1, toPoint0, fromPoint0, 100, profile0);

        float[] samples1 = new float[]{100, 200};
        DoubleUnaryOperator profile1 = Functions.sampled(samples1, 100);
        PointCh fromPoint1 = toPoint0;
        PointCh toPoint1 = randomPointCh();
        Edge e1 = new Edge(1, 2, fromPoint1, toPoint1, 100, profile1);


        List<Edge> l = new ArrayList();
        l.add(e0);
        l.add(e1);

        SingleRoute sr = new SingleRoute(l);

        ElevationProfile actual = ElevationProfileComputer.elevationProfile(sr, 100);
        float[] samples = new float[]{100, 100, 200};
        ElevationProfile expected = new ElevationProfile(200, samples);
        assertArrayEquals(expected.elevationSamples, actual.elevationSamples); //passer en package private pour les tests
        assertEquals(expected.length, actual.length);

    }
    @Test
    public void elevationProfileComputerThrowsOnInvalidMaxStepLength() {
        assertThrows(IllegalArgumentException.class, ()-> {
            DoubleUnaryOperator profile0 = Functions.constant(Double.NaN);
            PointCh fromPoint0 = randomPointCh();
            PointCh toPoint0 = randomPointCh();
            Edge e0 = new Edge(0, 1, toPoint0, fromPoint0, 100, profile0);

            float[] samples1 = new float[]{384.75f, 384.6875f, 400f, 384.5f};
            DoubleUnaryOperator profile1 = Functions.sampled(samples1, 100);
            PointCh fromPoint1 = toPoint0;
            PointCh toPoint1 = randomPointCh();
            Edge e1 = new Edge(1, 2, fromPoint1, toPoint1, 100, profile1);

            List<Edge> l = new ArrayList();
            l.add(e0);
            l.add(e1);

            SingleRoute sr = new SingleRoute(l);

            ElevationProfile profile = ElevationProfileComputer.elevationProfile(sr, 0);
        });
    }

    @Test
    public void elevationProfileComputerWorksOnComplicatedValues() {
        DoubleUnaryOperator profile0 = Functions.constant(Double.NaN);
        PointCh fromPoint0 = randomPointCh();
        PointCh toPoint0 = randomPointCh();
        Edge e0 = new Edge(0, 1, toPoint0, fromPoint0, 5800, profile0);

        float[] samples1 = new float[]{384.75f, 384.6875f, 400f, 384.5f};
        DoubleUnaryOperator profile1 = Functions.sampled(samples1, 2300);
        PointCh fromPoint1 = toPoint0;
        PointCh toPoint1 = randomPointCh();
        Edge e1 = new Edge(1, 2, fromPoint1, toPoint1, 2300, profile1);

        DoubleUnaryOperator profile2 = Functions.constant(Double.NaN);
        PointCh fromPoint2 = toPoint1;
        PointCh toPoint2 = randomPointCh();
        Edge e2 = new Edge( 2, 3, fromPoint2, toPoint2, 1100, profile2);

        DoubleUnaryOperator profile3 = Functions.constant(Double.NaN);
        PointCh fromPoint3 = toPoint2;
        PointCh toPoint3 = randomPointCh();
        Edge e3 = new Edge(3, 4, fromPoint3, toPoint3, 2200, profile3);

        float[] samples4 = new float[]{384.75f, 384.6875f, 400f, 384.5f};
        DoubleUnaryOperator profile4 = Functions.sampled(samples4, 1700);
        PointCh fromPoint4 = toPoint3;
        PointCh toPoint4 = randomPointCh();
        Edge e4 = new Edge(4, 5, fromPoint4, toPoint4, 1700, profile4);

        List<Edge> l = new ArrayList();
        l.add(e0);
        l.add(e1);
        l.add(e2);
        l.add(e3);
        l.add(e4);

        SingleRoute sr = new SingleRoute(l);

        ElevationProfile actual = ElevationProfileComputer.elevationProfile(sr, 100);
        float[] samples = new float[]{100, 100, 200};
        double expectedMaxElevation = 400.0;
        double actualMaxElevation = actual.maxElevation();
        assertEquals(expectedMaxElevation, actualMaxElevation, 1);

        double expectedTotalDescent = 29.3;
        double actualTotalDescent = actual.totalDescent();
        assertEquals(expectedTotalDescent, actualTotalDescent, 1e-1);

        double expectedTotalAscent = 29;
        double actualTotalAscent = actual.totalAscent();
        assertEquals(expectedTotalAscent, actualTotalAscent, 1e-1);

    }

    @Test
    public void elevationProfileComputerWorksWithSimpleValues2() {
        float[] samples0 = new float[]{50f, 30f, 40f, 60f};
        DoubleUnaryOperator profile0 = Functions.sampled(samples0, 300);
        PointCh fromPoint0 = randomPointCh();
        PointCh toPoint0 = randomPointCh();
        Edge e0 = new Edge(0, 1, toPoint0, fromPoint0, 300, profile0);

        float[] samples1 = new float[]{60f, 50f, 100f};
        DoubleUnaryOperator profile1 = Functions.sampled(samples1, 200);
        PointCh fromPoint1 = toPoint0;
        PointCh toPoint1 = randomPointCh();
        Edge e1 = new Edge(1, 2, fromPoint1, toPoint1, 200, profile1);

        DoubleUnaryOperator profile2 = Functions.constant(Double.NaN);
        PointCh fromPoint2 = toPoint1;
        PointCh toPoint2 = randomPointCh();
        Edge e2 = new Edge( 2, 3, fromPoint2, toPoint2, 400, profile2);


        float[] samples3 = new float[]{30f, 0f};
        DoubleUnaryOperator profile3 = Functions.sampled(samples3, 100);
        PointCh fromPoint3 = toPoint2;
        PointCh toPoint3 = randomPointCh();
        Edge e3 = new Edge(3, 4, fromPoint3, toPoint3, 100, profile3);

        List<Edge> l = new ArrayList();
        l.add(e0);
        l.add(e1);
        l.add(e2);
        l.add(e3);


        SingleRoute sr = new SingleRoute(l);



        ElevationProfile profile = ElevationProfileComputer.elevationProfile(sr, 55);
        double expected = 39.474;
        double actual = profile.elevationAt(52.63);

        assertEquals(expected, actual, 1e-3);

        actual = profile.elevationAt(500);
        expected = 82.88;
        assertEquals(expected, actual, 1e-1);

        actual = profile.elevationAt(1000);
        expected = 0f;
        assertEquals(expected, actual);

    }
    @Test
    public void ElevationProfileComputerWorksWhenThereAreNoProfiles() {
        DoubleUnaryOperator profile = Functions.constant(Double.NaN);

        PointCh fromPoint0 = randomPointCh();
        PointCh toPoint0 = randomPointCh();
        Edge e0 = new Edge( 0, 1, fromPoint0, fromPoint0, 400, profile);

        PointCh fromPoint1 = randomPointCh();
        PointCh toPoint1 = randomPointCh();
        Edge e1 = new Edge(23, 4, fromPoint1, toPoint1, 100, profile );

        PointCh fromPoint2 = randomPointCh();
        PointCh toPoint2 = randomPointCh();
        Edge e2 = new Edge(243, 43, fromPoint2, toPoint2, 100, profile);

        List<Edge> l = new ArrayList();
        l.add(e0);
        l.add(e1);
        l.add(e2);

        SingleRoute sr = new SingleRoute(l);
        ElevationProfile elevationProfile = ElevationProfileComputer.elevationProfile(sr, 100);

        float[] expected = new float[] {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f };
        float[] actual = elevationProfile.elevationSamples; //Repasser en les attributs de ElevationProgile en package private pour ces tests

        assertArrayEquals(expected, actual);
    }

    
    private PointCh randomPointCh() {
        var rnd = new Random();
        double e = rnd.nextDouble(SwissBounds.MIN_E, SwissBounds.MAX_E);
        double n = rnd.nextDouble(SwissBounds.MIN_N, SwissBounds.MAX_N);
        return new PointCh(e, n);
    }

}
