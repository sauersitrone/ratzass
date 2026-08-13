package de.simone.command;

import de.simone.btree.RTask;

public interface RBehaviorTreeListener {

    void executingStep(RTask rTask);    
}