package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static ch.epfl.test.TestRandomizer.newRandom;
import static java.lang.Float.NaN;
import static java.lang.Float.isNaN;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ElevationProfileComputerTest {

    @Test
    public void elevationProfileComputerArrayManipulationsWorkOnPresetValues() { // Juste un test de l'agorithme que l'on utilise pour manipuler les tableaux
        float[] arr = new float[]{NaN, 1f, 4f, 6f, NaN, NaN, NaN, 5f};
        /*List<Edge> l = new ArrayList<>();

        for(int i = 0; i < 7 ; ++i){
            l.add(new Edge(i, i, null, null, 5, null));
        }
        SingleRoute r = new SingleRoute(l);
        ElevationProfile ep = ElevationProfileComputer.elevationProfile(r, 100);*/
        System.out.println(Arrays.toString(samples(arr)));

    }
    private static float[] samples(float[] samples) {
        float routeLength = 8f;
        float stepLength = routeLength / (float) samples.length - 1;
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
        for (int i = 0; i < samples.length; ++i) {
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

        /*DoubleUnaryOperator profile2 = Functions.constant(Double.NaN);
        PointCh fromPoint2 = toPoint1;
        PointCh toPoint2 = randomPointCh();
        Edge e2 = new Edge( 2, 3, fromPoint2, toPoint2, 100, profile2);

        DoubleUnaryOperator profile3 = Functions.constant(Double.NaN);
        PointCh fromPoint3 = toPoint2;
        PointCh toPoint3 = randomPointCh();
        Edge e3 = new Edge(3, 4, fromPoint3, toPoint3, 100, profile3);

        float[] samples4 = new float[]{200, 100};
        DoubleUnaryOperator profile4 = Functions.sampled(samples4, 100);
        PointCh fromPoint4 = toPoint3;
        PointCh toPoint4 = randomPointCh();
        Edge e4 = new Edge(4, 5, fromPoint4, toPoint4, 100, profile4);
        */
        List<Edge> l = new ArrayList();
        l.add(e0);
        l.add(e1);
       /* l.add(e2);
        l.add(e3);
        l.add(e4);*/

        SingleRoute sr = new SingleRoute(l);

        ElevationProfile actual = ElevationProfileComputer.elevationProfile(sr, 100);
        float[] samples = new float[]{100, 100, 200};
        ElevationProfile expected = new ElevationProfile(200, samples);
        assertArrayEquals(expected.elevationSamples, actual.elevationSamples);
        assertEquals(expected.length, actual.length);

    }
    private PointCh randomPointCh() {
        var rnd = newRandom();
        double e = rnd.nextDouble(SwissBounds.MIN_E, SwissBounds.MAX_E);
        double n = rnd.nextDouble(SwissBounds.MIN_N, SwissBounds.MAX_N);
        return new PointCh(e, n);
    }

}
