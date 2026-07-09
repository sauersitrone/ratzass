package de.simone.game.actions;

import bwapi.Unit;
import de.simone.Ratzass;

public class Follow extends StarCraftAction {

    public Follow(int unitID, int targetUnitID) {
        this.unitID = unitID;
        this.targetUnitID = targetUnitID;
    }

    @Override
    public void execute() {
        Unit target = Ratzass.bwClient.getGame().getUnit(targetUnitID);
        Unit unit = Ratzass.bwClient.getGame().getUnit(unitID);
        boolean status = unit.follow(target);
        setStatus(status);
    }
}
