package de.simone.btree.combat;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import de.simone.command.CombatCenter;
import de.simone.command.CombatCenter.RequestName;
import de.simone.command.CombatRequest;
import de.simone.command.Squad;

public class AttackTask extends CombatTask {

    @TaskAttribute(required = true)
    public RequestName to;

    @Override
    public Status execute() {
        Squad squad = getObject();
        CombatRequest request = CombatCenter.getRequest(to);
        squad.attack(request.position);
        return Status.SUCCEEDED;
    }

    @Override
    public String toString() {
        return super.toString() + " to: " + to;
    }
}
