package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

/**
 * Classe finale et non-instanciable permettant diverses conversions entre les coordonnées WGS 84 et les coordonnées
 * WebMercator
 */
public final class WebMercator {
    private WebMercator() {}

    /**
     * Retourne la coordonnée x (dans le système WebMercator) d'un point (donnée en WGS 84) se trouvant à la longitude lon, donnée en radians
     * @param lon longitude
     * @return la coordonnée x (dans le système WebMercator) d'un point (donnée en WGS 84) se trouvant à la longitude lon, donnée en radians
     */
    public static double x(double lon) {
        return Math.fma(lon, 1/(2.0 * Math.PI), 0.5);
    }

    /**
     * Retourne la coordonnée y (dans le système WebMercator) d'un point (donnée en WGS 84) se trouvant à la latitude lat, donnée en radians
     * @param lat latitude
     * @return la coordonnée y (dans le système WebMercator) d'un point (donnée en WGS 84) se trouvant à la latitude lat, donnée en radians
     */
    public static double y(double lat) {
        return (Math.PI - Math2.asinh( Math.tan(lat) ) ) / (2.0 * Math.PI);
    }

    /**
     * Retourne la longitude lon convertie en WGS 84 d'un point se trouvant à l'abscisse x (en WebMercator), donnée en radians
     * @param x
     * @return la longitude lon convertie en WGS 84 d'un point se trouvant à l'abscisse x (en WebMercator), donnée en radians
     */
    public static double lon(double x) {
        return 2.0 * Math.PI * x - Math.PI;
    }

    /**
     * Retourne la latitude lat convertie en WGS 84 d'un point se trouvant à l'ordonnée y (en WebMercator), donnée en radians
     * @param y
     * @return la latitude lat convertie en WGS 84 d'un point se trouvant à l'ordonnée y (en WebMercator), donnée en radians
     */
    public static double lat(double y) {
        return Math.atan( Math.sinh(Math.PI - 2.0 * Math.PI * y) );
    }

}
