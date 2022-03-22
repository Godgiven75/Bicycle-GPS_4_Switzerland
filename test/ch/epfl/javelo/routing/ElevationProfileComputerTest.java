package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Float.NaN;
import static java.lang.Float.isNaN;

public class ElevationProfileComputerTest {

    @Test
    public void elevationProfileComputerWorksOnPresetValues() {
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
        float routeLength = 16f;
        float stepLength = routeLength / (float) samples.length;
        boolean isOnlyNan = true;
        for (int i = 0; i < samples.length; ++i) {
            if (!isNaN(samples[i])) {
                Arrays.fill(samples, 0, i, samples[i]);
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
                int j = i;
                while (isNaN(samples[j])) {
                    ++j;
                }
                double y1 = samples[j];

                double x =  (double)  stepLength / (double) (j - i + 1);

                samples[i] = (float) Math2.interpolate(y0, y1, x);
            }
        }
        return samples;
    }

}
