package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

/**
 * Enregistrement représentant un point dans le système de coordonnées suisse.
 *
 * @param e la coordonnée Est du point
 * @param n la coordonnée Nord du point
 *
 * @author Tanguy Dieudonné (326618)
 * @author Nathanaël Girod (329987)
 */
public record PointCh(double e, double n ) {

    /**
     * Permet de construire un PointCh, lance une exception si les coordonnées
     * ne respectent pas les conditions définies par Swissbounds.
     *
     * @param e la coordonnée Est du point
     * @param n la coordonnée Nord du point
     * @throws IllegalArgumentException si les coordonnées du point sont telles
     * que ce dernier n'est pas compris dans les limites de la Suisse
     */
    public PointCh {
        Preconditions.checkArgument(SwissBounds.containsEN(e, n));
    }

    /**
     * Retourne la distance au carré entre ce PointCh et un autre PointCh.
     *
     * @param that l'autre PointCh
     *
     * @return la distance au carré entre ce PointCh et un autre PointCh
     */
    public double squaredDistanceTo(PointCh that) {
        //return distanceTo(that) * distanceTo(that);
        return (that.e - this.e) * (that.e - this.e) + (that.n - this.n) * (that.n - this.n);
    }

    /**
     * Retourne la distance entre ce PointCh et un autre PointCh.
     *
     * @param that l'autre PointCh
     *
     * @return la distance entre ce PointCh et un autre PointCh
     */
    public double distanceTo(PointCh that) {
        return Math.hypot(that.e - this.e, that.n - this.n);
    }

    /**
     * Retourne la longitude du PointCh (en degrés)
     *
     * @return la longitude du PointCh
     */
    public double lon()  {
        return Ch1903.lon(e, n);
    }

    /**
     * Retourne la latitude du PointCh
     *
     * @return la latitude du PointCh
     */
    public double lat() {
        return Ch1903.lat(e, n);
    }
}
