package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;

import java.util.Arrays;
import java.util.DoubleSummaryStatistics;

/**
 * Représente le profil en long d'un itinéraire simple ou multiple
 */
public class ElevationProfile {
    public final double length;
    public final float[] elevationSamples;

    /**
     * Construit le profil en long d'un itinéraire de longueur length (en mètres) et dont les échantillons d'altitude,
     * répartis uniformément le long de l'itinéraire, sont contenus dans elevationSamples
     * @param length
     * @param elevationSamples
     */
    public ElevationProfile(double length, float[] elevationSamples) {
        Preconditions.checkArgument(length > 0 && elevationSamples.length >= 2);
        this.length = length;
        this.elevationSamples = elevationSamples.clone(); // Vérifier que l'immuabilité est ici satisfaite
    }

    /**
     * Retourne la longueur du profil, en mètres
     * @return la longueur du profil, en mètres
     */
    public double length() {
        return length;
    }

    // Retourne un DoubleSummaryStatistics composé des éléments d'elevationSamples
    private DoubleSummaryStatistics summaryStatistics(float[] elevationSamples) {
        DoubleSummaryStatistics s = new DoubleSummaryStatistics();
        for (float f : elevationSamples) {
            s.accept(f);
        }
        return s;
    }

    /**
     * Retourne l'altitude minimum du profil, en mètres
     * @return
     */
    public double minElevation() {
        return summaryStatistics(elevationSamples).getMin();
    }

    /**
     * Retourne l'altitude maximum du profil, en mètres
     * @return l'altitude maximum du profil, en mètres
     */
    public double maxElevation() {
        return summaryStatistics(elevationSamples).getMax();
    }

    /**
     * Retourne le dénivelé positif total du profil, en mètres
     * @return le dénivelé positif total du profil, en mètres
     */
    public double totalAscent() {
        double totalAscent = 0;
        for (int i = 0; i < elevationSamples.length; i++) {
            double difference = elevationSamples[i+1] - elevationSamples[i];
            if (difference >= 0) {
                totalAscent += difference;
            }
        }
        return totalAscent;
    }

    /**
     * Retourne le dénivelé négatif total du profil, en mètres
     * @return le dénivelé négatif total du profil, en mètres
     */
    public double totalDescent() {
        double totalDescent = 0;
        for (int i = 0; i < elevationSamples.length; i++) {
            double difference = elevationSamples[i+1] - elevationSamples[i];
            if (difference <= 0) {
                totalDescent += difference;
            }
        }
        return totalDescent;
    }

    /**
     * Retourne l'altitude du profil à la position donnée, qui n'est pas forcément comprise entre 0 et la longueur du
     * profil; le premier échantillon est retourné lorsque la position est négative, le dernier lorsqu'elle est
     * supérieure à la longueur
     * @param position
     * @return l'altitude du profil à la position donnée, qui n'est pas forcément comprise entre 0 et la longueur du
     * profil; le premier échantillon est retourné lorsque la position est négative, le dernier lorsqu'elle est
     * supérieure à la longueur
     */
    public double elevationAt(double position) {
        // Ne poourrait-on pas utiliser clamp ?
        if (position < 0) return elevationSamples[0];
        if (position > length) return elevationSamples[elevationSamples.length - 1];
        int indexOfAltitude = (int) Long.valueOf(Math.round(position)).doubleValue();
        return elevationSamples[indexOfAltitude];

        /*return position < 0
                ? elevationSamples[0]
                : position > length
                ? elevationSamples[elevationSamples.length - 1]
                : elevationSamples[indexOfAltitude]*/
    }

}
