package de.simone.btree;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import bwapi.UnitType;
import de.simone.command.BuildOrder;
import de.simone.command.LogisticCenter;
import de.simone.command.RUnit;
import de.simone.command.UnitsCenter;

public class TrainForceToTask extends RTask {

    @TaskAttribute(required = true)
    public int level;

    private BuildOrder currentBuildOrder;

    @Override
    public Status execute() {
        if (getStatus() == Status.RUNNING)
            return getBuildOrderStatus(currentBuildOrder);

        UnitsCenter unitsCenter = UnitsCenter.getInstance();
        LogisticCenter logisticsCenter = LogisticCenter.getInstance();

        if (level == 1) {
            RUnit rUnit = unitsCenter.getUnit(UnitType.Terran_Barracks);
            if (rUnit == null) {
                currentBuildOrder = new BuildOrder(getName(), UnitType.Terran_Barracks, 1);
                logisticsCenter.addBuildOrder(currentBuildOrder);
                return Status.RUNNING;
            }
        }

        if (level == 2) {
            RUnit rUnit = unitsCenter.getUnit(UnitType.Terran_Academy);
            if (rUnit == null) {
                currentBuildOrder = new BuildOrder(getName(), UnitType.Terran_Academy, 1);
                logisticsCenter.addBuildOrder(currentBuildOrder);
                return Status.RUNNING;
            }

            // TODO: how to work with the upgrade?
            // TODO: upgrade muss be a build order
            // TODO: i think the upgrades are in the pddl domain. i need only to find the solution for the target upgrade.  <-----------
            // Unit unit = RBWListener.game.getUnit(rUnit.unitID);
            // unit.upgrade(UpgradeType.U_238_Shells);
        }

        if (level == 3) {
            RUnit rUnit = unitsCenter.getUnit(UnitType.Terran_Factory);
            if (rUnit == null) {
                currentBuildOrder = new BuildOrder(getName(), UnitType.Terran_Factory, 1);
                logisticsCenter.addBuildOrder(currentBuildOrder);
                return Status.RUNNING;
            }

            rUnit = unitsCenter.getUnit(UnitType.Terran_Machine_Shop);
            if (rUnit == null) {
                currentBuildOrder = new BuildOrder(getName(), UnitType.Terran_Machine_Shop, 1);
                logisticsCenter.addBuildOrder(currentBuildOrder);
                return Status.RUNNING;
            }

            rUnit = unitsCenter.getUnit(UnitType.Terran_Armory);
            if (rUnit == null) {
                currentBuildOrder = new BuildOrder(getName(), UnitType.Terran_Armory, 1);
                logisticsCenter.addBuildOrder(currentBuildOrder);
                return Status.RUNNING;
            }
        }

        return Status.SUCCEEDED;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName() + " level:" + level;
    }
}
