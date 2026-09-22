package de.simone.btree.combat;

import java.awt.geom.Point2D;
import java.util.List;

import bwapi.Position;
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
        Position position = squad.getPosition();
        List<Unit> enemies = CombatCenter.getEnemiesInSight(squad);
        enemies = enemies.stream().filter(u -> u.canAttack()).toList();

        Unit closest = null;
        double closestDistance = Double.MAX_VALUE;
        for (Unit unit : enemies) {
            double distance = Point2D.distanceSq(position.getX(), position.getY(), unit.getPosition().getX(),
                    unit.getPosition().getY());
            if (distance < closestDistance) {
                closestDistance = distance;
                closest = unit;
            }
        }
        if (closest != null) {
            CommandQueue.addCommand(UnitCommandType.Attack_Unit, squad, closest.getPosition());
            return Status.SUCCEEDED;
        }

        return Status.FAILED;
    }
}
