package de.simone.command;

import bwapi.UnitType;
import de.simone.command.BuildOrder.BuildActionName;
import de.simone.command.BuildOrder.BuildStatus;

public class BuildAction {
    public BuildOrder remittent;
    public BuildActionName action;
    public UnitType unitType;
    public int quantity;
    public BuildStatus status = BuildStatus.PENDING;

    public BuildAction(BuildOrder remittent, BuildActionName action, UnitType unitType, int quantity) {
        this.remittent = remittent;
        this.action = action;
        this.unitType = unitType;
        this.quantity = quantity;
    }
}
