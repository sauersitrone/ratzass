package de.simone.btree.combat;

import de.simone.command.CombatCenter;
import de.simone.command.CombatCenter.RequestName;
import de.simone.command.CombatRequest;

public class IsSupportNeededCondition extends CombatTask {

    @Override
    public Status execute() {
        CombatRequest request = CombatCenter.getRequest(RequestName.CallSupport);

        Status status = request != null ? Status.SUCCEEDED : Status.FAILED;

        if (status == Status.SUCCEEDED)
            sendCommunication("Responding to support request from " + request.squadID);

        return status;
    }
}
