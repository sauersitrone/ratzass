package de.simone.btree.logistic;

import de.simone.RBWListener;
import de.simone.command.StarCraftConstants;

public class IsSupplyNeededCondition extends LogisticTask {

    @Override
    public Status execute() {
        if (RBWListener.currentSupplyTotal - RBWListener.currentSupplyUsed < StarCraftConstants.TERRAN_MIN_SUPPLY) {
            return Status.SUCCEEDED;
        }

        return Status.FAILED;
    }
}
