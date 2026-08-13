package de.simone.btree;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import de.simone.command.CommandQueue;

public abstract class RTask extends LeafTask<CommandQueue> {

    // i need some how fire an event to the GUI that this task is being executed, so
    // that the GUI can highlight it
    public Task<CommandQueue> getControl() {
        return control;
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }
}
