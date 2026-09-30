package de.simone.btree.logistic;

import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import bwapi.TechType;
import de.simone.btree.Blackboard;
import de.simone.command.LogisticCenter;

public class ResearchTask extends LogisticTask {

    @TaskAttribute(required = true)
    public TechType techType;

    private String voucher;

    @Override
    public Status execute() {
        if (getStatus() == Status.RUNNING) {
            Status status = getBuildOrderStatus(voucher);
            return status;
        }

        // no previous, create a new build order
        voucher = LogisticCenter.addBuildOrder(techType);

        return Status.RUNNING;
    }

    @Override
    public String toString() {
        String name = super.toString();
        return name + " " + techType;
    }

    @Override
    protected Task<Blackboard> copyTo(Task<Blackboard> task) {
        ResearchTask researchTask = (ResearchTask) task;
        researchTask.techType = techType;
        return researchTask;
    }
}
