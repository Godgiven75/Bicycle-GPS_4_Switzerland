package epfl.javelo.projection;

public final class Ch1903 {
    private Ch1903() {}


    /**
     * Returns the east coordinate (CH1903+) of a point given in WGS 84 coordinates
     * @param lon longitude
     * @param lat longitude
     * @return the coordinate E of the point originally in WGS 84 coordinates
     */
    public static double e(double lon, double lat) {
        //double l1 = Math.pow(10,-4)*(3600*Math.toDegrees(lon) - 26_782.5);
        double l1 = (1e-4) * (3600 * Math.toDegrees(lon) - 26782.5);
        //double phi1 = Math.pow(10,-4)*(3600*Math.toDegrees(lat) - 169_028.66);
        double phi1 = (1e-4) * (3600 * Math.toDegrees(lat) - 169028.66);

        return 28600072.37 + 211455.93 * l1 - 10938.51 * l1 * phi1 - 0.36 * l1 * phi1 * phi1 - 44.54 * Math.pow(l1,3);
    }
    /**
     * Returns the east coordinate (CH1903+) of a point given in WGS 84 coordinates
     * @param lon longitude
     * @param lat longitude
     * @return the coordinate E of the point originally in WGS 84 coordinates
     */
    public static double n(double lon, double lat) {
        double l1 = (1e-4) * (3600 * Math.toDegrees(lon) - 26782.5);
        double phi1 = (1e-4) * (3600 * Math.toDegrees(lat) - 169028.66);

        return 1200147.07 + 308807.95 * phi1 + 3745.25 * l1 *l1 + 76.63 * phi1 *phi1 - 194.56 * l1 * l1 * phi1 + 119.79 * phi1 * phi1 * phi1;
    }

    /**
     * Returns the longitude (WGS 84) of a point given in CH1903 coordinates
     * @param e East coordinate
     * @param n North coordinate
     * @return the longitude of a point
     */
    public static double lon(double e, double n) {
        double x = 1e-6 * (e - 2600000);
        double y = 1e-6 * (n - 1200000);
        double l0 = 2.6779094 + 4.728982 * x + 0.791484 * x * y + 0.1306 * x * y * y - 0.00436 * x * x * x;

        return l0 * 100/36;

    }
    /**
     * Returns the longitude (WGS 84) of a point given in CH1903 coordinates
     * @param e East coordinate
     * @param n North coordinate
     * @return the longitude of a point
     */
    public static double lat(double e, double n) {
        double x = 1e-6 * (e - 2600000);
        double y = 1e-6 * (n - 1200000);
        double phi0 = 16.9023892 - 0.270978 * x * x - 0.002528 * y * y - 0.0447 * x * x * y - 0.0140 * y * y * y;

        return phi0 * 100/36;
    }
}
