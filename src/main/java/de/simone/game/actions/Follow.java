package de.simone.game.actions;

import bwapi.Unit;
import de.simone.Ratzass;

public class Follow extends StarCraftAction {

    public Follow(RUnit unit, RUnit targetUnit) {
        this.unit = unit;
        this.targetUnit = targetUnit;
    }

    @Override
    public void execute() {
        Unit target = Ratzass.bwClient.getGame().getUnit(targetUnit.unitID);
        Unit unit = Ratzass.bwClient.getGame().getUnit(this.unit.unitID);
        boolean status = unit.follow(target);
        setSuccess(status);
    }

    @Override
    public RResources requiredResources() {
        return getRequiredUnit() ;
    }

    @Override
    public RResources producedResources() {
        throw null;
    }
}
