package de.simone.btree;

import java.util.List;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import de.simone.StarCraftException;
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
    Status getBuildOrderStatus(String key) {
        List<BuildOrder> orders = getObject().orders.get(key);
        if (orders == null)
            throw new StarCraftException("No Build order for key " + key + " found.");

        int completed = (int) orders.stream().filter(o -> o.status == OrderStatus.Completed).count();
        if (completed != orders.size())
            return Status.RUNNING;

        if (completed == orders.size())
            return Status.SUCCEEDED;

        int error = (int) orders.stream().filter(o -> o.status == OrderStatus.Error).count();
        if (error > 0)
            return Status.FAILED;

        return Status.FAILED;
    }
}
