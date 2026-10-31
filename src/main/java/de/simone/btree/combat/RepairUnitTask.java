package de.simone.btree.combat;

import java.util.List;

import bwapi.Unit;
import de.simone.command.CommandQueue;
import de.simone.command.UnitsCenter;

public class RepairUnitTask extends CombatTask {

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
}
