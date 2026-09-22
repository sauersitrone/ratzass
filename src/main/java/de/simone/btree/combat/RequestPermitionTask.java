package de.simone.btree.combat;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import de.simone.command.CombatCenter;
import de.simone.command.CombatCenter.RequestName;
import de.simone.command.Squad;

public class RequestPermitionTask extends CombatTask {

    @TaskAttribute(required = true)
    public RequestName to;

    @Override
    public Status execute() {
        Squad squad = getObject();
        boolean success = CombatCenter.requestPermition(squad, to);
        if (success) {
            sendCommunication("Request for " + to + " granted. Proceed with the operation.");
            return Status.SUCCEEDED;
        }

        return Status.FAILED;
    }
}
