package de.simone.btree.combat;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import bwapi.Position;
import de.simone.command.CombatCenter;
import de.simone.command.Squad;

public class MoveTask extends CombatTask {

    @TaskAttribute(required = true)
    public String position;

    @Override
    public Status execute() {
        Squad squad = getObject();
        Position area = CombatCenter.getInstance().getArea(position);
        squad.move(area);
        return Status.SUCCEEDED;
    }
}
