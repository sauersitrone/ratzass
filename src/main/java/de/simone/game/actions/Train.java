package de.simone.game.actions;

import bwapi.Unit;
import de.simone.Ratzass;

public class Train extends StarCraftAction {

    public Train(int unitID, int targetUnitID) {
        this.unitID = unitID;
        this.targetUnitID = targetUnitID;
    }

    @Override
    public void execute() {
        Unit target = Ratzass.bwClient.getGame().getUnit(targetUnitID);
        Unit unit = Ratzass.bwClient.getGame().getUnit(unitID);
        boolean status = unit.train(target.getType());
        setSuccess(status);
    }
}
