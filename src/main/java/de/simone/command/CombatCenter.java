package de.simone.command;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.Pair;

import com.badlogic.gdx.ai.btree.BehaviorTree;

import bwapi.Position;
import bwapi.Unit;
import bwapi.WalkPosition;
import de.simone.RBWListener;
import de.simone.math.SimplifyPolyline;

/**
 * centralize the combat operations and communications between squad and the
 * rest of the military
 * 
 */
public class CombatCenter {
    public enum NamedArea {
        RetreatArea, CallForHelp, EnemyPosition, RandomPosition
    }

    private static List<CombatCenterListener> listeners = new ArrayList<>();
    private static List<Squad> squads = new ArrayList<>();
    private static StringBuilder comms = new StringBuilder();
    // private static Map<NamedArea, Position> namedAreas = new TreeMap<>();
    private static Map<String, Pair<NamedArea, Position>> namedAreas = new TreeMap<>();

    public static BehaviorTree<CombatCenter> behaviorTree;

    public static void addSquad(Squad squad) {
        squads.add(squad);
    }

    public static boolean computePosition(Squad squad, NamedArea area) {
        Position position = null;

        if (area == NamedArea.RandomPosition) {
            WalkPosition walkPosition = position == null ? null : new WalkPosition(position);
            while (position == null || !RBWListener.game.isWalkable(walkPosition)) {
                Position squadPosition = squad.getPosition();
                int x = squadPosition.getX() + (int) (Math.random() * 400) - 200;
                int y = squadPosition.getY() + (int) (Math.random() * 400) - 200;
                position = new Position(x, y);
            }
        }

        if (area == NamedArea.EnemyPosition) {
            List<Unit> enemies = UnitsCenter.getEnemyUnits(squad.getPosition(), 200);
            if (enemies.size() > 0) {
                position = enemies.get(0).getPosition();
            }
        }

        if (area == NamedArea.RetreatArea) {
            position = getRetreatPosition(squad, 200);
        }

        if (position != null) {
            addArea(squad, area, position);
            return true;
        }
        return false;
    }

    private static Position getRetreatPosition(Squad squad, int distance) {
        List<Point2D> positions = new ArrayList<>(squad.positionsTracking);
        SimplifyPolyline.simplify(positions, 32 * 10, false);

        Path2D path = new Path2D.Double();
        for (int i = 0; i < positions.size(); i++) {
            Point2D point2d = positions.get(i);
            if (i == 0)
                path.moveTo(point2d.getX(), point2d.getY());

            path.lineTo(point2d.getX(), point2d.getY());
        }

        // look for the point more close to the distance
        double count = 0D;
        Point2D prevPoint = positions.get(positions.size() - 1);
        for (int i = positions.size() - 1; i >= 0; i--) {
            Point2D point2d = positions.get(i);
            count += point2d.distance(prevPoint);
            prevPoint = point2d;
            if (count >= distance)
                break;
        }

        return new Position((int) prevPoint.getX(), (int) prevPoint.getY());
    }

    public static Position getArea(Squad squad, NamedArea areaName) {
        Pair<NamedArea, Position> pair = namedAreas.get(squad.squadID);
        if (pair != null && pair.getLeft() == areaName) {
            return pair.getRight();
        }
        return null;
    }

    private static void addArea(Squad squad, NamedArea areaName, Position position) {
        namedAreas.put(squad.squadID, Pair.of(areaName, position));
    }

    /**
     * call by RBWListener every x seconds. this method will:
     * 
     */
    public static void update() {

        String logs = comms.toString();
        listeners.forEach(l-> l.updated(logs));
    }

    public static void sendCommunication(Squad squad, String text) {
        comms.append(squad.type + "unit " + squad.squadID + ": " + text);
        comms.append("\n");
        listeners.forEach(listener -> listener.updated(comms.toString()));
    }


    public static void addListener(CombatCenterListener listener) {
        listeners.add(listener);
    }

}
