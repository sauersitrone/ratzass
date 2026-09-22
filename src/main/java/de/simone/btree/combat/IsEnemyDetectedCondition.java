package de.simone.btree.combat;

import java.util.List;

import bwapi.Unit;
import de.simone.command.CombatCenter;
import de.simone.command.Squad;

/**
 * Checks if any enemy units are detected by the squad.
 */
public class IsEnemyDetectedCondition extends CombatTask {

    @Override
    public Status execute() {
        Squad squad = getObject();
        List<Unit> enemies = CombatCenter.getEnemiesInSight(squad);
        Status status = !enemies.isEmpty() ? Status.SUCCEEDED : Status.FAILED;

        if (status == Status.SUCCEEDED) {
            sendCommunication("Enemy detected at position: " + enemies.get(0).getPosition());
        }

        return status;
    }
}
