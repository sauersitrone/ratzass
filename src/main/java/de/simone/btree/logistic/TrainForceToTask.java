package de.simone.btree.logistic;

import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import bwapi.UnitType;
import bwapi.UpgradeType;
import de.simone.btree.Blackboard;
import de.simone.command.DogTag;
import de.simone.command.LogisticCenter;
import de.simone.command.UnitsCenter;

/**
 * traint the military force to a specified level. this leaf is used as first in
 * a sequence because it returns silently if the required level is already met.
 * this allow to check if Squads need resupply units.
 */
public class TrainForceToTask extends LogisticTask {

    @TaskAttribute(required = true)
    public int level;

    private String voucher;

    @Override
    public Status execute() {

        if (getStatus() == Status.RUNNING) {
            return getBuildOrderStatus(voucher);
        }

        if (level == 1) {
            DogTag dTag = UnitsCenter.getDogTag(UnitType.Terran_Barracks);
            if (dTag == null) {
                voucher = LogisticCenter.addBuildOrder(UnitType.Terran_Barracks, 1);
                return Status.RUNNING;
            }
        }

        if (level == 2) {
            // TODO: check if i already have the upgrade, if yes return SUCCEEDED
            voucher = LogisticCenter.addBuildOrder(UpgradeType.U_238_Shells, 1);
            return Status.RUNNING;
        }

        if (level == 3) {
            DogTag dTag = UnitsCenter.getDogTag(UnitType.Terran_Factory);
            if (dTag == null) {
                voucher = LogisticCenter.addBuildOrder(UnitType.Terran_Factory, 1);
                return Status.RUNNING;
            }

            dTag = UnitsCenter.getDogTag(UnitType.Terran_Machine_Shop);
            if (dTag == null) {
                voucher = LogisticCenter.addBuildOrder(UnitType.Terran_Machine_Shop, 1);
                return Status.RUNNING;
            }

            dTag = UnitsCenter.getDogTag(UnitType.Terran_Armory);
            if (dTag == null) {
                voucher = LogisticCenter.addBuildOrder(UnitType.Terran_Armory, 1);
                return Status.RUNNING;
            }
        }

        return Status.SUCCEEDED;
    }

    @Override
    public String toString() {
        return super.toString() + " level:" + level;
    }

    @Override
    protected Task<Blackboard> copyTo(Task<Blackboard> task) {
        TrainForceToTask copy = (TrainForceToTask) task;
        copy.level = level;
        return copy;
    }
}
