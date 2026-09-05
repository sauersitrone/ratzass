package de.simone.btree;

import java.util.List;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import bwapi.UnitType;
import de.simone.command.BuildOrder;
import de.simone.command.LogisticCenter;

public class TrainTask extends RTask {

    @TaskAttribute(required = true)
    public UnitType unitType;

    @TaskAttribute(required = true)
    public int count;

    @Override
    public Status execute() {
        LogisticCenter logisticCenter = LogisticCenter.getInstance();
        String key = unitType + "-" + count;

        if (getStatus() == Status.RUNNING) {
            // boolean ready = logisticCenter.areMyOrdersReady(unitType, count);
            // return ready ? Status.SUCCEEDED : Status.RUNNING;
            return getBuildOrderStatus(key);
        }

        // no previous, create a new build order
        List<BuildOrder> orders = logisticCenter.addBuildOrder(unitType, count);
        getObject().orders.put(key, orders);

        return Status.RUNNING;
    }

    @Override
    public String getName() {
        String name = super.getName();
        return name + " " + unitType + " count:" + count;
    }
}
