package de.simone.btree.combat;

import java.util.List;

import bwapi.Position;
import bwapi.Unit;
import de.simone.command.Squad;
import de.simone.command.UnitsCenter;

public class IsDangerCondition extends CombatTask {

    @Override
    public Status execute() {
        Squad squad = getObject();
        int myUnits = squad.getAliveMembers().size();

        Position center = squad.getCenter();
        List<Unit> enemies = UnitsCenter.getEnemyUnits(center, 200);

        return enemies.size() > myUnits ? Status.SUCCEEDED : Status.FAILED;

        // subtract: Abstand oder Richtung berechnen
        // Position unit = new Position(100, 200);
        // Position enemy = new Position(180, 240);

        // Position unitPosition = squad.getCenter(true);
        // Position enemyPosition = enemy.getPosition();

        // Position direction = enemyPosition.subtract(unitPosition);
        // Position approachPoint = unitPosition.add(direction.divide(2));

    }
}
