package epfl.javelo.projection;

import epfl.javelo.Preconditions;

/**
 * Enregistrement représentant un point dans le système Web Mercator
 * Ses attributs sont : double x, la coordonnée x du point; double y la coordonnée y du point.
 */
public record PointWebMercator(double x, double y) {

    private static final int BASE_ZOOM_LEVEL = 8;

    /**
     * Constructeur validant les coordonnées x et y du point et levant une expressions si l'une d'elle n'est pas comprise dans l'intervalle [0;1]
     * @param x coordonnée x du point
     * @param y coordonnée y du point
     */
    public PointWebMercator {
        Preconditions.checkArgument( !(0 <= x && x <= 1) || !(0 <= y && y <= 1));
    }

    /**
     * Retourne le point dont les coordonnées x et y sont au niveau de zoom zoomLevel
     * @param zoomLevel niveau de zoom
     * @param x coordonnée x du point au niveau de zoom
     * @param y coordonnée y du point au niveau de zoom
     * @return le point dont les coordonnées x et y sont au niveau de zoom zoomLevel
     */
    public static PointWebMercator of(int zoomLevel, double x, double y ) {
        Preconditions.checkArgument(!(zoomLevel >= 0 && zoomLevel <= 20));

        double scaledX = Math.scalb(x, - (zoomLevel + BASE_ZOOM_LEVEL));
        double scaledY = Math.scalb(y, - (zoomLevel + BASE_ZOOM_LEVEL));
        return new PointWebMercator(scaledX, scaledY);
    }

    /**
     * Retourne le point WebMercator correspondant au point du système de coordonnées suisse donné
     * @param pointCh point du système de coordonnées suisse
     * @return le point WebMercator correspondant au point du système de coordonnées suisse donné
     */
    public static PointWebMercator ofPointCh(PointCh pointCh) {
        double longitude = pointCh.lon();
        double latitude = pointCh.lat();

        double x = WebMercator.x(longitude);
        double y = WebMercator.y(latitude);

        return new PointWebMercator(x, y);
    }

    /**
     * Retourne la coordonnée x du point au niveau de zoom donné
     * @param zoomLevel niveau de zoom
     * @return Retourne la coordonnée x du point au niveau de zoom donné
     */
    public double xAtZoomLevel(int zoomLevel) {
        if(!(zoomLevel >= 0 && zoomLevel <= 20)) throw new IllegalArgumentException();
        return Math.scalb(x, zoomLevel + BASE_ZOOM_LEVEL );
    }

    /**
     * Retourne la coordonnée y du point au niveau de zoom donné
     * @param zoomLevel niveau de zoom
     * @return Retourne la coordonnée x du point au niveau de zoom donné
     */
    public double yAtZoomLevel(int zoomLevel) {
        if(!(zoomLevel >= 0 && zoomLevel <= 20)) throw new IllegalArgumentException();
        return Math.scalb(y, zoomLevel + BASE_ZOOM_LEVEL );
    }

    /**
     * Retourne la longitude du point, en radians
     * @return la longitude du point, en radians
     */
    public double lon() {
        return WebMercator.lon(x);
    }

    /**
     * Retourne la latitude du point, en radians
     * @return la latitude du point, en radians
     */
    public double lat() {
        return WebMercator.lat(y);
    }

    /**
     * Retourne le point de coordonnées suisses se trouvant à la même position que le récepteur (this) ou null si ce point n'est pas dans les limites de la Suisse définies par SwissBounds
     * @return le point de coordonnées suisses se trouvant à la même position que le récepteur (this) ou null si ce point n'est pas dans les limites de la Suisse définies par SwissBounds
     *
     */
    public PointCh toPointCh() {
        double lon = lon();
        double lat = lat();
        double e = Ch1903.e(lon, lat);
        double n = Ch1903.n(lon, lat);

        return SwissBounds.containsEN(e, n) ? new PointCh(e, n) : null;
    }
}
