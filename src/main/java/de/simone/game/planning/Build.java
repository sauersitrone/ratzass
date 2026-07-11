package de.simone.game.planning;

import bwapi.Unit;
import bwapi.UnitType;
import de.simone.Ratzass;
import de.simone.game.actions.StarCraftAction;

public class Build extends StarCraftAction {

    public Build(UnitType unitType) {
        this.unitType = unitType;
    }

    public Build(int unitID, int targetUnitID) {
        this.unitID = unitID;
        this.targetUnitID = targetUnitID;
    }

    @Override
    public void execute() {
        Unit target = Ratzass.bwClient.getGame().getUnit(targetUnitID);
        Unit unit = Ratzass.bwClient.getGame().getUnit(unitID);
        boolean status = unit.follow(target);
        setSuccess(status);
    }
}
