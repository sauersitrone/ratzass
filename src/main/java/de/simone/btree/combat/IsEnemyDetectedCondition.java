package de.simone.btree.combat;

import java.util.List;

import bwapi.Position;
import bwapi.Unit;
import de.simone.command.Squad;
import de.simone.command.UnitsCenter;

public class IsEnemyDetectedCondition extends CombatTask {

    @Override
    public Status execute() {
        Squad squad = getObject();
        Position center = squad.getPosition();
        List<Unit> enemies = UnitsCenter.getEnemyUnits(center, 200);
        Status status =  enemies.size() > 0 ? Status.SUCCEEDED : Status.FAILED;
        if(status == Status.SUCCEEDED) {
            sendCommunication("Enemy detected at position: " + enemies.get(0).getPosition());
        }

        return status;
    }
}
