package de.simone.btree.combat;

import de.simone.command.CombatCenter;
import de.simone.command.Squad;

/**
 * a Squad is in a favorable situation if the firepower of the squad's units
 * is greater than the firepower of the enemy units.
 */
public class IsFavorableCondition extends CombatTask {

    @Override
    public Status execute() {
        Squad squad = getObject();
        int firepower = CombatCenter.computeFirePower(squad);

        Status status = firepower > 0 ? Status.SUCCEEDED : Status.FAILED;

        if (status == Status.SUCCEEDED)
            sendCommunication("We can beat them !!");

        return status;
    }
}
