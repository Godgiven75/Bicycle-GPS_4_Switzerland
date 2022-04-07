package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

/**
 * Représente un point dans le système Web Mercator.
 *
 * Ses attributs sont :
 * double x : la coordonnée x du point
 * double y : la coordonnée y du point
 *
 * @author Tanguy Dieudonné (326618)
 * @author Nathanaël Girod (329987)
 */
public record PointWebMercator(double x, double y) {

    private static final int BASE_ZOOM_LEVEL = 8;

    /**
     * Construit les coordonnées x et y du point.
     *
     * @param x coordonnée x du point
     * @param y coordonnée y du point
     * @throws IllegalArgumentException si l'une des coordonnées n'est pas comprise
     * dans l'intervalle [0;1]
     */
    public PointWebMercator {
        Preconditions.checkArgument( (0 <= x && x <= 1) && (0 <= y && y <= 1) );
    }

    /**
     * Retourne le point dont les coordonnées x et y sont au niveau de zoom zoomLevel.
     *
     * @param zoomLevel le niveau de zoom
     * @param x la coordonnée x du point au niveau de zoom
     * @param y la coordonnée y du point au niveau de zoom
     *
     * @return le point dont les coordonnées x et y sont au niveau de zoom zoomLevel
     */
    public static PointWebMercator of(int zoomLevel, double x, double y ) {
        double scaledX = Math.scalb(x, - (actualZoomLevel(zoomLevel)));
        double scaledY = Math.scalb(y, - (actualZoomLevel(zoomLevel)));
        return new PointWebMercator(scaledX, scaledY);
    }

    /**
     * Retourne le point WebMercator correspondant au point du système de
     * coordonnées suisse donné.
     *
     * @param pointCh le point du système de coordonnées suisse
     *
     * @return le point WebMercator correspondant au point du système de
     * coordonnées suisse donné
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
     *
     * @param zoomLevel le niveau de zoom
     *
     * @return la coordonnée x du point au niveau de zoom donné
     */
    public double xAtZoomLevel(int zoomLevel) {
        return Math.scalb(x, actualZoomLevel(zoomLevel) );
    }

    /**
     * Retourne la coordonnée y du point au niveau de zoom donné.
     *
     * @param zoomLevel le niveau de zoom
     *
     * @return la coordonnée x du point au niveau de zoom donné
     */
    public double yAtZoomLevel(int zoomLevel) {
        return Math.scalb(y, actualZoomLevel(zoomLevel));
    }

    /**
     * Retourne la longitude du point, en radians.
     *
     * @return la longitude du point, en radians
     */
    public double lon() {
        return WebMercator.lon(x);
    }

    /**
     * Retourne la latitude du point, en radians
     *
     * @return la latitude du point, en radians
     */
    public double lat() {
        return WebMercator.lat(y);
    }

    /**
     * Retourne le point de coordonnées suisses se trouvant à la même position
     * que le récepteur (this) ou null si ce point n'est pas dans les limites de
     * la Suisse définies par SwissBounds.
     *
     * @return le point de coordonnées suisses se trouvant à la même position
     * que le récepteur (this) ou null si ce point n'est pas dans les limites de
     * la Suisse définies par SwissBounds
     */
    public PointCh toPointCh() {
        double lon = lon();
        double lat = lat();
        double e = Ch1903.e(lon, lat);
        double n = Ch1903.n(lon, lat);

        return SwissBounds.containsEN(e, n) ? new PointCh(e, n) : null;
    }

    /**
     * Retourne le niveau de zoom de base additioné au niveau de zoom passé en paramètre
     *
     * @param zoomLevel le niveau de zoom
     *
     * @return le niveau de zoom de base additioné au niveau de zoom passé en paramètre
     */
    private static int actualZoomLevel(int zoomLevel) {
        return BASE_ZOOM_LEVEL + zoomLevel;
    }
}
