package epfl.javelo.projection;

import epfl.javelo.Math2;

public final class WebMercator {
    private WebMercator() {}

    /**
     * Retourne la coordonnée x (dans le système WebMercator) d'un point (donnée en WGS 84) se trouvant à la longitude lon, donnée en radians
     * @param lon longitude
     * @return la coordonnée x (dans le système WebMercator) d'un point (donnée en WGS 84) se trouvant à la longitude lon, donnée en radians
     */
    public static double x(double lon) {
        return (lon + Math.PI) / (2 * Math.PI);
    }

    /**
     * Retourne la coordonnée y (dans le système WebMercator) d'un point (donnée en WGS 84) se trouvant à la latitude lat, donnée en radians
     * @param lat latitude
     * @return la coordonnée y (dans le système WebMercator) d'un point (donnée en WGS 84) se trouvant à la latitude lat, donnée en radians
     */
    public static double y(double lat) {
        return (Math.PI - Math2.asinh( Math.tan(lat) ) ) / (2 * Math.PI);
    }

    /**
     * Retourne la longitude lon (dans le système WGS 84) d'un point (donnée en WebMercator) se trouvant à l'abscisse x, donnée en radians
     * @param x
     * @return la longitude lon (dans le système WGS 84) d'un point (donnée en WebMercator) se trouvant à l'abscisse x, donnée en radians
     */
    public static double lon(double x) {
        return 2 * Math.PI * x - Math.PI;
    }

    /**
     * Retourne la latitude lat (dans le système WGS 84) d'un point (donnée en WebMercator) se trouvant à l'ordonnée y, donnée en radians
     * @param y
     * @return la latitude lat (dans le système WGS 84) d'un point (donnée en WebMercator) se trouvant à l'ordonnée y, donnée en radians
     */
    public static double lat(double y) {
        return Math.atan( Math2.asinh(Math.PI - 2 * Math.PI * y) );
    }

}
