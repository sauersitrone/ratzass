package de.simone.command;

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

public class InformationCenter {
    private static List<InformationCenterListener> listeners = new ArrayList<>();
    private static Map<Integer, Unit> enemyUnits = new TreeMap<>();
    private static List<Position> regions = new ArrayList<>();

    public static void addListener(InformationCenterListener listener) {
        listeners.add(listener);
    }

    /**
     * Call by RBWListener on every x frames.
     */
    public static void heartBeat() {
        initRegions();

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
        if (regions.isEmpty()) {
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
    }
}
