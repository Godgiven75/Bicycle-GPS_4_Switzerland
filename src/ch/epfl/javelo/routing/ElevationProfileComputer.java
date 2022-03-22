package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.util.Arrays;

import static java.lang.Float.isNaN;

/**
 * Représente un calculateur de profil en long (càd calculer le profil en long d'un itinéraire donné)
 */
public final class ElevationProfileComputer {

    private ElevationProfileComputer() {}

    /**
     * Retourne le profil en long de l'itinéraire route, en garantissant que l'espacement entre les échantillons du
     * profil est d'au maximum maxStepLength mètres; lève IllegalArgumentException si cet espacement n'est pas
     * strictement positif
     * @param route
     * @param maxStepLength
     * @return le profil en long de l'itinéraire route, en garantissant que l'espacement entre les échantillons du
     * profil est d'au maximum maxStepLength mètres; lève IllegalArgumentException si cet espacement n'est pas
     * strictement positif
     */
    public static ElevationProfile elevationProfile(Route route, double maxStepLength) {
        Preconditions.checkArgument(maxStepLength > 0);
        double itineraryLength = route.length();

        int numberOfSamples = (int) Math.ceil(itineraryLength / maxStepLength) + 1;
        double stepLength = itineraryLength /(double) numberOfSamples;
        float[] samples = new float[numberOfSamples];
        double position = 0;
        for (int i = 0; i < samples.length; ++i) {
            samples[i] = (float) route.elevationAt(i * position);
        }

        // Recherche du 1er échantillon valide du tableau
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
            return new ElevationProfile(itineraryLength, samples);
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
                System.out.println("i " + i);
                System.out.println(j - i + 1);
                double x = (double) i / ((j - i + 1) * stepLength);
                samples[i] = (float) Math2.interpolate(y0, y1, x);
            }
        }
        return new ElevationProfile(itineraryLength, samples);
    }

}
