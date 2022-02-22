package epfl.javelo;

public final class Math2 {

    /**
     * This class should not be instantiated
     */
    private Math2() {}

    static int ceilDiv(int x, int y) {
        return (x + y - 1) / y;
    }

    static double interpolate(double y0, double y1, double x) {

    }

    static int clamp(int min, int v, int max) {

    }

    static double clamp(double min, double v, double max) {

    }

    static double asinh(double x) {

    }

    static double dotProduct(double uX, double uY, double vX, double vY){

    }

    double squaredNorm(double uX, double uY) {

    }

    double Norm(double uX, double uY){
        return Math.sqrt(squaredNorm(uX, uY));
    }

    double projectionLength(double aX, double aY, double bX, double bY, double pX, double pY ) {

    }
}
