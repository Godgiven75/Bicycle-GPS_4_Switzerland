package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;

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

        double[] samples;
        return null;
    }

}
