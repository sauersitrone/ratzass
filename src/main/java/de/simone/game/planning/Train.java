package de.simone.game.planning;

import java.util.List;

import bwapi.Unit;
import bwapi.UnitType;
import de.simone.Ratzass;
import de.simone.game.actions.StarCraftAction;

public class Train extends StarCraftAction {

    public Train(UnitType unitType) {
        this.unitType = unitType;
    }

    public Train(int unitID) {
        this.unitID = unitID;
    }

    @Override
    public void execute() {
        Unit unit = Ratzass.bwClient.getGame().getUnit(unitID);
        boolean status = unit.train(unitType);
        setSuccess(status);
    }
}
