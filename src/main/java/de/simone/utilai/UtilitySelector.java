package de.simone.utilai;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Chooses the best {@link UtilityAction} for a context.
 * <p>
 * Supports inertia (bonus for the previously chosen action, avoids flickering
 * between near-equal actions) and optional weighted-random choice among the top
 * N actions for less predictable behavior.
 * <p>
 * Keep one selector instance per agent, because inertia is per instance.
 *
 * @param <C> context type
 */
public class UtilitySelector<C> {

    private final List<UtilityAction<C>> actions = new ArrayList<>();
    private final Random random;
    private double inertia = 0.0;
    private int topN = 1;
    private double minScore = 0.0;
    private UtilityAction<C> current;

    public UtilitySelector() {
        this(new Random());
    }

    public UtilitySelector(Random random) {
        this.random = random;
    }

    public UtilitySelector<C> add(UtilityAction<C> action) {
        actions.add(action);
        return this;
    }

    /** Extra score added to the currently running action. */
    public UtilitySelector<C> inertia(double inertia) {
        this.inertia = inertia;
        return this;
    }

    /** Pick randomly (weighted by score) among the best n actions. 1 = always best. */
    public UtilitySelector<C> topN(int topN) {
        this.topN = Math.max(1, topN);
        return this;
    }

    /** Actions scoring at or below this value are never chosen. */
    public UtilitySelector<C> minScore(double minScore) {
        this.minScore = minScore;
        return this;
    }

    public Optional<UtilityAction<C>> getCurrent() {
        return Optional.ofNullable(current);
    }

    /**
     * Scores all actions and returns the chosen one without executing it.
     * @param context - the context
     * @return the action
     */
    public Optional<UtilityAction<C>> select(C context) {
        List<Scored<C>> scored = new ArrayList<>();
        for (UtilityAction<C> a : actions) {
            double s = a.score(context);
            if (a == current && s > 0) {
                s += inertia;
            }
            if (s > minScore) {
                scored.add(new Scored<>(a, s));
            }
        }
        if (scored.isEmpty()) {
            current = null;
            return Optional.empty();
        }
        scored.sort(Comparator.comparingDouble((Scored<C> s) -> s.score).reversed());
        List<Scored<C>> candidates = scored.subList(0, Math.min(topN, scored.size()));
        current = pick(candidates);
        return Optional.of(current);
    }

    /** Selects and executes the best action. */
    public Optional<UtilityAction<C>> run(C context) {
        Optional<UtilityAction<C>> chosen = select(context);
        chosen.ifPresent(a -> a.execute(context));
        return chosen;
    }

    private UtilityAction<C> pick(List<Scored<C>> candidates) {
        if (candidates.size() == 1) {
            return candidates.get(0).action;
        }
        double total = candidates.stream().mapToDouble(s -> s.score).sum();
        double r = random.nextDouble() * total;
        for (Scored<C> s : candidates) {
            r -= s.score;
            if (r <= 0) {
                return s.action;
            }
        }
        return candidates.get(0).action;
    }

    private record Scored<C>(UtilityAction<C> action, double score) {
    }
}
