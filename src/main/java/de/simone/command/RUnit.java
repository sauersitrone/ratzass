package de.simone.command;

import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitCommandType;
import bwapi.UnitType;

/**
 * represent a starcraft unit in the planning model
 */
public class RUnit implements Comparable<RUnit> {


    public boolean isEnemy = false;
    public boolean isAlive = true;
    public int unitID = -1;
    public String squadID = "";
    public UnitType unitType = UnitType.None;
    public UnitCommandType currentCommand = UnitCommandType.Unknown;
    public Position position = new Position(0, 0);

    public RUnit(Unit unit) {
        this(unit.getID(), unit.getType());
        this.position = unit.getPosition();
    }

    public RUnit(int unitID, UnitType unitType) {
        this.unitID = unitID;
        this.unitType = unitType;
    }
    
	public static double distance(double x1, double y1, double x2, double y2) {
		final double dx = x1 - x2;
		final double dy = y1 - y2;
		return Math.sqrt(dx*dx + dy*dy);
	}
    @Override
    public int compareTo(RUnit other) {
        return Integer.compare(this.unitID, other.unitID);
    }
}
