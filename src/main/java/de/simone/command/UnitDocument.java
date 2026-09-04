package de.simone.command;

import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitCommandType;
import bwapi.UnitType;
import de.simone.RBWListener;

public class UnitDocument implements Comparable<UnitDocument> {
    public boolean isEnemy = false;
    public boolean isAlive = true;
    public int unitID = -1;
    public int targetID = -1;
    public String squadID = "";
    public UnitType unitType = UnitType.None;
    public UnitCommandType currentCommand = UnitCommandType.None;
    public Position position = new Position(0, 0);

    public UnitDocument(Unit unit) {
        this.unitID = unit.getID();
        this.unitType = unit.getType();
        this.position = unit.getPosition();
        this.isEnemy = RBWListener.game.self().isEnemy(unit.getPlayer());
    }

    @Override
    public int compareTo(UnitDocument other) {
        return Integer.compare(this.unitID, other.unitID);
    }
}
