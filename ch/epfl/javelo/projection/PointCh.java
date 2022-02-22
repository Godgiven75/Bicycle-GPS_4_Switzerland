package epfl.javelo.projection;



public record PointCh(double e, double n ) {
    public PointCh {
        if(!SwissBounds.containsEN(e, n)) throw new IllegalArgumentException();
    }

    /**
     * Retourne la distance au carré entre ce PointCh et un autre PointCh
     * @param that autre PointCj
     * @return la distance au carré entre ce PointCh et un autre PointCh
     */
    public double squaredDistanceTo(PointCh that) {
        return distanceTo(that) * distanceTo(that);
    }

    /**
     * Retourne la distance au carré entre ce PointCh et un autre PointCh
     * @param that autre PointCj
     * @return la distance au carré entre ce PointCh et un autre PointCh
     */
    public double distanceTo(PointCh that) {
        return Math.hypot(that.e - this.e, that.n - this.n);
    }

    /**
     * Retourne la longitude du PointCh
     * @return la longitude du PointCh
     */
    public double lon()  {
        return Math.toRadians(Ch1903.lon(e, n));
    }

    /**
     * Retourne la latitude du PointCh
     * @return la latitude du PointCh
     */
    public double lat() {
        return Math.toRadians(Ch1903.lat(e, n));

    }
}
