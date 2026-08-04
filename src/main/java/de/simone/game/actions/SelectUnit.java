package de.simone.game.actions;

import bwapi.UnitType;

public class SelectUnit extends StarCraftAction {

    public SelectUnit(UnitType unitType) {
        this.unitType = unitType;
        this.unit = RUnits.getUnit(unitType);
    }

    public SelectUnit(RUnit unit, RUnit targetUnit) {
        this.unit = unit;
        this.targetUnit = targetUnit;
    }

    @Override
    public void execute() {
        setSuccess(true);
    }

    @Override
    public RResources requiredResources() {
        return getRequiredUnit();
    }

    @Override
    public RResources producedResources() {
        RResources resources = new RResources();
        resources.units.put(unit.unitType, 1);
        return resources;
    }

}
