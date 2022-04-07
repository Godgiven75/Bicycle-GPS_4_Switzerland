package ch.epfl.javelo.projection;

/**
 * Classe finale et non-instanciable contenant des constantes et des méthodes
 * liées aux limites de la Suisse.
 *
 * @author Tanguy Dieudonné (326618)
 * @author Nathanaël Girod (329987)
 */
public final class SwissBounds {
    private SwissBounds() {}

    /**
     * La plus petite coordonnée E (c-à-d coordonnée Est) de Suisse.
     */
    public final static double MIN_E = 2_485_000;

    /**
     * La plus grande coordonnée E de Suisse.
     */
    public final static double MAX_E = 2_834_000;

    /**
     * La plus petite coordonnée N ( c-à-d coordonnée Nord) de Suisse.
     */
    public final static double MIN_N = 1_075_000;

    /**
     * La plus grande coordonnée N de Suisse.
     */
    public final static double MAX_N = 1_296_000;

    /**
     * La largeur de la Suisse (en mètres).
     */
    public final static double WIDTH = MAX_E - MIN_E;

    /**
     * La hauteur de la Suisse (en mètres).
     */
    public final static double HEIGHT = MAX_N - MIN_N;

    /**
     * Retourne vrai ssi les coordonées E et N données sont dans les limites de
     * la Suisse.
     *
     * @param e la coordonnée E
     * @param n la coordonnée N
     *
     * @return vrai ssi les coordonées E et N données sont dans les limites de
     * la Suisse
     */
    public static boolean containsEN(double e, double n) {
        return ((MIN_E <= e) && (e <= MAX_E) && (MIN_N <= n) && (n <= MAX_N));
    }
}
