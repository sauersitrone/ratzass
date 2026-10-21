package de.simone.command;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

import com.badlogic.gdx.ai.btree.BehaviorTree;

import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitCommandType;
import bwapi.UnitType;
import bwapi.WalkPosition;
import bwem.ChokePoint;
import de.simone.Vec;
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
        if (request == RequestName.PatrolPosition) {
            Position regionCenter = InformationCenter.getPatrolTarget(squad.getPosition());
            if (regionCenter == null)
                return false;

            CombatRequest combatRequest = new CombatRequest(squad, request, regionCenter);
            squadRequests.add(combatRequest);
            return true;
        }

        if (request == RequestName.AttackPosition) {
            List<Unit> enemies = getEnemiesInSight(squad);
            if (enemies.size() > 0) {
                Position position = enemies.get(0).getPosition();
                CombatRequest combatRequest = new CombatRequest(squad, request, position);
                squadRequests.add(combatRequest);
                return true;
            }
        }

        if (request == RequestName.CallSupport) {
            Position position = squad.getPosition();
            CombatRequest combatRequest = new CombatRequest(squad, request,
                    position);
            squadRequests.add(combatRequest);
            return true;
        }

        return false;
    }

    public static CombatRequest getRequest(Squad squad, RequestName request) {
        CombatRequest request2 = squadRequests.stream()
                .filter(r -> r.squadID.equals(squad.squadID) &&
                        r.request == request &&
                        r.status == OrderStatus.Queued)
                .findFirst().orElse(null);
        return request2;
    }

    /**
     * Returns the first queued combat request for the specified request name, if
     * any. this method will set the status of the request to completed, so that it
     * won't be returned again.
     * 
     * @param request - the requestName
     * @return the CombatRequest
     */
    public static CombatRequest getRequest(RequestName request) {
        CombatRequest request2 = squadRequests.stream()
                .filter(r -> r.request == request && r.status == OrderStatus.Queued)
                .findFirst().orElse(null);
        if (request2 != null) {
            request2.status = OrderStatus.Completed;
        }
        return request2;
    }

    /**
     * call by RBWListener every x seconds. this method will:
     * 
     */
    public static void update() {
        String logs = comms.toString();
        listeners.forEach(l -> l.update(logs));
    }

    public static void sendCommunication(Squad squad, String text) {
        comms.append(squad.type + " unit " + squad.squadID + ": " + text);
        comms.append("\n");
        listeners.forEach(listener -> listener.update(comms.toString()));
    }

    public static void sendCommunication(Squad squad, UnitCommandType command) {
        List<String> quotes = StarCraftConstants.quotes.stream()
                .filter(pair -> pair.getLeft() == command)
                .map(Pair::getRight).toList();
        Collections.shuffle(quotes);
        sendCommunication(squad, quotes.get(0));
    }

    public static void addListener(CombatCenterListener listener) {
        listeners.add(listener);
    }

    public static Unit getTarget(Squad squad) {
        Unit target = null;
        List<Unit> enemies = CombatCenter.getEnemiesInSight(squad);
        Optional<Unit> optional = enemies.stream()
                .min((u1, u2) -> {
                    int i1 = StarCraftConstants.attackPriorities.indexOf((u1.getType()));
                    int i2 = StarCraftConstants.attackPriorities.indexOf((u2.getType()));
                    return Integer.compare(i1, i2);
                });

        // priority target found, returnt it, else, find closest
        if (optional.isPresent()) {
            target = optional.get();
        } else {
            target = getClosestEnemy(squad);
        }

        return target;
    }

    /**
     * Return the list of enemy units currently in sight of all members of the given
     * squad.
     * 
     * @param squad
     * @return
     */
    public static List<Unit> getEnemiesInSight(Squad squad) {
        List<DogTag> dTags = squad.getSquadUnits();
        List<Unit> enemyUnits = new ArrayList<>();
        for (DogTag tag : dTags) {
            enemyUnits.addAll(getEnemiesInSight(tag.unit));
        }
        return enemyUnits;
    }

    public static Unit getClosestEnemy(Squad squad) {
        List<Unit> enemies = CombatCenter.getEnemiesInSight(squad);
        Position position = squad.getPosition();
        Unit closestUnit = enemies.stream().min((e1, e2) -> Double.compare(position.getDistance(e1.getPosition()),
                position.getDistance(e2.getPosition()))).orElse(null);
        return closestUnit;
    }

    /**
     * Return a list of enemy units currently in sight of the given unit.
     * 
     * @param unit - the unit
     * @return - the enemies
     */
    public static List<Unit> getEnemiesInSight(Unit unit) {
        Position center = unit.getPosition();
        List<Unit> units = RBWListener.game.getUnitsInRadius(center, unit.getType().sightRange());
        List<Unit> enemies = units.stream().filter(u -> u.getPlayer().isEnemy(RBWListener.game.self())).toList();
        return enemies;
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
        List<DogTag> myUnits = squad.getSquadUnits();

        int ground = enemyUnits.stream().mapToInt(u -> u.getType().groundWeapon().damageAmount()).sum();
        int air = enemyUnits.stream().mapToInt(u -> u.getType().airWeapon().damageAmount()).sum();
        int enemyFirePower = ground + air;

        ground = myUnits.stream().mapToInt(u -> u.unit.getType().groundWeapon().damageAmount()).sum();
        air = myUnits.stream().mapToInt(u -> u.unit.getType().airWeapon().damageAmount()).sum();
        int myFirePower = ground + air;

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

    /**
     * Returns the closest chokepoint to the given start position.
     * 
     * @param startPosition - the start position
     * @return the chokepoint
     */
    public static ChokePoint getChokePoint(TilePosition startPosition) {
        List<ChokePoint> chokePoints = RBWListener.bwMap.getChokePoints();
        ChokePoint chokePoint = null;
        double distance = Integer.MAX_VALUE;
        for (ChokePoint cp : chokePoints) {
            TilePosition cpPosition = cp.getCenter().toTilePosition();
            double dist = startPosition.getDistance(cpPosition);
            if (dist < distance) {
                chokePoint = cp;
                distance = dist;
            }
        }
        return chokePoint;
    }

    /**
     * compute radius from the center of the chokepoint
     * 
     * @param chokePoint - the chokepoint
     * @return the radious
     */
    public static int getChokePointRadious(ChokePoint chokePoint) {
        WalkPosition center = chokePoint.getCenter();
        List<WalkPosition> geom = chokePoint.getGeometry();
        int extentWalk = geom.stream()
                .mapToInt(p -> Math.max(Math.abs(p.x - center.x), Math.abs(p.y - center.y)))
                .max().orElse(4);
        int radius = Math.max(1, extentWalk / 4) * TilePosition.SIZE_IN_PIXELS;
        return radius;
    }

    public static TilePosition getBunkerLocation() {
        TilePosition startPosition = RBWListener.game.self().getStartLocation();
        ChokePoint chokePoint = CombatCenter.getChokePoint(startPosition);
        TilePosition buildPosition = null;
        // is there choke point nearby?
        if (chokePoint != null) {
            TilePosition chokePosition = chokePoint.getCenter().toTilePosition();
            Point2D a = Vec.of(chokePosition.x, chokePosition.y);
            Point2D b = Vec.of(startPosition.x, startPosition.y);
            double len = Math.sqrt(a.distanceSq(b));
            int steps = (int) Math.ceil(len);
            for (int i = 1; i <= steps; i++) {
                Point2D point = Vec.lerp(a, b, (double) i / steps);
                buildPosition = new TilePosition((int) point.getX(), (int) point.getY());
                if (RBWListener.game.canBuildHere(buildPosition, UnitType.Terran_Bunker))
                    return buildPosition;
            }
        }

        // no choke point nearby, find a position around the start position
        List<TilePosition> positions = getBunkerLocations();
        positions.removeIf(p -> !RBWListener.game.canBuildHere(p, UnitType.Terran_Bunker));
        if (!positions.isEmpty())
            buildPosition = positions.get(0);

        return buildPosition;
    }

    /**
     * return a list of possible bunker locations around the start position. The
     * list is ordered by distance to the start position.
     * 
     * @return the list
     */
    public static List<TilePosition> getBunkerLocations() {
        TilePosition startPosition = RBWListener.game.self().getStartLocation();
        Point2D b = Vec.of(startPosition.x, startPosition.y);
        List<Point2D> point2ds = Vec.getIntersectionPoints(b, 10 * TilePosition.SIZE_IN_PIXELS); // TODO:
        List<TilePosition> positions = new ArrayList<>();
        point2ds.forEach(p -> positions.add(new TilePosition((int) p.getX(), (int) p.getY())));
        return positions;
    }
}
