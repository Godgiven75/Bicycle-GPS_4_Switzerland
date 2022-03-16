package ch.epfl.javelo;

import static java.lang.Math.fma;

/**
 * Classe finale et non-instanciable permettant d'effectuer des opérations mathématiques
 */
public final class Math2 {

    private Math2() {}

    /**
     * Retourne la division entière par excès de x par y
     * @param x un entier x
     * @param y un entier y
     * @return la division entière par excès de x par y
     */
    public static int ceilDiv(int x, int y) {
        Preconditions.checkArgument(x >= 0 && y > 0 );
        return (x + y - 1) / y;
    }

    /**
     * Retourne l'ordonnée du point d'abscisse x appartenant à la droite passant par les points (0, y0) et (1, y1)
     * @param y0 ordonnée du point d'abscisse 0 par lequel passe la droite
     * @param y1 ordonnée du point d'abscisse 1 par lequel passe la droite
     * @param x abscisse du point dont on souhaite interpoler l'ordonnée
     * @return the interpolation on the line going through (0, y0) and (1, y1)
     */
    public static double interpolate(double y0, double y1, double x) {
        return fma(y1 - y0, x, y0);
    }

    /**
     * Limite la valeur de v à l'intervalle fermé compris entre min et max (cas entier (int))
     * @param min minimum de l'intervalle
     * @param v valeur à limiter
     * @param max maximum de l'intervalle
     * @return v si  min <= v <= max , min si v < min et max si v > max
     */
    public static int clamp(int min, int v, int max) {
        Preconditions.checkArgument(min <= max);
        if (v < min) return min;
        if (v > max) return max;
        return v;
    }

    /**
     * Limite la valeur de v à l'intervalle fermé compris entre min et max (cas réel (double))
     * @param min minimum de l'intervalle
     * @param v valeur à limiter
     * @param max maximum de l'intervalle
     * @return v si  min <= v <= max , min si v < min et max si v > max
     */
    public static double clamp(double min, double v, double max) {
        Preconditions.checkArgument (min <= max);
        if (v < min) return min;
        if (v > max) return max;
        return v;
    }

    /**
     * Retourne le sinus hyperbolique inverse de la valeur x
     * @param x
     * @return le sinus hyperbolique inverse de la valeur x
     */
    public static double asinh(double x) {
        return Math.log(x + Math.sqrt(1 + x * x));
    }

    /**
     * Retourne le produit scalaire de deux vecteurs
     * @param uX composante x du premier vecteur
     * @param uY composante y du premier vecteur
     * @param vX composante x du second vecteur
     * @param vY composante y du second vecteur
     * @return le produit scalaire des vecteurs (uX, vX) et (uY, vY)
     */
    public static double dotProduct(double uX, double uY, double vX, double vY){
        return fma(uX,vX,uY*vY);
    }

    /**
     * Retourne le carré de la norme d'un vecteur
     * @param uX composante x du vecteur
     * @param uY composante y du vecteur
     * @return le carré de la norme du vecteur (uX, uY)
     */
    public static double squaredNorm(double uX, double uY) {
        double norm = norm(uX, uY);
        return norm * norm;
    }

    /**
     * Retourne la norme d'un vecteur
     * @param uX composante x du vecteur
     * @param uY composante y du vecteur
     * @return la norme du vecteur (uX, uY)
     */
    public static double norm(double uX, double uY){
        return Math.hypot(uX, uY);
    }

    /**
     * Retourne la norme de la projection d'un vecteur (AP) sur un autre (AB)
     * @param aX abscisse du point A
     * @param aY ordonnée du point A
     * @param bX abscisse du point B
     * @param bY ordonnée du point B
     * @param pX abscisse du point P
     * @param pY ordonnée du point P
     * @return la norme de la projection de AP sur AB
     */
    public static double projectionLength(double aX, double aY, double bX, double bY, double pX, double pY ) {
        double AB_x = bX - aX;
        double AB_y = bY - aY;
        double q  = dotProduct(AB_x, AB_y, pX - aX, pY - aY ) / squaredNorm(AB_x, AB_y);

        return norm(q * AB_x, q * AB_y);
    }
}