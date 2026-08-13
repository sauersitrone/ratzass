package de.simone.btree;

import com.badlogic.gdx.ai.btree.Task;

import bwapi.UnitType;
import de.simone.command.CommandQueue;
import de.simone.command.UnitsCenter;

public class checkMinimum extends RTask {

    // @TaskAttribute
    // UnitType unitType;

    // @TaskAttribute
    // int minUnits;

    @Override
    public Status execute() {
        UnitsCenter unitsCenter = UnitsCenter.getInstance();

        // min 5 SCVs
        int count = unitsCenter.getUnitCount(UnitType.Terran_SCV);
        if (count < 5)
            unitsCenter.buildUnit(getName(), UnitType.Terran_SCV, 5 - count);

        // min 1 refinery
        count = unitsCenter.getUnitCount(UnitType.Terran_Refinery);
        if (count < 1)
            unitsCenter.buildUnit(getName(), UnitType.Terran_Refinery, 1 - count);

        // min 5 marines
        count = unitsCenter.getUnitCount(UnitType.Terran_Marine);
        if (count < 5)
            unitsCenter.buildUnit(getName(), UnitType.Terran_Marine, 5 - count);

        return Status.SUCCEEDED;
    }

    @Override
    protected Task<CommandQueue> copyTo(Task<CommandQueue> task) {
        return task;
    }
}
