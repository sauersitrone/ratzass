package de.simone.btree.logistic;

import java.util.List;

import com.badlogic.gdx.ai.btree.Task;

import bwapi.TilePosition;
import bwapi.UnitType;
import de.simone.btree.Blackboard;
import de.simone.command.InformationCenter;
import de.simone.command.UnitsCenter;

public class IsBunkerNeededCondition extends LogisticTask {

    @Override
    public Status execute() {
    List<TilePosition> bunkers = InformationCenter.getBunkerLocations();
        int c = UnitsCenter.getUnitCount(UnitType.Terran_Bunker);
        return bunkers != null && c == bunkers.size() ? Status.SUCCEEDED : Status.FAILED;
    }

    @Override
    protected Task<Blackboard> copyTo(Task<Blackboard> task) {
        IsBunkerNeededCondition copy = (IsBunkerNeededCondition) task;
        return copy;
    }
}
