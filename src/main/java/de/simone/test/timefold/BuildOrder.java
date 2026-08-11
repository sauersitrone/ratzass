package de.simone.test.timefold;

import java.util.List;

import ai.timefold.solver.core.api.domain.common.PlanningId;
import ai.timefold.solver.core.api.domain.solution.PlanningScore;
import ai.timefold.solver.core.api.domain.solution.PlanningSolution;
import ai.timefold.solver.core.api.domain.solution.ProblemFactCollectionProperty;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.score.HardSoftScore;
import lombok.Getter;

@Getter
@PlanningSolution
public class BuildOrder {

    @PlanningId
    private String id;
    
    @ValueRangeProvider
    @ProblemFactCollectionProperty
    private List<StarCraftAction> actions;
    
    @PlanningScore
    private HardSoftScore score;
    
    public BuildOrder() {
        //
    }

}
