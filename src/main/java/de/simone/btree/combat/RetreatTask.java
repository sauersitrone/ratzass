package de.simone.btree.combat;

import bwapi.Order;
import de.simone.command.Squad;

public class RetreatTask extends CombatTask {

    @Override
    public void start() {
        super.start();
        Squad squad = getObject();
        squad.retreat();
    }

    @Override
    public Status execute() {
        if(!isTimeOut())
            return Status.RUNNING;
        System.out.println("RetreatTask.execute() " + getObject().getSquadOrder());
        return getObject().getSquadOrder() == Order.Move ? Status.RUNNING : Status.SUCCEEDED;
    }
}
