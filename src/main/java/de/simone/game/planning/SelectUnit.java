package de.simone.game.planning;

import bwapi.UnitType;
import de.simone.game.actions.StarCraftAction;

public class SelectUnit extends StarCraftAction {

    public SelectUnit(UnitType unitType) {
        this.unitType = unitType;
    }

    public SelectUnit(int unitID, int targetUnitID) {
        this.unitID = unitID;
        this.targetUnitID = targetUnitID;
    }

    @Override
    public void execute() {
        setSuccess(true);
    }
}
