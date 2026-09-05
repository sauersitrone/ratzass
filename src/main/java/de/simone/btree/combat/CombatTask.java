package de.simone.btree.combat;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import de.simone.command.Squad;

public abstract class CombatTask extends LeafTask<Squad> {

    // i need some how fire an event to the GUI that this task is being executed, so
    // that the GUI can highlight it
    public Task<Squad> getControl() {
        return control;
    }

    @Override
    protected Task<Squad> copyTo(Task<Squad> task) {
        return task;
    }

    public String getName() {
        String name = this.getClass().getSimpleName();
        name = name.replace("Task", "");
        name = name.replace("Condition", "");
        return name;
    }

}
