package de.simone.command;

import java.util.ArrayList;
import java.util.List;

import bwapi.UnitType;
import de.simone.RBWListener;
import de.simone.command.StarCraftConstants.BuildActionName;
import de.simone.command.StarCraftConstants.OrderPriority;
import de.simone.command.StarCraftConstants.OrderStatus;

public class BuildOrder {

    // only used for the build order, not for the build actions. should used only by
    // LogisticCenter to sort the build orders by priority
    public OrderPriority priority = OrderPriority.Normal;

    public UnitType unitType;
    public int quantity;
    public OrderStatus status = OrderStatus.Pending;
    public String message = "";
    public int cicle = RBWListener.game.getFrameCount();

    public BuildActionName action;

    public BuildOrder(UnitType unitType, int quantity) {
        this.unitType = unitType;
        this.quantity = quantity;
    }

    /**
     * return the list of BuildOrder based on the provided plan. This
     * method processes the plan to create BuildOrder objects for gathering
     * resources and training/building units. It consolidates multiple gather
     * actions into a single BuildOrder with the correct quantity.
     * 
     * @param plan - the plan
     * @return the orders
     * 
     */
    public static List<BuildOrder> getBuildOrders(List<String> plan) {
        List<String> plan2 = new ArrayList<>(plan);
        List<BuildOrder> BuildOrders = new ArrayList<>();

        // check the gatherTask_mineral and gatherTask_gas actions and convert them to
        // one BuildAction with the correct quantity
        int mineralCount = plan2.stream().filter(action -> action.equals("gather-Mineral")).toList().size();
        mineralCount *= StarCraftConstants.MINERAL_LOAD;

        int gasCount = plan2.stream().filter(action -> action.equals("gather-Gas")).toList().size();
        gasCount *= StarCraftConstants.GAS_LOAD;

        if (mineralCount > 0) {
            BuildOrder buildOrder = new BuildOrder(UnitType.None, mineralCount);
            buildOrder.action = BuildActionName.gather_Mineral;
            BuildOrders.add(buildOrder);
            plan2.removeIf(action -> action.equals("gather-Mineral"));
        }
        if (gasCount > 0) {
            BuildOrder buildOrder = new BuildOrder(UnitType.None, mineralCount);
            buildOrder.action = BuildActionName.gather_Gas;
            BuildOrders.add(buildOrder);
            plan2.removeIf(action -> action.equals("gather-Gas"));
        }

        // pack the rest of the actions into BuildAction objects
        for (String action : plan2) {
            String[] action_UnitName = action.split("-");
            BuildOrder buildOrder = new BuildOrder(UnitType.valueOf(action_UnitName[1]), 1);
            buildOrder.action = BuildActionName.valueOf(action_UnitName[0]);
            BuildOrders.add(buildOrder);
        }
        return BuildOrders;
    }

    @Override
    public String toString() {
        return cicle + ", ut=" + unitType + ", a=" + action + ", q=" + quantity + ", s=" + status
                + ", m=" + message;
    }

}
