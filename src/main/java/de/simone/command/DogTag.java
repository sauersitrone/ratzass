package de.simone.command;

import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitCommandType;
import bwapi.UnitType;
import de.simone.RBWListener;

public class DogTag implements Comparable<DogTag> {
    public Unit unit = null;
    public boolean isEnemy = false;
    public boolean isAlive = true;
    public int targetID = -1;
    public String squadID = "";
    public UnitType unitType = UnitType.None;
    public UnitCommandType currentCommand = UnitCommandType.None;
    public Position position = new Position(0, 0);

    public DogTag(Unit unit) {
        this.unit = unit;
        this.unitType = unit.getType();
        this.position = unit.getPosition();
        this.isEnemy = RBWListener.game.self().isEnemy(unit.getPlayer());
    }

    @Override
    public int compareTo(DogTag other) {
        return Integer.compare(this.unit.getID(), other.unit.getID());
    }
}
