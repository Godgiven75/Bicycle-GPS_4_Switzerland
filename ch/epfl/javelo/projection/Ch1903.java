package epfl.javelo.projection;

public final class Ch1903 {
    private Ch1903() {}

    //longitude lambda, latitude phi

    /**
     *
     * @param lon
     * @param lat
     * @return the coordinate E of the point originally in WGS 84 coordinates
     */
    public static double e(double lon, double lat) {
        double l1 = Math.pow(10,-4)*(3600*Math.toDegrees(lon) - 26_782.5);
        double phi1 = Math.pow(10,-4)*(3600*Math.toDegrees(lat) - 169_028.66);

        return 2_600_072.37 + 211_455.93*l1 - 10_938.51*l1*phi1 - 0.36*l1*phi1*phi1 - 44.54*Math.pow(l1,3);
    }

    public static double n(double lon, double lat) {
        double l1 = Math.pow(10,-4)*(3600*Math.toDegrees(lon) - 26_782.5);
        double phi1 = Math.pow(10,-4)*(3600*Math.toDegrees(lat) - 169_028.66);

    }
}
