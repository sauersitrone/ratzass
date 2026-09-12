package de.simone.btree.combat;

import de.simone.command.Squad;

public class isSquadInBadShapeCondition extends CombatTask {

    @Override
    public Status execute() {
        Squad squad = getObject();
        int myUnits = squad.getAliveMembers().size();

        return myUnits < 3 ? Status.SUCCEEDED : Status.FAILED;

        // subtract: Abstand oder Richtung berechnen
        // Position unit = new Position(100, 200);
        // Position enemy = new Position(180, 240);

        // Position unitPosition = squad.getCenter(true);
        // Position enemyPosition = enemy.getPosition();

        // Position direction = enemyPosition.subtract(unitPosition);
        // Position approachPoint = unitPosition.add(direction.divide(2));

    }
}
