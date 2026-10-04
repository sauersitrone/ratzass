package de.simone.btree.logistic;

import com.badlogic.gdx.ai.btree.Task;

import de.simone.btree.Blackboard;

/**
 * This task is used for debugging purposes. you can use it to test the
 * behavior. put your interuption points in the code and run the behavior
 * tree and see if it is working correctly. It always returns a status of
 * SUCCEEDED.
 */
public class DebugLogisticTask extends LogisticTask {

    @Override
    public Status execute() {
        return Status.SUCCEEDED;
    }

    @Override
    protected Task<Blackboard> copyTo(Task<Blackboard> task) {
        DebugLogisticTask debugT = (DebugLogisticTask) task;
        return debugT;
    }
}
