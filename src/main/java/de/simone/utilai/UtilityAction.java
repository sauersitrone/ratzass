package de.simone.utilai;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * An action with a list of considerations. Its utility is the product of all
 * consideration scores, corrected by the compensation factor from Dave Mark's
 * approach so that actions with many considerations are not penalized unfairly,
 * then multiplied by a weight.
 *
 * @param <C> context type
 */
public class UtilityAction<C> {

    private final String name;
    private final List<Consideration<C>> considerations = new ArrayList<>();
    private final Consumer<C> executor;
    private double weight = 1.0;

    public UtilityAction(String name, Consumer<C> executor) {
        this.name = name;
        this.executor = executor;
    }

    public UtilityAction<C> add(Consideration<C> consideration) {
        considerations.add(consideration);
        return this;
    }

    public UtilityAction<C> weight(double weight) {
        this.weight = weight;
        return this;
    }

    public String getName() {
        return name;
    }

    public double getWeight() {
        return weight;
    }

    /**
     * return the utility score of this action for the given context. 0 means the
     * action is not applicable.
     * 
     * @param context - the context
     * @return the score
     */
    public double score(C context) {
        if (considerations.isEmpty()) 
            return weight;

        double result = 1.0;
        for (Consideration<C> c : considerations) {
            double s = c.score(context);
            result *= s;
            if (result == 0) {
                return 0;
            }
        }
        double modification = 1.0 - 1.0 / considerations.size();
        result += (1.0 - result) * modification * result;
        return result * weight;
    }

    public void execute(C context) {
        executor.accept(context);
    }

    @Override
    public String toString() {
        return name;
    }
}
