package de.simone.btree;

import com.badlogic.gdx.ai.btree.Task;

import bwapi.UnitType;
import de.simone.command.Command;
import de.simone.command.CommandQueue;

public class TrainTask extends RTask {

    @Override
    public Status execute() {
        CommandQueue commandQueue = getObject();
        Command command = commandQueue.train(UnitType.Terran_SCV);
        return command.status;
    }

    @Override
    protected Task<CommandQueue> copyTo(Task<CommandQueue> task) {
        return task;
    }

}
