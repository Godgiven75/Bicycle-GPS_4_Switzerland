package ch.epfl.javelo;

/**
 * Classe utilitaire, finale et non-instanciable, offrant une méthode de
 * validation d'argument.
 *
 * @author Tanguy Dieudonné (326618)
 * @author Nathanaël Girod (329987)
 */
public final class Preconditions {
    private Preconditions() {}

    /**
     * Lance une IllegalArgumentException si l'argument est faux, et ne fait
     * rien sinon.
     *
     * @param shouldBeTrue argument
     *
     * @throws IllegalArgumentException si l'argument est faux
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) throw new IllegalArgumentException();
    }
}

