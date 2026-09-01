package de.simone.command;

import bwapi.Position;
import bwapi.TilePosition;
import bwapi.UnitCommandType;
import de.simone.command.StarCraftConstants.OrderPriority;
import de.simone.command.StarCraftConstants.OrderStatus;

public class CombatOrder {

    public enum PositionName {
        Random, HelpCall, LastKnownEnemyPosition, ClosestSquad, RetreatArea, Unknown
    }

    // UnitCommandType examples of terran commands
    // Attack_Move, Attack_Unit, Move, Patrol, Hold_Position, Stop, Follow, Cloak,
    // Decloak, Siege, Unsiege, Lift, Land, Load, Unload, Unload_All,
    // Unload_All_Position, Right_Click_Position, Right_Click_Unit

    public OrderPriority priority = OrderPriority.Normal;
    public String remitent;
    public OrderStatus status;
    public String message;
    public UnitCommandType order = UnitCommandType.Unknown;
    public Position position = null;
    public PositionName positionName = PositionName.Unknown;
    public TilePosition tilePosition = null;

    public CombatOrder(String remitent, UnitCommandType order) {
        this.remitent = remitent;
        this.order = order;
        this.status = OrderStatus.Pending;
    }
}
