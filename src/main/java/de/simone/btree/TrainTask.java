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

    @Override
    public Status execute() {
        LogisticCenter logisticCenter = LogisticCenter.getInstance();

        if (getStatus() == Status.RUNNING) {
            boolean ready = logisticCenter.areMyOrdersReady(unitType, count);
            return ready ? Status.SUCCEEDED : Status.RUNNING;
        }

        // no previous, create a new build order
        BuildOrder order = new BuildOrder(unitType, count);
        logisticCenter.addBuildOrder(order);

        return Status.RUNNING;
    }

    @Override
    public String getName() {
        String name = super.getName();
        return name + " " + unitType + " count:" + count;
    }
}
