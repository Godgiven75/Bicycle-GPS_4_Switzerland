package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;

import java.util.DoubleSummaryStatistics;

/**
 * Représente le profil en long d'un itinéraire simple ou multiple
 *
 * @author Tanguy Dieudonné (326618)
 * @author Nathanaël Girod (329987)
 */
public class ElevationProfile {
     final double length;
     final float[] elevationSamples;

    /**
     * Construit le profil en long d'un itinéraire de longueur length (en mètres)
     * et dont les échantillons d'altitude, répartis uniformément le long de
     * l'itinéraire, sont contenus dans elevationSamples.
     *
     * @param length la longueur de l'itinéraire, en mètres
     * @param elevationSamples les échantillons d'altitude
     *
     * @throws IllegalArgumentException si la longueur de l'itinéraire est négative
     * ou si les échantillons ne sont pas au moins deux.
     */
    public ElevationProfile(double length, float[] elevationSamples) {
        Preconditions.checkArgument(length > 0 && elevationSamples.length >= 2);
        this.length = length;
        this.elevationSamples = elevationSamples.clone();
    }

    /**
     * Retourne la longueur du profil, en mètres.
     *
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
     * Retourne l'altitude minimum du profil, en mètres.
     *
     * @return l'altitude minimum du profil, en mètres
     */
    public double minElevation() {
        return summaryStatistics(elevationSamples).getMin();
    }

    /**
     * Retourne l'altitude maximum du profil, en mètres.
     *
     * @return l'altitude maximum du profil, en mètres
     */
    public double maxElevation() {
        return summaryStatistics(elevationSamples).getMax();
    }

    /**
     * Retourne le dénivelé positif total du profil, en mètres.
     *
     * @return le dénivelé positif total du profil, en mètres
     */
    public double totalAscent() {
        double totalAscent = 0;
        for (int i = 0; i < elevationSamples.length - 1; i++) {
            double difference = elevationSamples[i+1] - elevationSamples[i];
            if (difference >= 0) {
                totalAscent += difference;
            }
        }
        return totalAscent;
    }

    /**
     * Retourne le dénivelé négatif total du profil, en mètres.
     *
     * @return le dénivelé négatif total du profil, en mètres
     */
    public double totalDescent() {
        double negTotalDescent = 0;
        for (int i = 0; i < elevationSamples.length - 1; i++) {
            double difference = elevationSamples[i + 1] - elevationSamples[i];
            if (difference <= 0) {
                negTotalDescent -= difference;
            }
        }
        return negTotalDescent;
    }

    /**
     * Retourne l'altitude du profil à la position donnée, qui n'est pas forcément
     * comprise entre 0 et la longueur du profil; le premier échantillon est
     * retourné lorsque la position est négative, le dernier lorsqu'elle est
     * supérieure à la longueur.
     *
     * @param position la position, en mètres
     *
     * @return l'altitude du profil à la position donnée, qui n'est pas forcément
     * comprise entre 0 et la longueur du profil; le premier échantillon est
     * retourné lorsque la position est négative, le dernier lorsqu'elle est
     * supérieure à la longueur
     */
    public double elevationAt(double position) {
        return Functions.sampled(elevationSamples, length).applyAsDouble(position);
    }

}
