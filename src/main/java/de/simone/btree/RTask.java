package de.simone.btree;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import de.simone.command.BuildOrder;
import de.simone.command.StarCraftConstants.OrderStatus;

public abstract class RTask extends LeafTask<Blackboard> {

    // i need some how fire an event to the GUI that this task is being executed, so
    // that the GUI can highlight it
    public Task<Blackboard> getControl() {
        return control;
    }

    @Override
    protected Task<Blackboard> copyTo(Task<Blackboard> task) {
        return task;
    }

    public String getName() {
        String name = this.getClass().getSimpleName();
        name = name.replace("Task", "");
        name = name.replace("Condition", "");
        return name;
    }

    /**
     * return the status of this node based on the status of the build order. If the
     * build order is null, return FRESH
     * 
     * @param currentBuildOrder
     * @return
     */
    Status getBuildOrderStatus(BuildOrder currentBuildOrder) {
        if (currentBuildOrder != null) {
            if (currentBuildOrder.status == OrderStatus.Pending)
                return Status.RUNNING;

            if (currentBuildOrder.status == OrderStatus.Completed)
                return Status.SUCCEEDED;

            if (currentBuildOrder != null && currentBuildOrder.status == OrderStatus.Error)
                return Status.FAILED;
        }
        return Status.FRESH;
    }
}
