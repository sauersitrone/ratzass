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

    private String remitent;
    private int voucher;

    @Override
    public Status execute() {
        LogisticCenter logisticCenter = LogisticCenter.getInstance();

        if (getStatus() == Status.RUNNING) {
            boolean ready = logisticCenter.areMyOrdersReady(remitent, voucher);
            return ready ? Status.SUCCEEDED : Status.RUNNING;
        }

        set an id to the leaf so the task can check where he is now working on 

        // no previous, create a new build order
        BuildOrder order = new BuildOrder(super.getName(), unitType, count);
        this.remitent = order.remitent;
        this.voucher = order.voucher;
        logisticCenter.addBuildOrder(order);

        return Status.RUNNING;
    }

    @Override
    public String getName() {
        String name = super.getName();
        return name + " " + unitType + " count:" + count;
    }
}
