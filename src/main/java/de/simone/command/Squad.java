package de.simone.command;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.ai.btree.BehaviorTree;

import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitCommandType;
import bwapi.UnitType;
import de.simone.RUtils;

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
        None, Assembling, Moving, Ready, Dead
    }

    public enum SquadType {
        Patrol, Strike, Combat
    }

    public String squadID;
    public UnitCommandType currentCommand = UnitCommandType.Unknown;
    public SquadStatus status = SquadStatus.None;
    public SquadType type = SquadType.Patrol;
    public BehaviorTree<Squad> behaviorTree;

    // used to anotate the position of the squad movement
    public Position targetPosition;

    private List<Position> positionsTracking = new ArrayList<>();
    private List<UnitType> members = new ArrayList<>();

    public Squad(SquadType type, List<UnitType> members) {
        this.behaviorTree = RUtils.getBehaviorTree("squad.tree", this);
        this.type = type;
        this.members = members;
        Collections.shuffle(StarCraftConstants.coolSquadNames);
        this.squadID = StarCraftConstants.coolSquadNames.remove(0);
    }

    public void updateStatus() {
        // update the status, so the running behavior tree has the correct context.
        if (!isAlive()) {
            status = SquadStatus.Dead;
            return;
        }

        // check if the squad has reached the target position
        if (targetPosition != null && isSquadInPosition(targetPosition))
            status = SquadStatus.Ready;

    }

    public boolean isAlive() {
        List<DogTag> units = new ArrayList<>(UnitsCenter.getSquadUnits(squadID));
        return !units.isEmpty();
    }

    /**
     * return all the alive units.
     * 
     * @return - all units
     */
    public List<DogTag> getSquadUnits() {
        List<DogTag> units = UnitsCenter.getSquadUnits(squadID);
        return units;
    }

    /**
     * Recruit members for the squad based on the predefined list of unit types.
     */
    public void recruitMembers() {
        List<UnitType> requiredUnits = getRequiredUnits();
        for (UnitType unitType : requiredUnits) {
            DogTag unit = UnitsCenter.getDogTags().stream().filter(u -> u.unitType == unitType && "".equals(u.squadID))
                    .findFirst()
                    .orElse(null);
            if (unit != null) {
                unit.squadID = squadID;
            }
        }
        status = SquadStatus.Assembling;
        requiredUnits = getRequiredUnits();
        if (requiredUnits.isEmpty()) {
            status = SquadStatus.Ready;
            UnitsCenter.addSquad(this);
        }
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
        positionsTracking.add(position2);
        return position2;
    }

    /**
     * Checks if the squad is within a certain distance of the given position. if
     * yes, the squad is considered to be in position.
     * 
     * @param position - the position
     * @return - true or false
     */
    public boolean isSquadInPosition(Position position) {
        Position center = getPosition();
        return Point2D.distanceSq(center.x, center.y, position.x, position.y) <= 2 * TilePosition.SIZE_IN_PIXELS
                * TilePosition.SIZE_IN_PIXELS;
    }

    public void retreat() {
        // targetPosition = getRetreatPosition(this);
        targetPosition = positionsTracking.remove(positionsTracking.size() - 1);
        CommandQueue.addCommand(UnitCommandType.Right_Click_Position, this, targetPosition);
        CombatCenter.sendCommunication(this, UnitCommandType.Right_Click_Position);
        status = SquadStatus.Moving;
    }

    public void move(Position position) {
        trackPosition();
        targetPosition = new Position(position.x, position.y);
        CommandQueue.addCommand(UnitCommandType.Right_Click_Position, this, targetPosition);
        CombatCenter.sendCommunication(this, UnitCommandType.Right_Click_Position);
        status = SquadStatus.Moving;
    }

    public void attack(Position position) {
        trackPosition();
        targetPosition = new Position(position.x, position.y);
        CommandQueue.addCommand(UnitCommandType.Attack_Move, this, targetPosition);
        CombatCenter.sendCommunication(this, UnitCommandType.Attack_Move);
        status = SquadStatus.Moving;
    }

    public boolean canAttackAir() {
        List<DogTag> units = getSquadUnits();
        for (DogTag unit : units) {
            if (unit.unitType.airWeapon().targetsAir())
                return true;
        }

        return false;
    }

    public int getSpread() {
        List<Unit> units = getSquadUnits().stream().map(d -> d.unit).toList();
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
        List<Unit> units = getSquadUnits().stream().map(d -> d.unit).toList();
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
        List<DogTag> units = squad.getSquadUnits();
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
        List<DogTag> units = squad.getSquadUnits();
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
     * return a retreat position for the given squad. the retreat position is
     * calculated based on the squad's recent movement history.
     * 
     * TODO: check this method. the logic was based on random walkable positions.
     * now i explore region center with path. this method must retunr only the
     * nearest point of the already explored path.
     * 
    private static Position getRetreatPosition(Squad squad) {
        List<Point2D> positions = new ArrayList<>(squad.positionsTracking);
        // positions = SimplifyPolyline.simplify(positions, Squad.patrolRadius, false);
        positions = SimplifyPolyline.simplify(positions, 100, false);

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
            if (count >= 100)
                break;
        }

        return new Position((int) prevPoint.getX(), (int) prevPoint.getY());
    }
     */

}
