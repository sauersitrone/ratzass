package de.simone.test.timefold;

import ai.timefold.solver.core.api.score.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import de.simone.game.actions.RUnit;

public class StarCraftConstraintProvider  implements ConstraintProvider {

    @Override
    public Constraint [] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                // Hard constraints
                unitTypeConstraint(constraintFactory)
        };
    }

    Constraint unitTypeConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(RUnit.class)
                .filter(unit -> unit.unitType == null)
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Unit type must not be null");
    }

}
