package de.simone.btree.logistic;

import java.util.List;

import bwapi.Unit;
import bwapi.UnitType;
import de.simone.command.CommandQueue;
import de.simone.command.CommandQueue.ResourceType;
import de.simone.command.StarCraftConstants;
import de.simone.command.UnitsCenter;

public class EnsureAllCSVAreWorkingTask extends LogisticTask {

    @Override
    public Status execute() {
        List<Unit> units = UnitsCenter.getUnits().stream()
                .filter(u -> u.getType() == UnitType.Terran_SCV && u.isIdle()).toList();
        Unit refinery = UnitsCenter.getUnits().stream().filter(u -> u.getType() == UnitType.Terran_Refinery).findFirst()
                .orElse(null);
        for (Unit unit : units) {
            int gGas = (int) UnitsCenter.getUnits().stream()
                    .filter(u -> u.getType() == UnitType.Terran_SCV && u.isGatheringGas()).count();
            if (gGas < StarCraftConstants.SCV_GATHERING_GAS && refinery != null) {
                CommandQueue.gather(ResourceType.Gas);
            }

            int gMinerals = (int) (int) UnitsCenter.getUnits().stream()
                    .filter(u -> u.getType() == UnitType.Terran_SCV && u.isGatheringMinerals()).count();
            if (gMinerals < StarCraftConstants.SCV_GATHERING_MINERALS) {
                CommandQueue.gather(ResourceType.Mineral);
            }
        }
        return Status.SUCCEEDED;
    }
}
