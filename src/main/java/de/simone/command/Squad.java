package de.simone.command;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.ai.btree.BehaviorTree;

import bwapi.Color;
import bwapi.CoordinateType;
import bwapi.Position;
import bwapi.UnitCommandType;
import bwapi.UnitType;
import de.simone.RBWListener;
import de.simone.RUtils;
import de.simone.btree.Blackboard;

public class Squad {
    public enum SquadStatus {
        Assembling, Assembled, Attack, Regroup, Retreat
    }

    public enum SquadType {
        Patrol, Strike, Combat
    }

    public static List<String> coolSquadNames = new ArrayList<String>();
    static {
        coolSquadNames.add("Alpha");
        coolSquadNames.add("Bravo");
        coolSquadNames.add("Charlie");
        coolSquadNames.add("Delta");
        coolSquadNames.add("Echo");
        coolSquadNames.add("Foxtrot");
        coolSquadNames.add("Golf");
        coolSquadNames.add("Hotel");
        coolSquadNames.add("India");
        coolSquadNames.add("Juliet");
        coolSquadNames.add("Kilo");
        coolSquadNames.add("Lima");
        coolSquadNames.add("Mike");
        coolSquadNames.add("November");
        coolSquadNames.add("Oscar");
        coolSquadNames.add("Papa");
        coolSquadNames.add("Quebec");
        coolSquadNames.add("Romeo");
        coolSquadNames.add("Sierra");
        coolSquadNames.add("Tango");
        coolSquadNames.add("Uniform");
        coolSquadNames.add("Victor");
        coolSquadNames.add("Whiskey");
        coolSquadNames.add("X-ray");
        coolSquadNames.add("Yankee");
        coolSquadNames.add("Zulu");
    }

    public String squadID;
    public UnitCommandType currentCommand = UnitCommandType.Unknown;
    public SquadStatus status = SquadStatus.Assembling;
    public SquadType type = SquadType.Patrol;
    public BehaviorTree<Blackboard> behaviorTree;
    public List<Point2D> positionsTracking = new ArrayList<>();

    private List<UnitType> members = new ArrayList<>();

    public Squad(SquadType type, List<UnitType> members) {
        this.behaviorTree = RUtils.parseFile("squad.tree");
        this.type = type;
        this.members = members;
        Collections.shuffle(coolSquadNames);
        this.squadID = coolSquadNames.remove(0);
    }

    public boolean isAlive() {
        List<UnitDocument> units = new ArrayList<>(UnitsCenter.getSquadUnits(squadID));
        units.removeIf(u -> !u.isAlive);
        return !units.isEmpty();
    }

    public List<UnitDocument> getSquadUnits() {
        List<UnitDocument> units = UnitsCenter.getSquadUnits(squadID);
        return units;
    }

    public List<UnitDocument> getAliveMembers() {
        List<UnitDocument> units = new ArrayList<>(getSquadUnits());
        units.removeIf(u -> !u.isAlive);

        // fail save. if the squad has no alive members no more calculations are allows.
        // the behavior tree should be off and the squad retired.
        if (units.isEmpty())
            throw new IllegalStateException("No alive members in the squad.");
        return units;
    }

    /**
     * Recruit members for the squad based on the predefined list of unit types.
     */
    public void recruitMembers() {
        List<UnitType> requiredUnits = getRequiredUnits();
        for (UnitType unitType : requiredUnits) {
            UnitDocument unit = UnitsCenter.getDocument(unitType);
            if (unit != null) {
                unit.squadID = squadID;
            }
        }

        if (getRequiredUnits().isEmpty()) {
            status = SquadStatus.Assembled;
        }

        regroup(true);
    }

    /**
     * returna a list of unit types that are required for this squad based on the
     * predefined list of members and the current units in the squad.
     * 
     * @return - the needed unit types.
     */
    public List<UnitType> getRequiredUnits() {
        List<UnitDocument> freeUnits = new ArrayList<>(UnitsCenter.getDocuments());
        freeUnits.removeIf(u -> !"".equals(u.squadID));

        List<UnitType> myUnits = getSquadUnits().stream().map(u -> u.unitType).toList();

        List<UnitType> requiredUnits = new ArrayList<>(members);
        requiredUnits.removeAll(myUnits);

        return requiredUnits;
    }

    // TODO: change the merge strategy by resupply, so the squad can wait for new
    // fresh units
    public void mergeSquads(Squad sourceSquad) {
        List<UnitDocument> units = getSquadUnits();
        units.forEach(u -> u.squadID = squadID);
        this.squadID += "-" + sourceSquad.squadID;
        regroup(true);
    }

    public Position trackPosition() {
        Position position2 = getPosition();
        positionsTracking.add(new Point2D.Double(position2.x, position2.y));
        return position2;
    }

    public void performCommand(UnitCommandType commandType, Position position) {
        trackPosition();
        CommandQueue.addCommand(UnitCommandType.Right_Click_Position, this, position);
    }

    public boolean canAttackAir() {
        List<UnitDocument> units = getAliveMembers();
        for (UnitDocument unit : units) {
            if (unit.unitType.airWeapon().targetsAir())
                return true;
        }

        return false;
    }

    // TODO: delete?? i dont what to draw nothing in starcraft
    public void draw(Graphics2D g) {
        Position center = getPosition();

        Color color = RBWListener.game.self().getColor();
        RBWListener.game.drawCircle(CoordinateType.Map, 16 + center.x, 16 + center.y, getSpread(), color, false);
    }

    public int getSpread() {
        Rectangle2D box = getBox();
        double spread = Math.hypot(box.getX(), box.getY());

        return (int) spread;
    }

    public Rectangle2D getBox() {
        List<UnitDocument> units = getAliveMembers();
        List<Point2D> points = units.stream().<Point2D>map(u -> new Point2D.Double(u.position.x, u.position.y))
                .toList();
        Rectangle2D box = getBox(points);
        return box;
    }

    public static Rectangle2D getBox(List<Point2D> points) {
        Point2D point2d = points.get(0);
        Rectangle2D box = new Rectangle2D.Double(point2d.getX(), point2d.getY(), 0, 0);

        for (int i = 1; i < points.size(); i++) {
            Point2D point = points.get(i);
            box.add(point.getX(), point.getY());
        }
        return box;
    }

    public Position getPosition() {
        Rectangle2D box = getBox();
        Position position = new Position((int) box.getCenterX(), (int) box.getCenterY());

        return position;
    }

    public double getEnemyDistance() {
        double distance = 128;
        Position center = getPosition();
        for (UnitDocument enemy : UnitsCenter.getEnemies()) {
            Position enemyPosition = enemy.position;
            distance = Math.min(Point2D.distanceSq(center.x, center.y, enemyPosition.x, enemyPosition.x), distance);
        }

        return distance;
    }

    public void regroup(boolean attacking) {
        Position center = getPosition();
        if (attacking) {
            CommandQueue.addCommand(UnitCommandType.Attack_Move, this, center);
        } else {
            CommandQueue.addCommand(UnitCommandType.Right_Click_Position, this, center);

        }
    }

    /**
     * Line Formation (Frontal Assault / Defense)
     * Tactical Use: Maximizes firepower to the front.
     * Logic: Units spread out evenly perpendicular to the heading vector (left and
     * right of the leader).
     * 
     * @param squad - the squad to form
     * @param angle - the angle
     * @return the line
     */
    public static List<Position> calculateLine(Squad squad, double angle) {
        List<Position> line = new ArrayList<>();
        List<UnitDocument> units = squad.getAliveMembers();
        if (units.isEmpty())
            return line;

        Position leader = units.get(0).position;
        int space = 32;

        // perpendicular line
        double perpx = -Math.sin(angle);
        double perpy = Math.cos(angle);

        for (int i = 0; i < units.size(); i++) {
            double offset = i * space;
            double x = leader.x + perpx * offset;
            double y = leader.y + perpy * offset;
            line.add(new Position((int) x, (int) y));
        }

        return line;
    }

    /**
     * Column Formation (Single File Segment)
     * Logic: Constructs a column path where the first unit stands at the startPoint
     * and the last unit stands at the endPoint. The direction of the column is
     * defined entirely by the vector between your two points.
     * 
     * @param squad - the squad
     * @param angle - the direction
     * @return the formation
     */
    public static List<Position> calculateColumn(Squad squad, double angle) {
        List<Position> line = new ArrayList<>();
        List<UnitDocument> units = squad.getAliveMembers();
        if (units.isEmpty())
            return line;

        Position leader = units.get(0).position;
        int space = 32;

        // perpendicular line
        double dirx = Math.cos(angle);
        double diry = Math.sin(angle);

        for (int i = 0; i < units.size(); i++) {
            double offset = i * space;
            double x = leader.x + dirx * offset;
            double y = leader.y + diry * offset;
            line.add(new Position((int) x, (int) y));
        }

        return line;
    }

}
