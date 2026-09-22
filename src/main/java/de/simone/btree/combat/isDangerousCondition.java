package de.simone.btree.combat;

import de.simone.command.CombatCenter;
import de.simone.command.Squad;

/**
 * a Squad is in a dangerous situation  if the firepower of the enemy
 * units is greater than the firepower of the squad's units.
 */
public class IsDangerousCondition extends CombatTask {

    @Override
    public Status execute() {
        Squad squad = getObject();
        int firepower = CombatCenter.computeFirePower(squad);

        Status status = firepower < 0 ? Status.SUCCEEDED : Status.FAILED;

        if (status == Status.SUCCEEDED)
            sendCommunication("We are in dangerous situation !!");

        return status;
    }
}
