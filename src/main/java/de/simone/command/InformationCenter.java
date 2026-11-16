package de.simone.command;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import bwapi.Position;
import bwapi.Region;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.WalkPosition;
import bwem.Base;
import bwem.ChokePoint;
import de.simone.Vec;

public class InformationCenter {
    private static List<InformationCenterListener> listeners = new ArrayList<>();
    private static Map<Integer, Unit> enemyUnits = new TreeMap<>();
    private static List<Position> regions = new ArrayList<>();
    private static List<TilePosition> bunkerPositions = new ArrayList<>();

    public static void addListener(InformationCenterListener listener) {
        listeners.add(listener);
    }

    /**
     * Call by RBWListener on every x frames.
     */
    public static void heartBeat() {
        initRegions();
        initDefenceLocations();
        listeners.forEach(l -> l.informationUpdated(getEnemyUnits()));
    }

    public static void onUnitComplete(Unit unit) {
        if (!RBWListener.game.self().isEnemy(unit.getPlayer()))
            return;

        enemyUnits.put(unit.getID(), unit);
        listeners.forEach(l -> l.informationUpdated(getEnemyUnits()));
    }

    public static void onUnitDiscover(Unit unit) {
        if (!RBWListener.game.self().isEnemy(unit.getPlayer()))
            return;

        enemyUnits.put(unit.getID(), unit);
        listeners.forEach(l -> l.informationUpdated(getEnemyUnits()));
    }

    public static void onUnitDestroy(Unit unit) {
        if (!RBWListener.game.self().isEnemy(unit.getPlayer()))
            return;

        enemyUnits.remove(unit.getID());
        listeners.forEach(l -> l.informationUpdated(getEnemyUnits()));
    }

    public static List<Unit> getEnemyUnits() {
        return enemyUnits.values().stream().toList();
    }

    public static int getEnemyUnitCount(UnitType unitType) {
        return getEnemyUnits(unitType).size();
    }

    public static List<Unit> getEnemyUnits(UnitType unitType) {
        return getEnemyUnits().stream().filter(u -> u.getType() == unitType).toList();
    }

    public static Position getPatrolTarget(Position fromPosition) {
        // TODO: build like a priority of targets. like is there a building detected,
        // patrol the area more often to gather more info
        return getPatrolPosition(fromPosition);
    }

    private static boolean isRegionPatrolable(Position position) {
        TilePosition tilePosition = position.toTilePosition();
        WalkPosition walkPosition = new WalkPosition(position);
        return RBWListener.game.isWalkable(walkPosition) && !RBWListener.game.isExplored(tilePosition);
    }

    private static Position getPatrolPosition(Position fromPosition) {
        for (Position regionCenter : regions) {
            if (isRegionPatrolable(regionCenter))
                return regionCenter;
        }
        return null;
    }

    /**
     * Initialize the list of regions, ordered by distance to the start location,
     * with the farthest regions first.
     */
    private static void initRegions() {
        if (!regions.isEmpty())
            return;

        // regions
        List<Region> allRegions = RBWListener.game.getAllRegions();
        allRegions.forEach(r -> regions.add(r.getCenter()));

        // bases
        List<Base> bases = RBWListener.bwMap.getBases();
        bases.forEach(b -> regions.add(b.getCenter()));

        regions.sort((r1, r2) -> {
            TilePosition p1 = r1.toTilePosition();
            TilePosition p2 = r2.toTilePosition();
            TilePosition start = RBWListener.game.self().getStartLocation();
            double d1 = start.getDistance(p1);
            double d2 = start.getDistance(p2);
            // return Double.compare(d1, d2);
            return Double.compare(d2, d1); // inverse
        });
    }

    public static Line2D getDefenceLine() {
        TilePosition startPosition = RBWListener.game.self().getStartLocation();
        ChokePoint chokePoint = getChokePoint(startPosition);
        if (chokePoint == null)
            return null;
        TilePosition chokePosition = chokePoint.getCenter().toTilePosition();
        Line2D line = new Line2D.Double(chokePosition.toPosition().x, chokePosition.toPosition().y,
                startPosition.toPosition().x, startPosition.toPosition().y);
        return line;
    }

    private static void initDefenceLocations() {
        if (!bunkerPositions.isEmpty())
            return;

        bunkerPositions = getDefenceLocations(UnitType.Terran_Bunker, UnitType.Terran_Bunker);
    }

    public static List<TilePosition> getDefenceLocations(UnitType leftUnit, UnitType rightUnit) {
        List<TilePosition> buildPositions = new ArrayList<>();
        Line2D center = getDefenceLine();
        if (center == null)
            return buildPositions;

        Line2D[] lines = getParallelLines(center, leftUnit, rightUnit);
        TilePosition buildLocation = getBuildLocation(lines[0], leftUnit);
        if (buildLocation != null)
            buildPositions.add(buildLocation);
        buildLocation = getBuildLocation(lines[1], rightUnit);
        if (buildLocation != null)
            buildPositions.add(buildLocation);
        return buildPositions;
    }

    /**
     * return the locations of the bunkers. this method always returns a list of 3
     * bunkers and only if there is a choke point in the vicinities. if this
     * conditions are not met, this method returns null.
     * 
     * @return 3 bunker list or empty list
     */
    public static List<TilePosition> getBunkerLocations() {
        return bunkerPositions;
    }

    private static TilePosition getBuildLocation(Line2D line, UnitType unitType) {
        double len = line.getP1().distance(line.getP2());
        int steps = (int) Math.ceil(len / TilePosition.SIZE_IN_PIXELS);
        for (int i = 0; i <= steps; i++) {
            Point2D point = Vec.lerp(line.getP1(), line.getP2(), (double) i / steps);
            TilePosition bp = new Position((int) point.getX(), (int) point.getY()).toTilePosition();
            if (RBWListener.game.canBuildHere(bp, unitType))
                return bp;
        }
        return null;
    }

    public static Line2D[] getBunkerLines() {
        Line2D line = getDefenceLine();
        Line2D[] lines = getParallelLines(line, UnitType.Terran_Bunker, UnitType.Terran_Bunker);
        return new Line2D[] { lines[0], line, lines[1] };
    }

    private static Line2D[] getParallelLines(Line2D center, UnitType leftUnitType, UnitType rightUnitType) {
        double dx = center.getX2() - center.getX1();
        double dy = center.getY2() - center.getY1();
        double length = Math.hypot(dx, dy);
        double distance = leftUnitType.tileHeight() * TilePosition.SIZE_IN_PIXELS;
        double offsetX = -dy / length * distance;
        double offsetY = dx / length * distance;
        Line2D left = new Line2D.Double(
                center.getX1() + offsetX, center.getY1() + offsetY,
                center.getX2() + offsetX, center.getY2() + offsetY);
        double rightDistance = rightUnitType.tileHeight() * TilePosition.SIZE_IN_PIXELS;
        double rightOffsetX = -dy / length * rightDistance;
        double rightOffsetY = dx / length * rightDistance;
        Line2D right = new Line2D.Double(
                center.getX1() - rightOffsetX, center.getY1() - rightOffsetY,
                center.getX2() - rightOffsetX, center.getY2() - rightOffsetY);
        return new Line2D[] { left, right };
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
}
