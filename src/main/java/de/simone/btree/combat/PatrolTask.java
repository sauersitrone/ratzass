package de.simone.btree.combat;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import bwapi.UnitCommandType;
import de.simone.command.CombatCenter.NamedArea;

public class PatrolTask extends CombatTask {

    @TaskAttribute(required = true)
    public NamedArea area;

    @Override
    public Status execute() {
        Status status = performPositionCommand(UnitCommandType.Right_Click_Position, area);
        if (status == Status.FAILED) 
            return Status.FAILED;
        
        sendCommunication("Patrolling area " + area);
        return Status.SUCCEEDED;
    }
}
