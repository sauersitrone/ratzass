package de.simone.btree;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import bwapi.UnitType;
import de.simone.command.BuildOrder;
import de.simone.command.LogisticCenter;

public class TrainTask extends RTask {

    @TaskAttribute(required = true)
    public UnitType unitType;

    @TaskAttribute(required = true)
    public int count;

    private BuildOrder currentBuildOrder;

    @Override
    public Status execute() {
        LogisticCenter logisticCenter = LogisticCenter.getInstance();

        if (getStatus() == Status.RUNNING)
            return getBuildOrderStatus(currentBuildOrder);

        // no previous, create a new build order
        currentBuildOrder = new BuildOrder(super.getName(), unitType, count);
        logisticCenter.addBuildOrder(currentBuildOrder);

        return Status.RUNNING;
    }

    @Override
    public String getName() {
        String name = super.getName();
        return name + " " + unitType + " count:" + count;
    }
}
