package de.simone.command;

import java.util.ArrayList;
import java.util.List;

import bwapi.TechType;
import bwapi.UnitType;
import bwapi.UpgradeType;
import de.simone.command.StarCraftConstants.PlannedAction;
import de.simone.StarCraftException;
import de.simone.command.StarCraftConstants.OrderPriority;
import de.simone.command.StarCraftConstants.OrderStatus;

public class BuildOrder {
    public OrderPriority priority = OrderPriority.Normal;
    public UnitType unitType = UnitType.None;
    public TechType techType = TechType.None;
    public UpgradeType upgradeType = UpgradeType.None;
    public int quantity;
    private OrderStatus status = OrderStatus.Queued;
    public String message = "";
    public int id = StarCraftConstants.idGenerator++;
    public PlannedAction action;

    public BuildOrder(UnitType unitType, int quantity) {
        this.unitType = unitType;
        this.quantity = quantity;
    }

    public BuildOrder(UpgradeType upgradeType, int quantity) {
        this.upgradeType = upgradeType;
        this.quantity = quantity;
    }

    public BuildOrder(TechType techType) {
        this.techType = techType;
    }

    /**
     * idk how. pubt with method work fine, with exposed variable, dont
     * 
     * @param status
     */
    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    /**
     * 
     * idk how. pubt with method work fine, with exposed variable, dont
     * 
     * @return
     */
    public OrderStatus getStatus() {
        return status;
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
            buildOrder.action = PlannedAction.gather_Mineral;
            BuildOrders.add(buildOrder);
            plan2.removeIf(action -> action.equals("gather-Mineral"));
        }
        if (gasCount > 0) {
            BuildOrder buildOrder = new BuildOrder(UnitType.None, gasCount);
            buildOrder.action = PlannedAction.gather_Gas;
            BuildOrders.add(buildOrder);
            plan2.removeIf(action -> action.equals("gather-Gas"));
        }

        // pack the rest of the actions into BuildAction objects
        for (String action : plan2) {
            String[] action_UnitName = action.split("-");
            BuildOrder buildOrder = null;
            if (PlannedAction.train.toString().equals(action_UnitName[0]))
                buildOrder = new BuildOrder(UnitType.valueOf(action_UnitName[1]), 1);

            if (PlannedAction.build.toString().equals(action_UnitName[0]))
                buildOrder = new BuildOrder(UnitType.valueOf(action_UnitName[1]), 1);

            if (PlannedAction.upgrade.toString().equals(action_UnitName[0]))
                buildOrder = new BuildOrder(UpgradeType.valueOf(action_UnitName[1]), 1);

            if (PlannedAction.research.toString().equals(action_UnitName[0]))
                buildOrder = new BuildOrder(TechType.valueOf(action_UnitName[1]));

            // fail save
            if (buildOrder == null)
                throw new StarCraftException("Unknown action type: " + action_UnitName[0]);

            buildOrder.action = PlannedAction.valueOf(action_UnitName[0]);
            BuildOrders.add(buildOrder);
        }
        return BuildOrders;
    }

    @Override
    public String toString() {
        return action + " unitType=" + unitType + " techType=" + techType + " upgradeType=" + upgradeType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass())
            return false;
        return ((BuildOrder) obj).id == this.id;
    }
}
