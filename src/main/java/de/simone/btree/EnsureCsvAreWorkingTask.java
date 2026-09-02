package de.simone.btree;

import bwapi.Unit;
import bwapi.UnitType;
import de.simone.command.CommandQueue;
import de.simone.command.CommandQueue.ResourceType;
import de.simone.command.StarCraftConstants;
import de.simone.command.UnitsCenter;

public class EnsureCsvAreWorkingTask extends RTask {

    @Override
    public Status execute() {
        Unit unit = UnitsCenter.getIdleTerranSCV();
        if (unit != null) {
            int gGas = (int) UnitsCenter.getUnits().stream()
                    .filter(u -> u.getType() == UnitType.Terran_SCV && u.isGatheringGas()).count();
            if (gGas < StarCraftConstants.SCV_GATHERING_GAS) {
                CommandQueue.getInstance().gather(ResourceType.Gas);
            }

            int gMinerals = (int) (int) UnitsCenter.getUnits().stream()
                    .filter(u -> u.getType() == UnitType.Terran_SCV && u.isGatheringMinerals()).count();
            if (gMinerals < StarCraftConstants.SCV_GATHERING_MINERALS) {
                CommandQueue.getInstance().gather(ResourceType.Mineral);
            }
        }

        return Status.SUCCEEDED;
    }
}
