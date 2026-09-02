package de.simone.btree;

import bwapi.UnitType;
import de.simone.RBWListener;
import de.simone.command.BuildOrder;
import de.simone.command.LogisticCenter;
import de.simone.command.StarCraftConstants;
import de.simone.command.StarCraftConstants.OrderPriority;

public class EnsureSupplyTask extends RTask {

    @Override
    public Status execute() {
        if (RBWListener.currentSupplyTotal - RBWListener.currentSupplyUsed < StarCraftConstants.TERRAN_MIN_SUPPLY) {
            BuildOrder buildOrder = new BuildOrder(getName(), UnitType.Terran_Supply_Depot, 1);
            buildOrder.priority = OrderPriority.High;
            LogisticCenter.getInstance().addBuildOrder(buildOrder);

            // CommandQueue.getInstance().build(UnitType.Terran_Supply_Depot);
        }

        return Status.SUCCEEDED;
    }
}
