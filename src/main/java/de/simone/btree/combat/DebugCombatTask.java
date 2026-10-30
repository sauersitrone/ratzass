package de.simone.btree.combat;

/**
 * This task is used for debugging purposes. you can use it to test the
 * behavior. put your interuption points in the code and run the behavior
 * tree and see if it is working correctly. It always returns a status of
 * SUCCEEDED.
 */
public class DebugCombatTask extends CombatTask {

    @Override
    public Status execute() {
        return Status.SUCCEEDED;
    }
}
