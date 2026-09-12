package de.simone.btree.combat;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import bwapi.UnitCommandType;
import de.simone.command.CombatCenter.NamedArea;

public class AttackMoveTask extends CombatTask {

    @TaskAttribute(required = true)
    public NamedArea area;

    @Override
    public Status execute() {
        Status status = performPositionCommand(UnitCommandType.Attack_Move, area);
        if (status == Status.FAILED) 
            return Status.FAILED;
        
        sendCommunication("Attacking area " + area);
        return Status.SUCCEEDED;
    }
}
