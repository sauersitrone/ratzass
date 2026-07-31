package de.simone.game.actions;

import java.util.Map;
import java.util.TreeMap;

import bwapi.UnitType;

public class RUnits {

    public static Map<UnitType, Integer> unitCounts = new TreeMap<>();
    public static Map<Integer, RUnit> units = new TreeMap<>();

    /**
     * Get a unit of the specified type from the units map. If there are no units of that type, return null.
     * 
     * @param unitType - the type
     * @return the unit
     */
    public static RUnit getUnit(UnitType unitType) {
        int count = unitCounts.getOrDefault(unitType, 0);
        if (count == 0)
            return null;

        RUnit unit = units.values().stream().filter(u -> u.unitType == unitType).findFirst().get();

        return unit;
    }
}
