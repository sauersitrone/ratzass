package de.simone.command;

import java.util.ArrayList;
import java.util.List;

import bwapi.UnitType;
import de.simone.command.StarCraftConstants.OrderPriority;
import de.simone.command.StarCraftConstants.OrderStatus;

public class BuildOrder {

    public enum BuildActionName {
        gather_Mineral,
        gather_Gas,
        train,
        build,
    }

    // only used for the build order, not for the build actions. should used only by
    // LogisticCenter to sort the build orders by priority
    public OrderPriority priority = OrderPriority.Normal;

    public String remitent;
    public UnitType unitType;
    public int quantity;
    public OrderStatus status;
    public String message = "";

    private List<BuildAction> buildActions = new ArrayList<>();

    public BuildOrder(String remitent, UnitType unitType, int quantity) {
        this.remitent = remitent;
        this.unitType = unitType;
        this.quantity = quantity;
        this.status = OrderStatus.Pending;
    }

    public List<BuildAction> getBuildActions() {
        return buildActions;
    }

    /**
     * Set the build actions for this build order based on the provided plan. This
     * method processes the plan to create BuildAction objects for gathering
     * resources and training/building units. It consolidates multiple gather
     * actions into a single BuildAction with the correct quantity.
     * 
     * @param plan - the plan
     */
    public void setBuildActions(List<String> plan) {
        List<String> plan2 = new ArrayList<>(plan);

        // check the gatherTask_mineral and gatherTask_gas actions and convert them to
        // one BuildAction with the correct quantity
        int mineralCount = plan2.stream().filter(action -> action.equals("gather-Mineral")).toList().size();
        mineralCount *= StarCraftConstants.MINERAL_LOAD;

        int gasCount = plan2.stream().filter(action -> action.equals("gather-Gas")).toList().size();
        gasCount *= StarCraftConstants.GAS_LOAD;

        if (mineralCount > 0) {
            buildActions.add(
                    new BuildAction(this, BuildActionName.gather_Mineral, null, mineralCount));
            plan2.removeIf(action -> action.equals("gather-Mineral"));
        }
        if (gasCount > 0) {
            buildActions.add(new BuildAction(this, BuildActionName.gather_Gas, null, gasCount));
            plan2.removeIf(action -> action.equals("gather-Gas"));
        }

        // pack the rest of the actions into BuildAction objects
        for (String action : plan2) {
            String[] action_UnitName = action.split("-");
            buildActions.add(
                    new BuildAction(this, BuildActionName.valueOf(action_UnitName[0]),
                            UnitType.valueOf(action_UnitName[1]), 1));
        }
    }

}
