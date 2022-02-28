package epfl.javelo;

/**
 * Classe utilitaire, finale et non-instanciable, offrant une méthode de validation d'argument
 */
public final class Preconditions {
    private Preconditions() {}

    /**
     * Lance une IllegalArgumentException si l'argument est faux, et ne fait rien sinon
     * @param shouldBeTrue argument
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) throw new IllegalArgumentException();
    }
}

