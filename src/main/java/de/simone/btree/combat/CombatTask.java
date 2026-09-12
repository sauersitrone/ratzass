package de.simone.btree.combat;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import bwapi.Position;
import bwapi.UnitCommandType;
import de.simone.command.CombatCenter;
import de.simone.command.CombatCenter.NamedArea;
import de.simone.command.CommandQueue;
import de.simone.command.Squad;

public abstract class CombatTask extends LeafTask<Squad> {

    // i need some how fire an event to the GUI that this task is being executed, so
    // that the GUI can highlight it
    public Task<Squad> getControl() {
        return control;
    }

    protected Status performPositionCommand(UnitCommandType commandType, NamedArea namedArea) {
        Squad squad = getObject();
        Position position = CombatCenter.getArea(squad, namedArea);
        if (position == null) {
            return Status.FAILED;
        }

        CommandQueue.addCommand(commandType, squad, position);
        return Status.SUCCEEDED;
    }

    protected void sendCommunication(String text) {
        Squad squad = getObject();
        CombatCenter.sendCommunication(squad, text);
    }

    @Override
    protected Task<Squad> copyTo(Task<Squad> task) {
        return task;
    }

    @Override
    public String toString() {
        String name = getClass().getSimpleName();
        name = name.replace("Task", "");
        name = name.replace("Condition", "");
        return name;
    }
}
