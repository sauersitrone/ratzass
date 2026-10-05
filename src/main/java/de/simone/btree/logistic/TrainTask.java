package de.simone.btree.logistic;

import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import bwapi.TechType;
import bwapi.UnitType;
import bwapi.UpgradeType;
import de.simone.btree.Blackboard;
import de.simone.command.LogisticCenter;
import de.simone.command.UnitsCenter;

/**
 * Task for training units in the logistic system. It handles creating build
 * orders and optionally waits for their completion.
 */
public class TrainTask extends LogisticTask {

    @TaskAttribute
    public UnitType unitType;

    @TaskAttribute
    public UpgradeType upgradeType;

    @TaskAttribute
    public TechType techType;

    @TaskAttribute(required = true)
    public int count;

    @TaskAttribute
    public boolean wait = true;

    private String voucher;

    @Override
    public void start() {
        if (unitType == null && upgradeType == null && techType == null)
            throw new IllegalArgumentException("At least one of unitType, upgradeType, or techType must be specified.");

        super.start();
    }

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
        if (unitType != null)
            voucher = LogisticCenter.addBuildOrder(unitType, total);

        if (upgradeType != null)
            voucher = LogisticCenter.addBuildOrder(upgradeType, total);

        if (techType != null)
            voucher = LogisticCenter.addBuildOrder(techType);

        // no wait
        if (!wait) {
            voucher = null;  
            return Status.SUCCEEDED;
        }

        return Status.RUNNING;
    }

    @Override
    public String toString() {
        String name = super.toString();
        if (upgradeType != null)
            name += " Upgrade: " + upgradeType;

        if (techType != null)
            name += " Tech: " + techType;

        if (unitType != null)
            name += " Unit: " + unitType;

        return name + " count: " + count + " wait: " + wait;
    }

    @Override
    protected Task<Blackboard> copyTo(Task<Blackboard> task) {
        TrainTask trainTask = (TrainTask) task;
        trainTask.unitType = unitType;
        trainTask.upgradeType = upgradeType;
        trainTask.techType = techType;
        trainTask.count = count;
        trainTask.wait = wait;
        return trainTask;
    }
}
