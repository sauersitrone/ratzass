package de.simone.btree.logistic;

import com.badlogic.gdx.ai.btree.Task;

import bwapi.TilePosition;
import bwapi.Unit;
import bwem.ChokePoint;
import de.simone.btree.Blackboard;
import de.simone.command.CommandQueue;
import de.simone.command.InformationCenter;
import de.simone.command.RBWListener;
import de.simone.command.UnitsCenter;

/**
 * Explores a choke point on the map using a free Terran SCV. if e.g bunker muss
 * be built, some thimes the bunker position needs to be scouted first. and mark
 * the terrain as explored. if not, the bunker build order might fail.
 */
public class ExploreChokePointTask extends LogisticTask {

    @Override
    public Status execute() {
        TilePosition startPosition = RBWListener.game.self().getStartLocation();
        ChokePoint chokePoint = InformationCenter.getChokePoint(startPosition);
        if (chokePoint == null)
            return Status.SUCCEEDED;

        Unit scv = UnitsCenter.getFreeTerranSCV();
        if (scv == null)
            return Status.SUCCEEDED;

        CommandQueue.rightClick(scv.getID(), chokePoint.getCenter().toPosition());
        return Status.SUCCEEDED;
    }

    @Override
    protected Task<Blackboard> copyTo(Task<Blackboard> task) {
        ExploreChokePointTask exploreTask = (ExploreChokePointTask) task;
        return exploreTask;
    }
}
