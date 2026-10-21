package de.simone.utilai;

/**
 * Response curve mapping a normalized input (0..1) to a score (0..1).
 */
@FunctionalInterface
public interface Curve {

    double evaluate(double x);

    static double clamp01(double v) {
        return v < 0 ? 0 : (v > 1 ? 1 : v);
    }

    /**
     * Linear curve; output equals input.
     * 
     * @return the curve
     */
    static Curve linear() {
        return x -> clamp01(x);
    }

    /**
     * Inverse curve; output equals 1 minus input.
     * @return the curve
     */
    static Curve inverse() {
        return x -> 1 - clamp01(x);
    }

    /**
     * quadratic curve; output equals input squared.
     * @return the curve
     */
    static Curve quadratic() {
        return x -> {
            double c = clamp01(x);
            return c * c;
        };
    }

    /**
     * Power curve; output equals input raised to the given exponent.
     * 
     * @param exponent - the exponent
     * @return the curve
     */
    static Curve power(double exponent) {
        return x -> Math.pow(clamp01(x), exponent);
    }

    /**
     * S-shaped curve; k ~ 10 is typical, midpoint x0 in 0..1.
     * @param k- the grow rate
     * @param x0 - the midpoint
     * @return the curve
     */
    static Curve logistic(double k, double x0) {
        return x -> clamp01(1.0 / (1.0 + Math.exp(-k * (clamp01(x) - x0))));
    }

    /**
     * return a step curve; output is 1 below the threshold, 0 at or above it.
     * @param threshold - the threshold
     * @return the curve
     */
    static Curve step(double threshold) {
        return x -> x < threshold ? 1 : 0;
    }

    default Curve inverted() {
        return x -> 1 - evaluate(x);
    }
}
