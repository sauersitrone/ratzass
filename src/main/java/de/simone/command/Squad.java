package de.simone.command;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.ai.btree.BehaviorTree;

import bwapi.Color;
import bwapi.CoordinateType;
import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitCommandType;
import bwapi.UnitType;
import de.simone.RBWListener;
import de.simone.RUtils;
import de.simone.SimplifyPolyline;

/**
 * This class provides a structured way to manage a group of units as a single
 * tactical entity in the game.
 * 
 * although a Squad is a Military unit from 8 to 14 personnel (US/NATO doctrine:
 * 9 to 13 or ~12 soldiers) here is the representaion of a group of units in the
 * game. The size of this tactical unit is dictated by the fictional
 * {@link #SquadType}.
 * 
 */
public class Squad {
    public enum SquadStatus {
        None, Assembling, Assembled, Moving, Ready
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
    public static int patrolRadius = 32 * 10; // explore 10 tiles radius;

    public String squadID;
    public UnitCommandType currentCommand = UnitCommandType.Unknown;
    public SquadStatus status = SquadStatus.None;
    public SquadType type = SquadType.Patrol;
    public BehaviorTree<Squad> behaviorTree;

    // used to anotate the retreat position
    public Position targetPosition;

    private List<Point2D> positionsTracking = new ArrayList<>();
    private List<UnitType> members = new ArrayList<>();

    public Squad(SquadType type, List<UnitType> members) {
        this.behaviorTree = RUtils.getBehaviorTree("squad.tree", this);
        this.type = type;
        this.members = members;
        Collections.shuffle(coolSquadNames);
        this.squadID = coolSquadNames.remove(0);
    }

    public void updateStatus() {
        // update the status, so the running behavior tree has the correct context.
        if (isAlive())
            status = SquadStatus.None;

        // check if the squad has reached the target position
        if (targetPosition != null && isSquadInPosition(targetPosition))
            status = SquadStatus.Ready;

    }

    public boolean isAlive() {
        List<DogTag> units = new ArrayList<>(UnitsCenter.getSquadUnits(squadID));
        units.removeIf(u -> !u.isAlive);
        return !units.isEmpty();
    }

    /**
     * return all the units. dead or alive.
     * 
     * @return - all units
     */
    public List<DogTag> getSquadUnits() {
        List<DogTag> units = UnitsCenter.getSquadUnits(squadID);
        return units;
    }

    public List<DogTag> getAliveMembers() {
        List<DogTag> units = new ArrayList<>(getSquadUnits());
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
            DogTag unit = UnitsCenter.getDogTag(unitType);
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
     * return a list of unit types that are required for this squad based on the
     * predefined list of members and the current units in the squad.
     * 
     * @return - the needed unit types.
     */
    public List<UnitType> getRequiredUnits() {
        List<DogTag> freeUnits = new ArrayList<>(UnitsCenter.getDogTags());
        freeUnits.removeIf(u -> !"".equals(u.squadID));

        List<UnitType> myUnits = getSquadUnits().stream().map(u -> u.unitType).toList();

        List<UnitType> requiredUnits = new ArrayList<>(members);
        requiredUnits.removeAll(myUnits);

        return requiredUnits;
    }

    // TODO: change the merge strategy by resupply, so the squad can wait for new
    // fresh units
    public void mergeSquads(Squad sourceSquad) {
        List<DogTag> units = getSquadUnits();
        units.forEach(u -> u.squadID = squadID);
        this.squadID += "-" + sourceSquad.squadID;
        regroup(true);
    }

    public Position trackPosition() {
        Position position2 = getPosition();
        positionsTracking.add(new Point2D.Double(position2.x, position2.y));
        return position2;
    }

    public boolean isSquadInPosition(Position position) {
        Position center = getPosition();
        return Point2D.distanceSq(center.x, center.y, position.x, position.y) <= patrolRadius;
    }

    public void retreat() {
        targetPosition = getRetreatPosition(this, patrolRadius);
        CommandQueue.addCommand(UnitCommandType.Right_Click_Position, this, targetPosition);
        CombatCenter.sendCommunication(this, "Retreating ...");
        status = SquadStatus.Moving;
    }

    public void move(Position position) {
        trackPosition();
        targetPosition = new Position(position.x, position.y);
        CommandQueue.addCommand(UnitCommandType.Right_Click_Position, this, targetPosition);
        CombatCenter.sendCommunication(this, "Moving ...");
        status = SquadStatus.Moving;
    }

    public void attack(Position position) {
        trackPosition();
        CommandQueue.addCommand(UnitCommandType.Attack_Move, this, position);
        CombatCenter.sendCommunication(this, "Ohhhh YEAHHH !");
    }

    public boolean canAttackAir() {
        List<DogTag> units = getAliveMembers();
        for (DogTag unit : units) {
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
        List<Unit> units = getAliveMembers().stream().map(d -> d.unit).toList();
        Rectangle2D box = CombatCenter.getBox(units);
        double spread = Math.hypot(box.getX(), box.getY());

        return (int) spread;
    }

    public void regroup(boolean attacking) {
        Position center = getPosition();
        if (attacking) {
            CommandQueue.addCommand(UnitCommandType.Attack_Move, this, center);
        } else {
            CommandQueue.addCommand(UnitCommandType.Right_Click_Position, this, center);

        }
    }

    public Position getPosition() {
        List<Unit> units = getAliveMembers().stream().map(d -> d.unit).toList();
        Rectangle2D box = CombatCenter.getBox(units);
        Position position = new Position((int) box.getCenterX(), (int) box.getCenterY());

        return position;
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
        List<DogTag> units = squad.getAliveMembers();
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
        List<DogTag> units = squad.getAliveMembers();
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

    /**
     * return a retreat position for the given squad at the specified distance. the
     * retreat position is calculated based on the squad's recent movement history.
     * 
     * @param squad    - the squad
     * @param distance - the distance
     * @return - the position
     */
    private static Position getRetreatPosition(Squad squad, int distance) {
        List<Point2D> positions = new ArrayList<>(squad.positionsTracking);
        positions = SimplifyPolyline.simplify(positions, Squad.patrolRadius, false);

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

}
