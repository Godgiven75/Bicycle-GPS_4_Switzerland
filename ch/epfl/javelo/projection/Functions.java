package epfl.javelo.projection;

import epfl.javelo.Math2;

import java.util.function.DoubleUnaryOperator;

public final class Functions {
    private Functions() {}



    public static DoubleUnaryOperator constant(double y ) {
        return new Constant(y);
    }

    public static DoubleUnaryOperator sampled(float[] samples, double xMax) {
        return new Sampled(samples, xMax);
    }

    private record Constant(double constant) implements DoubleUnaryOperator {
        @Override
        public double applyAsDouble(double constant) {
            return constant;
        }
    }

    private record Sampled(float[] samples, double xMax) implements DoubleUnaryOperator {
        //Je me demande s'il n'y a pas une meilleure façon d'écrire ce qui suit.
        @Override
        public double applyAsDouble(double preImage) {
            if (preImage < 0) return samples[0];
            if (preImage > xMax) return samples[samples.length - 1];

            double step = xMax / samples.length;
            for (int i = 0; i < samples.length; ++i) {

                double y0 = samples[i];
                double y1 = samples[i + 1];

                if (i * step < preImage && preImage < (i + 1) * step) {
                    return Math2.interpolate(y0, y1, preImage);
                }
            }
            return 0;
        }
    }
}
