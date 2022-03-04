package ch.epfl.javelo;

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

    private static class Sampled implements DoubleUnaryOperator {

        private float[] samples;
        private double xMax;
        private double step;

        public Sampled(float[] samples, double xMax)
        {
            this.samples = samples.clone();
            this.xMax = xMax;
            step = xMax / (samples.length);

        }

        @Override
        public double applyAsDouble(double operand) {

            double q = operand / step;

            int x0 = (int) Math.floor(q);
            if(x0 < 0) {
                return samples[0];
            }
            if(x0  < samples.length - 1 ) {
                return Math2.interpolate(samples[x0], samples[x0 + 1], q - x0);
            }
            return samples[samples.length - 1];

        }
    }
}