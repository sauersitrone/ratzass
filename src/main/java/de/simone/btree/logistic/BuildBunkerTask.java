package de.simone.btree.logistic;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ai.btree.Task;

import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import de.simone.btree.Blackboard;
import de.simone.command.CombatCenter;
import de.simone.command.LogisticCenter;
import de.simone.command.UnitsCenter;

public class BuildBunkerTask extends LogisticTask {
    private String buildVoucher;
    private String trainVoucher;

    @Override
    public Status execute() {
        Status status = getStatus();
        if (status == Status.RUNNING) {
            Status buildStatus = getBuildOrderStatus(buildVoucher);

            Status trainStatus = Status.SUCCEEDED;
            if (trainVoucher != null)
                trainStatus = getBuildOrderStatus(trainVoucher);

            if (buildStatus == Status.SUCCEEDED && trainStatus == Status.SUCCEEDED) {
                loadBunker();
                return Status.SUCCEEDED;
            }
            status = buildStatus == Status.FAILED || trainStatus == Status.FAILED ? Status.FAILED : Status.RUNNING;
            return status;
        }

        // check if is need to build defences. if no need, return SUCCEEDED
        TilePosition bunkerLocation = CombatCenter.getBunkerLocation();
        if (bunkerLocation == null)
            return Status.SUCCEEDED;

        buildVoucher = LogisticCenter.addBuildOrder(UnitType.Terran_Bunker, 1);

        int exist = UnitsCenter.getUnitCount(UnitType.Terran_Bunker);
        int total = exist < 4 ? 4 - exist : 0;
        if (exist > 0)
            trainVoucher = LogisticCenter.addBuildOrder(UnitType.Terran_Marine, total);
        else 
            return Status.SUCCEEDED;

        return Status.RUNNING;
    }

    private void loadBunker() {
        List<Unit> marines = UnitsCenter.getUnits().stream().filter(u -> u.getType() == UnitType.Terran_Marine)
                .toList();
        marines = marines.stream().filter(u -> !u.isLoaded()).toList();
        marines = new ArrayList<>(marines);

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
    }

    @Override
    protected Task<Blackboard> copyTo(Task<Blackboard> task) {
        BuildBunkerTask buildBunkerTask = (BuildBunkerTask) task;
        buildBunkerTask.buildVoucher = buildVoucher;
        buildBunkerTask.trainVoucher = trainVoucher;
        return buildBunkerTask;
    }

    @Override
    public String toString() {
        String name = super.toString();
        return name;
    }
}
