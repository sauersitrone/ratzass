package de.simone.btree;

import java.util.Optional;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import de.simone.RBWListener;
import de.simone.command.BuildOrder;
import de.simone.command.LogisticCenter;
import de.simone.command.StarCraftConstants.BuildActionName;
import de.simone.command.StarCraftConstants.OrderStatus;

public class CheckPendingActionTask extends RTask {

    @TaskAttribute(required = true)
    public BuildActionName action;

    @Override
    public Status execute() {
        Status status = Status.FAILED;

        // is there a gathering action?
        Optional<BuildOrder> optional = LogisticCenter.getInstance().buildOrders.stream()
                .filter(ba -> ba.action == action && ba.status == OrderStatus.Pending)
                .findFirst();

        if (optional.isPresent()) {
            // if yes, check if is there enough resources to complete the action.
            BuildOrder buildOrder = optional.get();
            if (RBWListener.currentMinerals >= buildOrder.quantity) {
                buildOrder.status = OrderStatus.Completed;
                return Status.SUCCEEDED;
            } else {
                status = Status.RUNNING;
            }
        }
        return status;
    }
}
