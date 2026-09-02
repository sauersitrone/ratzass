package de.simone.command;

import bwapi.UnitType;
import de.simone.command.StarCraftConstants.OrderStatus;

public class BuildAction {
    public BuildOrder remittent;
    public StarCraftConstants.BuildActionName action;
    public UnitType unitType = UnitType.None;
    public int quantity;
    public OrderStatus status = OrderStatus.Pending;
    public String message = "";

    public BuildAction(BuildOrder remittent, StarCraftConstants.BuildActionName action, UnitType unitType, int quantity) {
        this.remittent = remittent;
        this.action = action;
        this.unitType = unitType;
        this.quantity = quantity;
    }
}
