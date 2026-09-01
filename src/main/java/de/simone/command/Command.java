package de.simone.command;

import bwapi.Position;
import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.UnitCommandType;
import bwapi.UnitType;
import bwapi.UpgradeType;
import de.simone.RBWListener;
import de.simone.command.StarCraftConstants.OrderStatus;

public class Command {

    // unique id for this command
    public int cycle = RBWListener.game.getFrameCount();

    // unit that must this commmand execute
    public int unitId = -1;

    // represents the target unit for the actions that unitId must execute (eg.
    // attack, repair, follow, etc.)
    public int targetId = -1;

    // it is responsability of commandQueue set to false if something whet wrong.
    // see commandQueue.logFail():
    public OrderStatus status = OrderStatus.Pending;

    // optional message to provide more information about the command execution
    public String message = "";

    public UnitCommandType order = UnitCommandType.Unknown;
    public UnitType unitType = UnitType.None;
    public TechType techType = TechType.None;
    public UpgradeType upgradeType = UpgradeType.None;
    public Position position = null;
    public TilePosition tilePosition = null;

    public Command() {
        //
    }

    public Command(UnitCommandType order, UnitType unitType) {
        this.order = order;
        this.unitType = unitType;
    }

    public Command(UnitCommandType order, int unitId, int targetUnitId, Position position) {
        this.order = order;
        this.unitId = unitId;
        this.targetId = targetUnitId;
        this.position = position;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Command other = (Command) obj;
        return cycle == other.cycle;
    }

}
