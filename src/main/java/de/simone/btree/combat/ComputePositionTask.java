package de.simone.btree.combat;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import de.simone.command.CombatCenter;
import de.simone.command.CombatCenter.NamedArea;
import de.simone.command.Squad;

public class ComputePositionTask extends CombatTask {

    @TaskAttribute(required = true)
    public NamedArea area;

    @Override
    public Status execute() {
        Squad squad = getObject();
        boolean success = CombatCenter.computePosition(squad, area);
        if (!success) {
            sendCommunication("Whats wrong with our " + area + " position ??. We needed it !!");
            return Status.FAILED;
        }

        return Status.SUCCEEDED;
    }
}
