package ch.epfl.javelo;

/**
 *  Classe non-instanciable et finale permettant d'extraire une séquence de bit d'un vecteur de 32 bits
 */
public final class Bits {
    private Bits() {}

    /**
     * Retourne la séquence de bits du vecteur value commençant au bit d'index start (0 <= start <= 30) de longueur length, extraite de manière signée
     * @param value vecteur de 32 bits
     * @param start index de départ
     * @param length longeur de la séquence
     * @return la séquence de bits du vecteur value commençant au bit d'index start de longueur length, extraite de manière signée
     */
    public static int extractSigned (int value, int start, int length) {
        int rangeSize = start + length;
        Preconditions.checkArgument(0 <= start && length >= 1 && rangeSize <= Integer.SIZE ); //demander pour la précondition
        int leftShift = value << Integer.SIZE - rangeSize;
        return leftShift >> Integer.SIZE - length;

    }

    /**
     * Retourne la séquence de bits du vecteur value commençant au bit d'index start (0 <= start <= 30) de longueur length, extraite de manière non-signée
     * @param value vecteur de 32 bits
     * @param start index de départ
     * @param length longeur de la séquence
     * @return la séquence de bits du vecteur value commençant au bit d'index start de longueur length, extraite de manière non-signée
     */
    public static int extractUnsigned(int value, int start, int length) {
        int rangeSize = start + length;
        Preconditions.checkArgument(0 <= start && length >= 1 && rangeSize < Integer.SIZE );
        int leftShift = value <<  Integer.SIZE - rangeSize;
        return leftShift >>> Integer.SIZE - length;
    }

}