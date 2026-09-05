package de.simone.btree;

import java.util.List;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import bwapi.UnitType;
import de.simone.command.BuildOrder;
import de.simone.command.LogisticCenter;
import de.simone.command.UnitDocument;
import de.simone.command.UnitsCenter;

public class TrainForceToTask extends RTask {

    @TaskAttribute(required = true)
    public int level;

    @Override
    public Status execute() {
        UnitsCenter unitsCenter = UnitsCenter.getInstance();

        if (getStatus() == Status.RUNNING) {
            return getBuildOrderStatus("" + level);
        }

        if (level == 1) {
            UnitDocument rUnit = unitsCenter.getDocument(UnitType.Terran_Barracks);
            if (rUnit == null) {
                submitOrder(UnitType.Terran_Barracks, 1);
                return Status.RUNNING;
            }
        }

        if (level == 2) {
            UnitDocument rUnit = unitsCenter.getDocument(UnitType.Terran_Academy);
            if (rUnit == null) {
                submitOrder(UnitType.Terran_Academy, 1);
                return Status.RUNNING;
            }

            // TODO: how to work with the upgrade?
            // TODO: upgrade muss be a build order
            // TODO: i think the upgrades are in the pddl domain. i need only to find the
            // solution for the target upgrade. <-----------
            // Unit unit = RBWListener.game.getUnit(rUnit.unitID);
            // unit.upgrade(UpgradeType.U_238_Shells);
        }

        if (level == 3) {
            UnitDocument rUnit = unitsCenter.getDocument(UnitType.Terran_Factory);
            if (rUnit == null) {
                submitOrder(UnitType.Terran_Factory, 1);
                return Status.RUNNING;
            }

            rUnit = unitsCenter.getDocument(UnitType.Terran_Machine_Shop);
            if (rUnit == null) {
                submitOrder(UnitType.Terran_Machine_Shop, 1);
                return Status.RUNNING;
            }

            rUnit = unitsCenter.getDocument(UnitType.Terran_Armory);
            if (rUnit == null) {
                submitOrder(UnitType.Terran_Armory, 1);
                return Status.RUNNING;
            }
        }

        return Status.SUCCEEDED;
    }

    private void submitOrder(UnitType unitType, int count) {
        List<BuildOrder> orders = LogisticCenter.getInstance().addBuildOrder(unitType, count);
        Blackboard blackboard = getObject();
        blackboard.orders.put("" +level, orders);
    }

    @Override
    public String getName() {
        return super.getName() + " level:" + level;
    }
}
