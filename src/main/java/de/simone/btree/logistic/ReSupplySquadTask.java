package de.simone.btree.logistic;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ai.btree.Task;

import bwapi.UnitType;
import de.simone.btree.Blackboard;
import de.simone.command.LogisticCenter;
import de.simone.command.Squad;
import de.simone.command.UnitsCenter;

public class ReSupplySquadTask extends LogisticTask {
    private List<String> vouchers = new ArrayList<>();
    private Squad currentSquad; // only 1 squad at the time

    @Override
    public Status execute() {
        if (getStatus() == Status.RUNNING) {
            currentSquad.recruitMembers();
        } else {
            vouchers.clear();
            List<Squad> squads = UnitsCenter.getSquads();
            for (Squad squad : squads) {
                List<UnitType> requiredUnits = squad.getRequiredUnits();
                if (!requiredUnits.isEmpty()) {
                    currentSquad = squad;
                    requiredUnits.forEach(unitType -> vouchers.add(LogisticCenter.addBuildOrder(unitType, 1)));
                    return Status.RUNNING;
                }
            }
        }

        return currentSquad == null || currentSquad.getRequiredUnits().isEmpty() ? Status.SUCCEEDED : Status.RUNNING;
    }

    @Override
    public String toString() {
        return super.toString() + " squad:" + currentSquad;
    }

    @Override
    protected Task<Blackboard> copyTo(Task<Blackboard> task) {
        ReSupplySquadTask reSupplySquadTask = (ReSupplySquadTask) task;
        reSupplySquadTask.currentSquad = currentSquad;
        reSupplySquadTask.vouchers = new ArrayList<>(vouchers);
        return reSupplySquadTask;
    }
}
