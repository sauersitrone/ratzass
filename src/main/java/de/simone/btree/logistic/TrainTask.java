package de.simone.btree.logistic;

import java.util.List;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import bwapi.UnitType;
import de.simone.command.BuildOrder;
import de.simone.command.LogisticCenter;

public class TrainTask extends LogisticTask {

    @TaskAttribute(required = true)
    public UnitType unitType;

    @TaskAttribute(required = true)
    public int count;

    @Override
    public Status execute() {
        String key = unitType + "-" + count;

        if (getStatus() == Status.RUNNING) {
            Status status= getBuildOrderStatus(key);
            System.out.println(toString() + " "+ status);
            return status;
        }

        // no previous, create a new build order
        List<BuildOrder> orders = LogisticCenter.addBuildOrder(unitType, count);
        getObject().orders.put(key, orders);

        return Status.RUNNING;
    }

    @Override
    public String toString() {
        String name = super.toString();
        return name + " " + unitType + " count:" + count;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        TrainTask task = (TrainTask) obj;
        return toString().equals(task.toString());
    }
}
