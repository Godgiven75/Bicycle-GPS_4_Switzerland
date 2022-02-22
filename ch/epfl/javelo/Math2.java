package epfl.javelo;

public final class Math2 {

    /**
     * This class should not be instantiated
     */
    private Math2() {}

    static int ceilDiv(int x, int y) {
        if( x < 0 || y <= 0 ) throw new IllegalArgumentException();
        return (x + y - 1) / y;
    }

    static double interpolate(double y0, double y1, double x) {
        return Math.fma(y1 - y0, x, y0);
    }

    static int clamp(int min, int v, int max) {
        if (min > max) throw new IllegalArgumentException();
        if (v < min) return min;
        if (v > max) return max;
        return v;
    }

    static double clamp(double min, double v, double max) {
        if (min > max) throw new IllegalArgumentException();
        if (v < min) return min;
        if (v > max) return max;
        return v;
    }

    static double asinh(double x) {
        return Math.log(x + Math.sqrt(1 + x * x));
    }

    static double dotProduct(double uX, double uY, double vX, double vY){
        return uX*vX + uY*vY;
    }

    double squaredNorm(double uX, double uY) {
        return uX * uX + uY * uY;
    }

    double Norm(double uX, double uY){
        return Math.sqrt(squaredNorm(uX, uY));
    }

    double projectionLength(double aX, double aY, double bX, double bY, double pX, double pY ) {
        double AB_x = bX - aX;
        double AB_y = bY - aY;
        double q  = dotProduct(AB_x, AB_y, pX - aX, pY - aY )/ squaredNorm(AB_x, AB_y);

        return Norm(q * AB_x, q * AB_y);
    }
}
