package de.simone.test.timefold;

import ai.timefold.solver.core.api.domain.common.PlanningId;
import bwapi.UnitType;
import de.simone.game.actions.RUnit;
import de.simone.game.actions.RUnits;
import de.simone.game.actions.RResource;
import de.simone.game.actions.RResources;

/**
 * The Abstract Superclass for all actions which can be performed in StarCraft.
 */
public abstract class StarCraftAction {

    public enum Status {
        PENDING,
        ABORTED,
        SUCCESS,
        FAILURE
    }

    @PlanningId
    public String actionId;

    public RResource resource;
    public UnitType unitType;
    public RUnit unit;
    public RUnit targetUnit;
    public Status status = Status.PENDING;

    synchronized void setSuccess(boolean status) {
        if (status) {
            this.status = Status.SUCCESS;
        } else {
            this.status = Status.FAILURE;
        }
    }

    /**
     * Returns the required units for the action. If the unit already exists then no
     * requirements are needed. If the unit does not exist then we need to build it.
     * 
     * @return the unit
     */
    RResources getRequiredUnit() {
        RUnit unit = RUnits.getUnit(unitType);
        // If the unit exists then is no requeriments
        if (unit != null) {
            return null;
        } else {
            // ifn the unit does not exist then we need to build it
            RResources resources = new RResources();
            resources.units.put(unitType, 1);
            return resources;
        }
    }

    /**
     * Executes the action. the action is executed in the game and the status is set
     * to either SUCCESS or FAILURE depending on the result of the execution.
     */
    public abstract void execute();

    /**
     * in planning phase, this method returns the required price for the action. The
     * price is represented as a Resources object which contains the required amount
     * of gas and minerals.
     * 
     * @return - the resources
     */
    public abstract RResources requiredResources();

    public abstract RResources producedResources();

}
