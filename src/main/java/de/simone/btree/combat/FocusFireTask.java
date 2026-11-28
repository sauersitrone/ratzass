package de.simone.btree.combat;

import de.simone.command.Squad;

/**
 * select the closest enemy unit and issue an attack command to reduce the
 * enemy's firepower as fast as possible.
 */
public class FocusFireTask extends CombatTask {

    @Override
    public Status execute() {
        Squad squad = getObject();
        boolean firing = squad.focusFire();
        return firing ? Status.SUCCEEDED : Status.FAILED;
    }
}
