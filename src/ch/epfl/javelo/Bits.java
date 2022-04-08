package ch.epfl.javelo;

/**
 *  Classe non-instanciable et finale permettant d'extraire une séquence de bit
 *  d'un vecteur de 32 bits.
 *
 * @author Tanguy Dieudonné (326618)
 * @author Nathanaël Girod (329987)
 */
public final class Bits {
    private Bits() {}

    /**
     * Retourne la séquence de bits du vecteur value commençant au bit d'index
     * start (0 <= start <= 30) de longueur length, extraite de manière signée
     *
     * @param value le vecteur de 32 bits
     * @param start l'index de départ
     * @param length la longeur de la séquence
     * @throws IllegalArgumentException si la plage de bits décrite par les
     * arguments start et length n'est pas incluse dans l'intervalle allant de 0 à 31
     *
     * @return la séquence de bits du vecteur value commençant au bit d'index
     * start de longueur length, extraite de manière signée
     */
    public static int extractSigned (int value, int start, int length) {
        Preconditions.checkArgument(isBitRangeValid(start, length));
        int rangeSize = start + length;
        int leftShift = value << Integer.SIZE - rangeSize;
        return leftShift >> Integer.SIZE - length;
    }

    /**
     * Retourne la séquence de bits du vecteur value commençant au bit d'index
     * start (0 <= start <= 30) de longueur length, extraite de manière non-signée.
     *
     * @param value le vecteur de 32 bits
     * @param start l'index de départ
     * @param length la longeur de la séquence
     * @throws IllegalArgumentException si la plage de bits décrite par les
     * arguments start et length n'est pas incluse dans l'intervalle allant de 0 à 31,
     * et également si la longueur vaut 32
     *
     * @return la séquence de bits du vecteur value commençant au bit d'index
     * start de longueur length, extraite de manière non-signée
     */
    public static int extractUnsigned(int value, int start, int length) {
        Preconditions.checkArgument(isBitRangeValid(start, length));
        Preconditions.checkArgument(length < Integer.SIZE);
        int rangeSize = start + length;
        int leftShift = value <<  Integer.SIZE - rangeSize;
        return leftShift >>> Integer.SIZE - length;
    }


    private static boolean isBitRangeValid(int start, int length) {
        int rangeSize = start + length;
        return 0 <= start && length >= 1 && rangeSize <= Integer.SIZE;
    }
}