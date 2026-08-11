package de.simone.test.timefold.domain;

import java.util.List;

import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty;
import ai.timefold.solver.core.api.domain.solution.PlanningScore;
import ai.timefold.solver.core.api.domain.solution.PlanningSolution;
import ai.timefold.solver.core.api.domain.solution.ProblemFactCollectionProperty;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.score.HardSoftScore;
import ai.timefold.solver.core.api.solver.SolverStatus;
import lombok.Getter;

@Getter
@PlanningSolution
public class Timetable {

    private String name;

    @ValueRangeProvider
    @ProblemFactCollectionProperty
    private List<Timeslot> timeslots;

    @ValueRangeProvider
    @ProblemFactCollectionProperty
    private List<Room> rooms;
    
    @PlanningEntityCollectionProperty
    private List<Lesson> lessons;

    @PlanningScore
    private HardSoftScore score;

    // Ignored by Timefold, used by the UI to display solve or stop solving button
    private SolverStatus solverStatus;

    // No-arg constructor required for Timefold
    public Timetable() {
        //
    }

    public Timetable(String name, HardSoftScore score, SolverStatus solverStatus) {
        this.name = name;
        this.score = score;
        this.solverStatus = solverStatus;
    }

    public Timetable(String name, List<Timeslot> timeslots, List<Room> rooms, List<Lesson> lessons) {
        this.name = name;
        this.timeslots = timeslots;
        this.rooms = rooms;
        this.lessons = lessons;
    }

    public void setScore(HardSoftScore score) {
        this.score = score;
    }
}
