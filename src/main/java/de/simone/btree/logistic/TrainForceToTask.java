package de.simone.btree.logistic;

import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import bwapi.UnitType;
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
                submitOrder(UnitType.Terran_Barracks, 1);
                return Status.RUNNING;
            }
        }

        if (level == 2) {
            DogTag dTag = UnitsCenter.getDogTag(UnitType.Terran_Academy);
            if (dTag == null) {
                submitOrder(UnitType.Terran_Academy, 1);
                return Status.RUNNING;
            }

            // TODO: how to work with the upgrade?
            // TODO: upgrade muss be a build order
            // TODO: i think the upgrades are in the pddl domain. i need only to find the
            // solution for the target upgrade. <-----------
            // Unit unit = RBWListener.game.getUnit(dTag.unitID);
            // unit.upgrade(UpgradeType.U_238_Shells);
        }

        if (level == 3) {
            DogTag dTag = UnitsCenter.getDogTag(UnitType.Terran_Factory);
            if (dTag == null) {
                submitOrder(UnitType.Terran_Factory, 1);
                return Status.RUNNING;
            }

            dTag = UnitsCenter.getDogTag(UnitType.Terran_Machine_Shop);
            if (dTag == null) {
                submitOrder(UnitType.Terran_Machine_Shop, 1);
                return Status.RUNNING;
            }

            dTag = UnitsCenter.getDogTag(UnitType.Terran_Armory);
            if (dTag == null) {
                submitOrder(UnitType.Terran_Armory, 1);
                return Status.RUNNING;
            }
        }

        return Status.SUCCEEDED;
    }

    private void submitOrder(UnitType unitType, int count) {
        voucher = LogisticCenter.addBuildOrder(unitType, count);
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
