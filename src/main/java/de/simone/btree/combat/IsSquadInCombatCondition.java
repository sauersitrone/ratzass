package de.simone.btree.combat;

import de.simone.command.Squad;

/**
 * Checks if the squad is currently engaged in combat.
 */
public class IsSquadInCombatCondition extends CombatTask {

    @Override
    public Status execute() {
        Squad squad = getObject();
        int inCombat = (int) squad.getAliveMembers().stream().map(dt -> dt.unit).filter(u -> u.isAttacking()).count();

        Status status = inCombat > 0 ? Status.SUCCEEDED : Status.FAILED;
        return status;
    }
}
