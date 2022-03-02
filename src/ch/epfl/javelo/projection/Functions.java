package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.util.function.DoubleUnaryOperator;

/**
 * Classe publique finale et non instanciable contenant des méthodes permettant de créer des objets représentants des
 * fonctions mathématiques R -> R (c-à-d des réels vers les réels).
 */
public final class Functions {
    private Functions() {}


    /**
     * Retourne une fonction constante, dont la valeur est toujours y
     * @param y valeur (double) de la fonction
     * @return une fonctions constante, dont la valeur est toujotus y
     */
    public static DoubleUnaryOperator constant(double y ) {
        return new Constant(y);
    }

    /**
     * Retourne une fonction obtenue par interpolation linéaire entre les échantillons samples, espacés régulièrement
     * sur l'intervalle allant de 0 à xMax
     * @param samples tableau d'échantillons
     * @param xMax abscisse maximale
     * @return une fonction obtenue par interpolation linéaire entre les échantillons samples, espacés régulièrement
     */
    public static DoubleUnaryOperator sampled(float[] samples, double xMax) {
        Preconditions.checkArgument(samples.length >= 2 && xMax > 0);
        return new Sampled(samples, xMax);
    }

    private record Constant(double constant) implements DoubleUnaryOperator {
        @Override
        public double applyAsDouble(double operand) {
            return constant;
        }
    }

    private record Sampled(float[] samples, double xMax) implements DoubleUnaryOperator {
        //Je me demande s'il n'y a pas une meilleure façon d'écrire ce qui suit.

        @Override
        public double applyAsDouble(double operand) {
            double step = xMax / (double) (samples.length - 1);

            //ce clamp permet de prolonger la fonction en - l'infini et + l'infini (permet d'éviter les erreurs)
            operand = Math2.clamp(0, operand, xMax);

            double q = operand / step;

            int x0 = (int)Math.floor(q);
            int x1 = (int) Math.ceil(q);
            double y0 = samples[x0];
            double y1 = samples[x1];

            return Math2.interpolate(y0, y1, q - x0 );


        }
    }
}