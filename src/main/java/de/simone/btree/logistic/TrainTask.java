package de.simone.btree.logistic;

import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import bwapi.UnitType;
import de.simone.btree.Blackboard;
import de.simone.command.LogisticCenter;
import de.simone.command.UnitsCenter;

public class TrainTask extends LogisticTask {

    @TaskAttribute(required = true)
    public UnitType unitType;

    @TaskAttribute(required = true)
    public int count;

    private String voucher;

    @Override
    public Status execute() {
        int exist = UnitsCenter.getUnitCount(unitType);
        int total = exist + count;
        if (exist >= total)
            return Status.SUCCEEDED;

        if (getStatus() == Status.RUNNING) {
            Status status = getBuildOrderStatus(voucher);
            return status;
        }

        // no previous, create a new build order
        voucher = LogisticCenter.addBuildOrder(unitType, total);

        return Status.RUNNING;
    }

    @Override
    public String toString() {
        String name = super.toString();
        return name + " " + unitType + " count:" + count;
    }

    @Override
    protected Task<Blackboard> copyTo(Task<Blackboard> task) {
        TrainTask trainTask = (TrainTask) task;
        trainTask.unitType = unitType;
        trainTask.count = count;
        return trainTask;
    }
}
