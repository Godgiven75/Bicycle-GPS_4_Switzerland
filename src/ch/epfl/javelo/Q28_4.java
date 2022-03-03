package ch.epfl.javelo;

/**
 * Classe finale et non-instanciable permettant de convertir des nombres entre la représentation Q28_4 et d'autres représentations
 */
public final class Q28_4 {
    private Q28_4() {}

    /**
     * Retourne la valeur Q28.4 correspondant à l'entier donné
     * @param i entier
     * @return a valeur Q28.4 correspondant à l'entier donné
     */
    public static int ofInt(int i) {
        return i << 4;
    }

    /**
     * Retourne la valeur de type double égale à la valeur Q28.4 donnée
     * @param q28_4 valeur donnée
     * @return la valeur de type double égale à la valeur Q28.4 donnée
     */

    public static double asDouble(int q28_4) {
        return Math.scalb(q28_4, -4);
    }

    /**
     * Retourne la valeur de type float égale à la valeur Q28.4 donnée
     * @param q28_4 valeur donnée
     * @return la valeur de type float égale à la valeur Q28.4 donnée
     */
    public static float asFloat(int q28_4) {
        return Math.scalb(q28_4, -4);
    }
}
