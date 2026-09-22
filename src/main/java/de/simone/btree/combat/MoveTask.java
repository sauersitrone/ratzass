package de.simone.btree.combat;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import bwapi.UnitType;
import de.simone.command.CombatCenter;
import de.simone.command.CombatCenter.RequestName;
import de.simone.command.CombatRequest;
import de.simone.command.Squad;
import de.simone.command.Squad.SquadStatus;

public class MoveTask extends CombatTask {

    @TaskAttribute(required = true)
    public RequestName to;

    @Override
    public Status execute() {
        Squad squad = getObject();
 
        if (getStatus() == Status.RUNNING)
            return squad.status == SquadStatus.Moving ? Status.RUNNING : Status.SUCCEEDED;

        CombatRequest request = CombatCenter.getRequest(to);
        // Another squad might have already handled the call
        if (request == null)
            return Status.FAILED;

        squad.move(request.position);
        if (to == RequestName.CallSupport)
            sendCommunication("Hold on brothers! we are coming !");

        return Status.SUCCEEDED;
    }

    @Override
    public String toString() {
        return super.toString() + " to: " + to;
    }
}
