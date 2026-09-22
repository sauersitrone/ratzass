package de.simone.command;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ai.btree.BehaviorTree;

import bwapi.Position;
import bwapi.Unit;
import bwapi.WalkPosition;
import de.simone.RBWListener;
import de.simone.command.StarCraftConstants.OrderStatus;

/**
 * centralize the combat operations and communications between squad and the
 * rest of the military
 * 
 */
public class CombatCenter {
    public enum RequestName {
        Unknown, CallSupport, AttackPosition, PatrolPosition
    }

    public static BehaviorTree<CombatCenter> behaviorTree;

    private static List<CombatCenterListener> listeners = new ArrayList<>();
    private static StringBuilder comms = new StringBuilder();
    private static List<CombatRequest> squadRequests = new ArrayList<>();

    public static boolean requestPermition(Squad squad, RequestName request) {
        Position position = null;
        if (request == RequestName.PatrolPosition) {
            WalkPosition walkPosition = null;
            while (position == null || !RBWListener.game.isWalkable(walkPosition)) {
                walkPosition = position == null ? null : new WalkPosition(position);
                Position squadPosition = squad.getPosition();
                int x = squadPosition.getX() + (int) (Math.random() * Squad.patrolRadius);
                int y = squadPosition.getY() + (int) (Math.random() * Squad.patrolRadius);
                position = new Position(x, y);
                CombatRequest combatRequest = new CombatRequest(squad, request, position);
                squadRequests.add(combatRequest);
                return true;
            }
        }

        if (request == RequestName.AttackPosition) {
            List<Unit> enemies = getEnemiesInSight(squad);
            if (enemies.size() > 0) {
                position = enemies.get(0).getPosition();
                CombatRequest combatRequest = new CombatRequest(squad, request, position);
                squadRequests.add(combatRequest);
                return true;
            }
        }

        if (request == RequestName.CallSupport) {
            position = squad.getPosition();
            CombatRequest combatRequest = new CombatRequest(squad, request, position);
            squadRequests.add(combatRequest);
            return true;
        }

        return false;
    }

    public static CombatRequest getRequest(Squad squad, RequestName request) {
        CombatRequest request2 = squadRequests.stream()
                .filter(r -> r.squadID.equals(squad.squadID) &&
                        r.request == request &&
                        r.status == OrderStatus.Pending)
                .findFirst().orElse(null);
        return request2;
    }

    /**
     * Returns the first pending combat request for the specified request name, if
     * any.
     * 
     * @param request - the requestName
     * @return the CombatRequest
     */
    public static CombatRequest getRequest(RequestName request) {
        CombatRequest request2 = squadRequests.stream()
                .filter(r -> r.request == request && r.status == OrderStatus.Pending)
                .findFirst().orElse(null);
        return request2;
    }

    /**
     * call by RBWListener every x seconds. this method will:
     * 
     */
    public static void update() {

        String logs = comms.toString();
        listeners.forEach(l -> l.updated(logs));
    }

    public static void sendCommunication(Squad squad, String text) {
        comms.append(squad.type + "unit " + squad.squadID + ": " + text);
        comms.append("\n");
        listeners.forEach(listener -> listener.updated(comms.toString()));
    }

    public static void addListener(CombatCenterListener listener) {
        listeners.add(listener);
    }

    /**
     * Return the list of enemy units currently in sight of all members of the given
     * squad.
     * 
     * @param squad
     * @return
     */
    public static List<Unit> getEnemiesInSight(Squad squad) {
        List<DogTag> dTags = squad.getAliveMembers();
        List<Unit> enemyUnits = new ArrayList<>();
        for (DogTag tag : dTags) {
            enemyUnits.addAll(UnitsCenter.getEnemyUnits(tag.unit));
        }
        return enemyUnits;
    }

    /**
     * Computes the difference in firepower between this squad and the enemies in
     * sight. A positive value indicates that this squad has more firepower, while a
     * negative value indicates that the enemy squad has more firepower.
     * 
     * @param squad - the squad
     * @return > 0 more firepower for this squad, < 0 more firepower for the enemy
     *         squad.
     */
    public static int computeFirePower(Squad squad) {
        List<Unit> enemyUnits = getEnemiesInSight(squad);
        List<DogTag> myUnits = squad.getAliveMembers();

        int groud = enemyUnits.stream().mapToInt(u -> u.getType().groundWeapon().damageAmount()).sum();
        int air = enemyUnits.stream().mapToInt(u -> u.getType().airWeapon().damageAmount()).sum();
        int enemyFirePower = groud + air;

        groud = myUnits.stream().mapToInt(u -> u.unit.getType().groundWeapon().damageAmount()).sum();
        air = myUnits.stream().mapToInt(u -> u.unit.getType().airWeapon().damageAmount()).sum();
        int myFirePower = groud + air;

        return myFirePower - enemyFirePower;
    }

    /**
     * return a bounding box that contains all the given units.
     * 
     * @param units - the units
     * @return the bounding box
     */
    public static Rectangle2D getBox(List<Unit> units) {
        List<Point2D> points = units.stream()
                .<Point2D>map(u -> new Point2D.Double(u.getPosition().x, u.getPosition().y))
                .toList();
        Rectangle2D box = getBox2D(points);
        return box;
    }

    static Rectangle2D getBox2D(List<Point2D> points) {
        Point2D point2d = points.get(0);
        Rectangle2D box = new Rectangle2D.Double(point2d.getX(), point2d.getY(), 0, 0);

        for (int i = 1; i < points.size(); i++) {
            Point2D point = points.get(i);
            box.add(point.getX(), point.getY());
        }
        return box;
    }

}
