package de.simone.test.timefold.domain;

import ai.timefold.solver.core.api.domain.common.PlanningId;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Room {

    @PlanningId
    private String id;

    private String name;

    public Room() {
        //
    }

    public Room(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
