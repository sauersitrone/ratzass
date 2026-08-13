package de.simone.btree;

import com.badlogic.gdx.ai.btree.Task;

import bwapi.Position;
import de.simone.command.Command;
import de.simone.command.CommandQueue;
import lombok.extern.java.Log;

@Log
public class PatrolTask extends RTask {

    // @TaskAttribute
    // public UnitType unitType;

    @Override
    public Status execute() {
       CommandQueue commandQueue = getObject();

       // check if is there a chokpoint or a point of interest to patrol
        Position patrolPosition = new Position(0, 0);
        Command command = commandQueue.patrol(patrolPosition);

        return command.status;
    }

    @Override
    protected Task<CommandQueue> copyTo(Task<CommandQueue> task) {
        return task;
    }

}
