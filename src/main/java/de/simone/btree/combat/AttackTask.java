package de.simone.btree.combat;

import java.time.LocalTime;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import bwapi.Order;
import de.simone.command.CombatCenter;
import de.simone.command.CombatCenter.RequestName;
import de.simone.command.CombatRequest;

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
    public String toString() {
        return super.toString() + " to: " + to;
    }
}
