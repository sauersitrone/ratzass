package de.simone.command;

import java.util.ArrayList;
import java.util.List;

import bwapi.UnitType;

public class BuildOrder {
    public enum BuildStatus {
        ERROR,
        PENDING,
        COMPLETED
    }

    public enum BuildActionName {
        gather_Mineral,
        gather_Gas,
        train_Unit,
        build_Unit,
    }

    public String remitent;
    public UnitType unitType;
    public int quantity;

    public BuildStatus status;
    public String message;

    private List<BuildAction> buildActions = new ArrayList<>();

    public BuildOrder(String remitent, UnitType unitType, int quantity) {
        this.remitent = remitent;
        this.unitType = unitType;
        this.quantity = quantity;
        this.status = BuildStatus.PENDING;
    }

    public List<BuildAction> getBuildActions() {
        return buildActions;
    }

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
