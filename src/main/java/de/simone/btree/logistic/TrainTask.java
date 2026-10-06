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
 * Task for build/training/upgrade/tech units. the general fields are
 * self-explanatory except for 'increase' and 'total'.
 * - increase: the number of additional units to produce beyond the current
 * count.
 * - total: the total number of units desired.
 * The usage depend of your preferences. i prefer increase if i what to specify
 * "what i exactly want to produce". for total, i use it when i want to maintain
 * a specific number of units consistently.
 * e.g:
 * i what to train 1 Terran_SCV but i already have 1.
 * - train unitType:"Terran_SCV" increase:1 -> total on the ground: 2
 * - train unitType:"Terran_SCV" total:1 -> total on the ground: 1
 */
public class TrainTask extends LogisticTask {

    @TaskAttribute
    public UnitType unitType;

    @TaskAttribute
    public UpgradeType upgradeType;

    @TaskAttribute
    public TechType techType;

    @TaskAttribute
    public int increase;

    @TaskAttribute
    public int total;

    private String voucher;

    @Override
    public void start() {
        if (unitType == null && upgradeType == null && techType == null)
            throw new IllegalArgumentException("At least one of unitType, upgradeType, or techType must be specified.");

        if (increase == 0 && total == 0)
            throw new IllegalArgumentException("Either increase or total must be specified.");

        super.start();
    }

    @Override
    public Status execute() {
        int exist = UnitsCenter.getUnitCount(unitType);

        // mantain the total number of units specified
        int toProduction = total;
        if (total > 0 && exist >= total) {
            return Status.SUCCEEDED;
        }

        // increase the total of units
        if (increase > 0) {
            toProduction = exist + increase;
            if (exist >= toProduction)
                return Status.SUCCEEDED;
        }

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

        if (total > 0)
            name += " Total: " + total;

        if (increase > 0)
            name += " Increase: " + increase;

        return name;
    }

    @Override
    protected Task<Blackboard> copyTo(Task<Blackboard> task) {
        TrainTask trainTask = (TrainTask) task;
        trainTask.unitType = unitType;
        trainTask.upgradeType = upgradeType;
        trainTask.techType = techType;
        trainTask.increase = increase;
        return trainTask;
    }
}
