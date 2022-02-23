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
        return (lon+Math.PI)/(2*Math.PI);
    }

    /**
     * Retourne la coordonnée y (dans le système WebMercator) d'un point (donnée en WGS 84) se trouvant à la latitude lat, donnée en radians
     * @param lat
     * @return la coordonnée y (dans le système WebMercator) d'un point (donnée en WGS 84) se trouvant à la latitude lat, donnée en radians
     */
    public static double y(double lat) {
        return (Math.PI - Math2.asinh( Math.tan(lat))) / (2 * Math.PI);
    }

    public static double lon(double x) {

    }

    public static double lat(double y) {

    }

}
