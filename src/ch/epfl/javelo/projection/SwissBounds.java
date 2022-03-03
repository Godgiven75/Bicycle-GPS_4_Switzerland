package ch.epfl.javelo.projection;

/**
 * Classe finale et non-instanciable contenant des constantes et des méthodes liées aux limites de la Suisse
 */
public final class SwissBounds {
    private SwissBounds() {}


    /**
     * La plus petite coordonnée E (c-à-d coordonnée est) de Suisse
     */
    public final static double MIN_E = 2_485_000;

    /**
     * La plus grande coordonnée E de Suisse
     */
    public final static double MAX_E = 2_834_000;

    /**
     * La plus petite coordonnée N ( c-à-d coordonnée nord) de Suisse
     */
    public final static double MIN_N = 1_075_000;

    /**
     * La plus grande coordonnée N de Suisse
     */
    public final static double MAX_N = 1_296_000;

    /**
     * La largeur de la Suisse (en mètres)
     */
    public final static double WIDTH = MAX_E - MIN_E;

    /**
     * La hauteur de la Suisse (en mètres)
     */
    public final static double HEIGHT = MAX_N - MIN_N;

    /**
     * Retourne vrai si et seulement si les coordonées E et N données sont dans les limites de la Suisse
     * @param e coordonnée E
     * @param n coordonnée N
     * @return vrai si et seulement si les coordonées E et N données sont dans les limites de la Suisse
     */
    public static boolean containsEN(double e, double n) {
        return ((MIN_E <= e) && (e <= MAX_E) && (MIN_N <= n) && (n <= MAX_N));
    }
}
