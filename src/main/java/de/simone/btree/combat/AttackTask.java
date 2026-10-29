package de.simone.btree.combat;

import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import bwapi.Order;
import de.simone.command.CombatCenter;
import de.simone.command.CombatCenter.RequestName;
import de.simone.command.CombatRequest;
import de.simone.command.Squad;

public class AttackTask extends CombatTask {

    @TaskAttribute(required = true)
    public RequestName to;

    @Override
    public void start() {
        super.start();
        CombatRequest request = CombatCenter.getRequest(to);
        getObject().attack(request.position);
    }

    @Override
    public Status execute() {
        if(!isTimeOut())
            return Status.RUNNING;

        return getObject().getSquadOrder() == Order.AttackMove ? Status.RUNNING : Status.SUCCEEDED;
    }

    @Override
    protected Task<Squad> copyTo(Task<Squad> task) {
        AttackTask copy = (AttackTask) task;
        copy.to = to;
        return copy;
    }

    @Override
    public String toString() {
        return super.toString() + " to: " + to;
    }
}
