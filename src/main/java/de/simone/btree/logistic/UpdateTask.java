package de.simone.btree.logistic;

import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import bwapi.UpgradeType;
import de.simone.btree.Blackboard;
import de.simone.command.LogisticCenter;

public class UpdateTask extends LogisticTask {

    @TaskAttribute(required = true)
    public UpgradeType upgradeType;

    @TaskAttribute(required = true)
    public int count;

    private String voucher;

    @Override
    public Status execute() {
        if (getStatus() == Status.RUNNING) {
            Status status = getBuildOrderStatus(voucher);
            return status;
        }

        // no previous, create a new build order
        voucher = LogisticCenter.addBuildOrder(upgradeType, count);

        return Status.RUNNING;
    }

    @Override
    public String toString() {
        String name = super.toString();
        return name + " " + upgradeType + " count:" + count;
    }

    @Override
    protected Task<Blackboard> copyTo(Task<Blackboard> task) {
        UpdateTask updateTask = (UpdateTask) task;
        updateTask.upgradeType = upgradeType;
        updateTask.count = count;
        return updateTask;
    }
}
