package de.simone.game.actions;

import java.util.Map;

import bwapi.Unit;
import bwapi.UnitType;
import de.simone.Ratzass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Train extends StarCraftAction {

    public Train() {
        //
    }

    public Train(UnitType unitType) {
        this.unitType = unitType;
    }

    public Train(RUnit unit, RUnit targetUnit) {
        this.unit = unit;
        this.targetUnit = targetUnit;
    }

    
    @Override
    public void execute() {
        Unit unit = Ratzass.bwClient.getGame().getUnit(this.unit.unitID);
        boolean status = unit.train(this.targetUnit.unitType);
        setSuccess(status);
    }

    @Override
    public RResources requiredResources() {
        RResources resources = new RResources();
        RResource gas = new RResource(RResource.Type.GAS, unitType.gasPrice());
        RResource mineral = new RResource(RResource.Type.MINERAL, unitType.mineralPrice());
        RResource supply = new RResource(RResource.Type.SUPPLY, unitType.supplyRequired());
        RResource time = new RResource(RResource.Type.TIME, unitType.buildTime());
        Map<UnitType, Integer> units = unitType.requiredUnits();
        resources.units.putAll(units);
        resources.resources.add(gas);
        resources.resources.add(mineral);
        resources.resources.add(supply);
        resources.resources.add(time);
        return resources;
    }

    @Override
    public RResources producedResources() {
        RResources resources = new RResources();
        resources.units.put(unitType, 1);
        resources.resources.add(resource);
        return resources;
    }
}
