package de.simone.btree;

import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import de.simone.command.Command;
import de.simone.command.CommandQueue;

public class GatherTask extends RTask {

    @TaskAttribute
    CommandQueue.ResourceType resourceType;

    @Override
    public Status execute() {
        CommandQueue commandQueue = getObject();
        Command command = commandQueue.gather(resourceType);
        return command.status;
    }

    @Override
    public String getName() {
        String name = super.getName();
        name += " (" + resourceType + ")";
        return name;
    }

    @Override
    protected Task<CommandQueue> copyTo(Task<CommandQueue> task) {
        return task;
    }
}
