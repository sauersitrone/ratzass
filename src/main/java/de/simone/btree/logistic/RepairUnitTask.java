package de.simone.btree.logistic;

import java.util.List;

import com.badlogic.gdx.ai.btree.Task;

import bwapi.Unit;
import de.simone.btree.Blackboard;
import de.simone.command.CommandQueue;
import de.simone.command.UnitsCenter;

public class RepairUnitTask extends LogisticTask {

    @Override
    public Status execute() {
        List<Unit> units = UnitsCenter.getUnits();
        List<Unit> builds = units.stream().filter(u -> u.getType().isBuilding()).toList();
        List<Unit> damagedBuilds = builds.stream().filter(u -> u.getHitPoints() < u.getType().maxHitPoints()).toList();

        if (!damagedBuilds.isEmpty()) {
            Unit target = damagedBuilds.get(0);
            CommandQueue.repair(target.getID());
        }

        return Status.SUCCEEDED;
    }

    @Override
    protected Task<Blackboard> copyTo(Task<Blackboard> task) {
        RepairUnitTask copy = (RepairUnitTask) task;
        return copy;
    }
}
