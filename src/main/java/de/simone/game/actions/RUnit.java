package de.simone.game.actions;

import bwapi.UnitType;

/**
 * represent a starcraft unit in the planning model
 */
public class RUnit {
    
    public int unitID = -1;
    public UnitType unitType = UnitType.None;

    public RUnit() {
        //
    }

    public RUnit(int unitID, UnitType unitType) {
        this.unitID = unitID;
        this.unitType = unitType;
    }
}
