package de.simone.btree.logistic;

import java.util.List;

import com.badlogic.gdx.ai.btree.Task;

import bwapi.Unit;
import bwapi.UnitType;
import de.simone.btree.Blackboard;
import de.simone.command.UnitsCenter;

public class LoadBunkersTask extends LogisticTask {

    @Override
    public Status execute() {
        List<Unit> marines = UnitsCenter.getUnits().stream().filter(u -> u.getType() == UnitType.Terran_Marine)
                .toList();
        marines = marines.stream().filter(u -> !u.isLoaded()).toList();

        List<Unit> bunkers = UnitsCenter.getUnits().stream().filter(u -> u.getType() == UnitType.Terran_Bunker)
                .toList();
        for (Unit bunker : bunkers) {
            int space = bunker.getSpaceRemaining();
            if (space > 0) {
                for (int i = 0; i < space; i++) {
                    if (!marines.isEmpty()) {
                        Unit marine = marines.remove(0);
                        marine.load(bunker);
                    }
                }
            }
        }

        return Status.SUCCEEDED;
    }

    @Override
    protected Task<Blackboard> copyTo(Task<Blackboard> task) {
        LoadBunkersTask copy = (LoadBunkersTask) task;
        return copy;
    }
}
