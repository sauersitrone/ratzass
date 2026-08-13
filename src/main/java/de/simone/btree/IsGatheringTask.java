package de.simone.btree;

import java.util.List;

import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import bwapi.UnitCommandType;
import bwapi.UnitType;
import de.simone.command.CommandQueue;
import de.simone.command.RUnit;
import de.simone.command.UnitsCenter; 

public class IsGatheringTask extends RTask {

    @TaskAttribute
    int minUnits;

    @Override
    public Status execute() {
        UnitsCenter unitsCenter = UnitsCenter.getInstance();
        List<RUnit> workers = unitsCenter.getUnits(UnitType.Terran_SCV);
        int count = (int) workers.stream().filter(w -> w.currentCommand == UnitCommandType.Gather).count();

        return count >= minUnits ? Status.SUCCEEDED : Status.FAILED;
    }

    @Override
    protected Task<CommandQueue> copyTo(Task<CommandQueue> task) {
        return task;
    }
}
