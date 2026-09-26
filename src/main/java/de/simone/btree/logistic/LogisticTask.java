package de.simone.btree.logistic;

import java.util.List;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import de.simone.StarCraftException;
import de.simone.btree.Blackboard;
import de.simone.command.BuildOrder;
import de.simone.command.LogisticCenter;
import de.simone.command.StarCraftConstants.OrderStatus;

public abstract class LogisticTask extends LeafTask<Blackboard> {

    // that the GUI can highlight it
    public Task<Blackboard> getControl() {
        return control;
    }

    @Override
    public String toString() {
        String name = getClass().getSimpleName();
        name = name.replace("Task", "");
        name = name.replace("Condition", "");
        return name;
    }

    /**
     * Get the status of the build order associated with the given key.
     * - RUNNING if not all orders are completed or the order is marked as a
     * voucher.
     * - SUCCEEDED if all are completed.
     * - FAILED if any order has an error.
     * 
     * @param key - the key
     * @return the status
     */
    Status getBuildOrderStatus(String key) {
        List<BuildOrder> orders = LogisticCenter.getBuildOrders(key);
        if (orders == null)
            throw new StarCraftException("No Build order for key " + key + " found.");

        if (orders.isEmpty())
            return Status.RUNNING;

        int completed = (int) orders.stream().filter(o -> o.getStatus() == OrderStatus.Completed).count();
        if (completed != orders.size())
            return Status.RUNNING;

        if (completed == orders.size())
            return Status.SUCCEEDED;

        int error = (int) orders.stream().filter(o -> o.getStatus() == OrderStatus.Error).count();
        if (error > 0)
            return Status.FAILED;

        return Status.FAILED;
    }
}
