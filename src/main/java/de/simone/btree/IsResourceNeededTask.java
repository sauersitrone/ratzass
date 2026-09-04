package de.simone.btree;

import de.simone.RBWListener;
import de.simone.command.StarCraftConstants;

public class IsResourceNeededTask extends RTask {

    @Override
    public Status execute() {
        if (RBWListener.currentSupplyTotal - RBWListener.currentSupplyUsed < StarCraftConstants.TERRAN_MIN_SUPPLY) {
            return Status.SUCCEEDED;
        }

        return Status.FAILED;
    }
}
