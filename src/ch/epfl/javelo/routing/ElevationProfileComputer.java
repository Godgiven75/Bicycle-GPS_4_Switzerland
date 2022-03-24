package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.util.Arrays;

import static java.lang.Float.isNaN;

/**
 * Représente un calculateur de profil en long (càd calculer le profil en long d'un itinéraire donné)
 */
public final class ElevationProfileComputer { // modifier la dernière boucle pour ne pas itérer sur tout le tableau

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
        int numberOfSamples = (int) Math.ceil( itineraryLength / maxStepLength) + 1;
        double stepLength = itineraryLength /(double) (numberOfSamples - 1);
        float[] samples = new float[numberOfSamples];
        double position = 0;
        for (int i = 0; i < samples.length; ++i) {
            samples[i] = (float) route.elevationAt(position);// Interpolation d'arêtes avec un profil déjà faite dans l'appel...
            position += stepLength;
        }

        // Recherche du 1er échantillon valide du tableau
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
        for (int i = firstValidSampleIndex; i < samples.length; ++i) {
            if (isNaN(samples[i])) {
                double y0 = samples[i - 1];
                int j = i + 1;
                while (isNaN(samples[j])) {
                    ++j;
                }
                double y1 = samples[j];

                for(int k = i ; k < j; ++k) {
                    double x = (double) (k - i + 1) / (double) (j - i + 1);
                    samples[k] = (float) Math2.interpolate(y0, y1 , x);
                }
                i = j;
            }
        }
        return new ElevationProfile(itineraryLength, samples);
    }

}
