package de.simone.btree.combat;

import de.simone.command.Squad;

/**
 * a Squad is in bad shape condition if it has less than half of its units
 * alive.
 * 
 */
public class IsSquadInBadShapeCondition extends CombatTask {

    @Override
    public Status execute() {
        Squad squad = getObject();
        int units = squad.getSquadUnits().size();
        int aliveUnits = squad.getAliveMembers().size();

        return aliveUnits < units / 2 ? Status.SUCCEEDED : Status.FAILED;
    }
}
