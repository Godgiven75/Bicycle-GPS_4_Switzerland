package epfl.javelo;

import static java.lang.Math.fma;

public final class Math2 {

    /**
     * This class should not be instantiated
     */
    private Math2() {}

    /**
     * Ceiling integer division
     * @param x
     * @param y
     * @return the ceiling of the x/y quotient
     */
    static int ceilDiv(int x, int y) {
        if( x < 0 || y <= 0 ) throw new IllegalArgumentException();
        return (x + y - 1) / y;
    }

    /**
     * Returns the image of the point with x-coordinate x on the line going through the points (0, y0) and (1, y1)
     * @param y0
     * @param y1
     * @param x
     * @return the interpolation on the line going through (0, y0) and (1, y1)
     */
    static double interpolate(double y0, double y1, double x) {
        return fma(y1 - y0, x, y0);
    }

    /**
     * limits the value (integer) v to the closed interval between min and max
     * @param min
     * @param v
     * @param max
     * @return v if  min <= v <= max , min if v < min and max if v > max
     */
    static int clamp(int min, int v, int max) {
        if (min > max) throw new IllegalArgumentException();
        if (v < min) return min;
        if (v > max) return max;
        return v;
    }

    /**
     * limits the value (double) v to the closed interval between min and max
     * @param min
     * @param v
     * @param max
     * @return v if  min <= v <= max , min if v < min and max if v > max
     */
    static double clamp(double min, double v, double max) {
        if (min > max) throw new IllegalArgumentException();
        if (v < min) return min;
        if (v > max) return max;
        return v;
    }

    /**
     * Returns the inverse hyperbolic sinus of the value x
     * @param x
     * @return inverse hyperbolic sine of x
     */
    static double asinh(double x) {
        return Math.log(x + Math.sqrt(1 + x * x));
    }

    /**
     * dot product of two vectors
     * @param uX
     * @param uY
     * @param vX
     * @param vY
     * @return the dot product of the vectors (uX, vX) and (uY, vY)
     */
    static double dotProduct(double uX, double uY, double vX, double vY){
        return fma(uX,vX,uY*vY);
    }

    /**
     * The square of the norm of a vector
     * @param uX
     * @param uY
     * @return the square of the norm of the vector (uX, uY)
     */
    double squaredNorm(double uX, double uY) {
        return uX * uX + uY * uY;
    }

    /**
     * The norm of a vector
     * @param uX
     * @param uY
     * @return the norm of the vector (uX, uY)
     */
    double Norm(double uX, double uY){
        return Math.sqrt(squaredNorm(uX, uY));
    }

    /**
     * Returns the length of the projection of the vector AP on AP
     * @param aX
     * @param aY
     * @param bX
     * @param bY
     * @param pX
     * @param pY
     * @return
     */
    double projectionLength(double aX, double aY, double bX, double bY, double pX, double pY ) {
        double AB_x = bX - aX;
        double AB_y = bY - aY;
        double q  = dotProduct(AB_x, AB_y, pX - aX, pY - aY )/ squaredNorm(AB_x, AB_y);

        return Norm(q * AB_x, q * AB_y);
    }
}