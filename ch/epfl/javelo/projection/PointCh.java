package epfl.javelo.projection;


import epfl.javelo.Preconditions;

/**
 * Enregistrement représentant un point dans le système de coordonnées suisse
 */
public record PointCh(double e, double n ) {

    /**
     * Permet de construire un PointCh, lance une exception si les coordonnées ne respectent pas les conditions définies
     * par Swissbounds
     * @param e coordonnée est du point
     * @param n coordonnée nord du point
     */
    public PointCh {
        Preconditions.checkArgument(SwissBounds.containsEN(e, n));
    }

    /**
     * Retourne la distance au carré entre ce PointCh et un autre PointCh
     * @param that autre PointCh
     * @return la distance au carré entre ce PointCh et un autre PointCh
     */
    public double squaredDistanceTo(PointCh that) {
        return distanceTo(that) * distanceTo(that);
    }

    /**
     * Retourne la distance au carré entre ce PointCh et un autre PointCh
     * @param that autre PointCh
     * @return la distance au carré entre ce PointCh et un autre PointCh
     */
    public double distanceTo(PointCh that) {
        return Math.hypot(that.e - this.e, that.n - this.n);
    }

    /**
     * Retourne la longitude du PointCh (en degrés)
     * @return la longitude du PointCh
     */
    public double lon()  {
        return Ch1903.lon(e, n);
    }

    /**
     * Retourne la latitude du PointCh
     * @return la latitude du PointCh
     */
    public double lat() {
        return Ch1903.lat(e, n);
    }
}
