package de.simone.btree.combat;

import bwapi.Unit;
import bwapi.UnitCommandType;
import de.simone.command.CombatCenter;
import de.simone.command.CommandQueue;
import de.simone.command.Squad;

/**
 * select the closest enemy unit and issue an attack command to reduce the
 * enemy's firepower as fast as possible.
 */
public class FocusFireTask extends CombatTask {

    @Override
    public Status execute() {
        Squad squad = getObject();
        Unit target = CombatCenter.getTarget(squad);
        if (target != null) {
            CommandQueue.addCommand(UnitCommandType.Attack_Unit, squad, target.getPosition());
            return Status.SUCCEEDED;
        }

        return Status.FAILED;
    }
}
