package ch.epfl.javelo.projection;

/**
 * Classe finale et non-instanciable, offrant des méthodes statiques permettant
 * de convertir entre les coordonnées WGS84 et les coordonnées suisses.
 *
 * @author Tanguy Dieudonné (326618)
 * @author Nathanaël Girod (329987)
 */
public final class Ch1903 {
    private Ch1903() {}

    /**
     * Retourne la coordonée est (E) (dans le système (CH1903+) d'un point donné
     * dans le système WGS 84.
     *
     * @param lon la longitude du point
     * @param lat la latitude du point
     *
     * @return la coordonée est (E) (dans le système (CH1903+) d'un point donné
     * dans le système WGS 84
     */
    public static double e(double lon, double lat) {
        double l1 = (1e-4) * (3600 * Math.toDegrees(lon) - 26782.5);
        double phi1 = (1e-4) * (3600 * Math.toDegrees(lat) - 169028.66);

        return 2600072.37 + 211455.93 * l1 - 10938.51 * l1 * phi1
                - 0.36 * l1 * phi1 * phi1 - 44.54 * l1 * l1 * l1;
    }
    /**
     * Retourne la coordonnée nord (N) (dans le système (CH1903+) d'un point donné
     * dans le système WGS 84.
     *
     * @param lon la longitude du point
     * @param lat la latitude du point
     *
     * @return la coordonnée nord (N) (dans le système (CH1903+) d'un point donné
     * dans le système WGS 84
     */
    public static double n(double lon, double lat) {
        double l1 = (1e-4) * (3600 * Math.toDegrees(lon) - 26782.5);
        double phi1 = (1e-4) * (3600 * Math.toDegrees(lat) - 169028.66);

        return 1200147.07 + 308807.95 * phi1 + 3745.25 * l1 *l1 + 76.63 * phi1 *phi1
                - 194.56 * l1 * l1 * phi1 + 119.79 * phi1 * phi1 * phi1;
    }

    /**
     * Retourne la longitude (système WGS84) d'un point donné en coordonnées CH1903+.
     *
     * @param e la coordonnée Est (E)
     * @param n la coordonnée Nord (N)
     *
     * @return la longitude (système WGS84) d'un point donné en coordonnées CH1903+
     */
    public static double lon(double e, double n) {
        double x = 1e-6 * (e - 2600000);
        double y = 1e-6 * (n - 1200000);
        double l0 = 2.6779094
                + 4.728982 * x
                + 0.791484 * x * y
                + 0.1306 * x * y * y
                - 0.0436 * x * x * x;

        return Math.toRadians(l0 * 100.0/36.0);
    }
    /**
     * Retourne la latitude (système WGS84) d'un point donné en coordonnées CH1903+.
     *
     * @param e la coordonnée Est (E)
     * @param n la coordonnée Nord (N)
     *
     * @return la latitude (système WGS84) d'un point donné en coordonnées CH1903+
     */
    public static double lat(double e, double n) {
        double x = 1e-6 * (e - 2600000.0);
        double y = 1e-6 * (n - 1200000.0);
        double phi0 = 16.9023_892  + 3.238272 * y - 0.270978 * x * x
                - 0.002528 * y * y - 0.0447 * x * x * y - 0.0140 * y * y * y;

        return Math.toRadians(phi0 * 100.0/36.0);
    }
}
