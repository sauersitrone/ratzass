package de.simone.btree.combat;

import de.simone.command.Squad;
import de.simone.command.Squad.SquadStatus;

public class RetreatTask extends CombatTask {

    @Override
    public Status execute() {
        Squad squad = getObject();

        if (getStatus() == Status.RUNNING) {
            return squad.status == SquadStatus.Moving ? Status.RUNNING : Status.SUCCEEDED;
        }
        
        squad.retreat();
        return Status.RUNNING;
    }
}
