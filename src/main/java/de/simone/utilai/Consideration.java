package de.simone.utilai;

import java.util.function.ToDoubleFunction;

/**
 * A single factor in a decision. Reads a raw value from the context, normalizes
 * it to 0..1 using [min, max] and maps it through a {@link Curve}.
 *
 * @param <C> context type (e.g. a squad, a blackboard)
 */
public class Consideration<C> {

    private final String name;
    private final ToDoubleFunction<C> input;
    private final double min;
    private final double max;
    private final Curve curve;

    public Consideration(String name, ToDoubleFunction<C> input, double min, double max, Curve curve) {
        if (max == min) {
            throw new IllegalArgumentException("max must differ from min");
        }
        this.name = name;
        this.input = input;
        this.min = min;
        this.max = max;
        this.curve = curve;
    }

    public String getName() {
        return name;
    }

    /** @return score in 0..1 */
    public double score(C context) {
        double normalized = (input.applyAsDouble(context) - min) / (max - min);
        return Curve.clamp01(curve.evaluate(Curve.clamp01(normalized)));
    }
}
