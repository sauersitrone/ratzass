package de.simone.command;

import java.awt.Point;
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

public class Squad {
    public enum SquadStatus {
        Building, Idle, Attack, Regroup, Retreat
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
    public SquadStatus status = SquadStatus.Building;
    public SquadType type = SquadType.Patrol;
    public BehaviorTree<Squad> behaviorTree;

    private int regroupTime = 0;
    private CommandQueue commandQueue;
    private UnitsCenter unitsCenter;
    private List<UnitType> members = new ArrayList<>();

    public Squad(SquadType type, List<UnitType> members) {
        this.behaviorTree = RUtils.parseFile("squad.tree", this);
        this.type = type;
        this.members = members;
        Collections.shuffle(coolSquadNames);
        this.squadID = coolSquadNames.remove(0);
        this.unitsCenter = UnitsCenter.getInstance();
        this.commandQueue = CommandQueue.getInstance();
    }

    // public int getSize() {
    // return members.size();
    // }

    public List<RUnit> getAliveMembers() {
        List<RUnit> units = unitsCenter.getSquadUnits(squadID);
        units.removeIf(u -> !u.isAlive);
        return units;
    }

    /**
     * Recruit members for the squad based on the predefined list of unit types.
     */
    public void recruitMembers() {
        List<UnitType> requiredUnits = getRequiredUnits();
        for (UnitType unitType : requiredUnits) {
            RUnit unit = unitsCenter.getUnit(unitType);
            if (unit != null) {
                unit.squadID = squadID;
            }
        }

        if (getRequiredUnits().isEmpty()) {
            status = SquadStatus.Idle;
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
        List<RUnit> freeUnits = unitsCenter.getUnits();
        freeUnits.removeIf(u -> !"".equals(u.squadID));

        List<UnitType> myUnits = unitsCenter.getSquadUnits(squadID).stream().map(u -> u.unitType).toList();

        List<UnitType> requiredUnits = new ArrayList<>(members);
        requiredUnits.removeAll(myUnits);

        return requiredUnits;
    }

    // terry
    public void mergeSquads(Squad sourceSquad) {
        List<RUnit> units = unitsCenter.getSquadUnits(sourceSquad.squadID);
        units.forEach(u -> u.squadID = squadID);
        this.squadID += "-" + sourceSquad.squadID;
        regroup(true);
    }

    public boolean getSpreadExceeded() {
        List<RUnit> units = unitsCenter.getSquadUnits(squadID);
        return getSpread() > units.size() * 30;
    }

    public boolean getSpreadFixed() {
        List<RUnit> units = unitsCenter.getSquadUnits(squadID);
        return (getSpread() < units.size() * 20) || (RBWListener.game.getFrameCount() > (regroupTime + 60));
    }

    public String getClosestSquadID(int x, int y) {
        String bestSquad = squadID;
        double closest = 256 * 256;

        for (Squad squad : unitsCenter.getSquads()) {
            Point center = squad.getCenter(false);

            if (center != null) {
                double distance = RUnit.distance(x, y, center.x, center.y);

                if (distance < closest) {
                    closest = distance;
                    bestSquad = squad.squadID;
                }
            }
        }
        return bestSquad;
    }

    public void patrol(Position position) {
        List<RUnit> units = unitsCenter.getSquadUnits(squadID);
        for (RUnit unit : units) {
            commandQueue.patrol(unit.unitID, position);
        }
    }

    public void retreat() {
        Point rally = StaffUtils.getRallyPoint(0, 0);
        double theta = Math.random() * 2.0 * Math.PI;

        List<RUnit> units = unitsCenter.getSquadUnits(squadID);
        for (RUnit unit : units) {
            commandQueue.rightClick(unit.unitID,
                    32 * rally.x + (int) (Math.cos(theta) * 3.5),
                    32 * rally.y + (int) (Math.sin(theta) * 3.5));
        }
    }

    public void setRegrouping(boolean regrouping) {
        regroupTime = RBWListener.game.getFrameCount();
        this.status = regrouping ? SquadStatus.Regroup : SquadStatus.Idle;
    }

    public void stopRetreat() {
        this.status = SquadStatus.Idle;
        regroup(false);
    }

    public boolean getCanAttackAir() {
        List<RUnit> units = unitsCenter.getSquadUnits(squadID);
        for (RUnit unit : units) {
            if (unit.unitType.airWeapon().targetsAir())
                return true;
        }

        return false;
    }

    // terry
    public void draw() {
        Point center = getCenter(true);
        if (center == null) {
            return;
        }

        RBWListener.game.drawText(CoordinateType.Map, center.x, center.y, getSquadSupply() + " - " + getEnemySupply());
        RBWListener.game.drawCircle(CoordinateType.Map, 16 + center.x, 16 + center.y, getSpread(), Color.Red, false);
    }

    // terry
    public int getSpread() {
        int minX = 256 * 32;
        int minY = 256 * 32;
        int maxX = 0;
        int maxY = 0;

        List<RUnit> units = unitsCenter.getSquadUnits(squadID);
        for (RUnit unit : units) {
            minX = Math.min(minX, unit.position.x);
            minY = Math.min(minY, unit.position.y);
            maxX = Math.max(maxX, unit.position.x);
            maxY = Math.max(maxY, unit.position.y);
        }

        return Math.max(maxX - minX, maxY - minY) / 2;
    }

    /**
     * Get the center point of the squad. If `real` is true, returns the center in
     * real coordinates; otherwise, returns the center in tile coordinates.
     * 
     * @param real - real coordinateos or tile coordinates
     * @return the center
     */
    public Point getCenter(boolean real) {
        int x = 0;
        int y = 0;
        List<RUnit> units = unitsCenter.getSquadUnits(squadID);
        if (units.isEmpty())
            return null;

        for (RUnit unit : units) {
            if (real) {
                x += unit.position.x;
                y += unit.position.y;
            } else {
                x += unit.position.x / 32;
                y += unit.position.y / 32;
            }
        }

        return new Point(x / units.size(), y / units.size());
    }

    /**
     * terry
     * Distance from the center of squad to nearest enemy unit.
     */
    public double getEnemyDistance() {
        double distance = 128;

        Point center = getCenter(false);
        if (center == null) {
            return distance;
        }

        UnitsCenter enemyCenter = unitsCenter;

        for (RUnit unit : enemyCenter.getUnits()) {
            double dx = unit.position.x - center.x;
            double dy = unit.position.y - center.y;
            distance = Math.min(Math.sqrt(dx * dx + dy * dy), distance);
        }

        return distance;
    }

    /**
     * terry
     * 
     * @return
     */
    public double getBaseDistance() {
        double distance = 128;

        Point center = getCenter(false);
        if (center == null) {
            return distance;
        }

        for (RUnit unit : unitsCenter.getUnits(UnitType.Terran_Command_Center)) {
            double dx = unit.position.x - center.x;
            double dy = unit.position.y - center.y;
            distance = Math.min(Math.sqrt(dx * dx + dy * dy), distance);
        }

        return distance;
    }

    // TERRY: is this not a btree task?
    public void regroup(boolean groupCasters) {
        Point center = getCenter(true);
        if (center == null) {
            return;
        }

        List<RUnit> units = unitsCenter.getSquadUnits(squadID);
        for (RUnit unit : units) {
            if (groupCasters) {
                commandQueue.rightClick(unit.unitID, center.x, center.y);
            } else {
                commandQueue.attackMove(unit.unitID, center.x, center.y);
            }
        }
    }

    public int getSquadSupply() {
        int supply = 0;
        List<RUnit> units = unitsCenter.getSquadUnits(squadID);
        for (RUnit unit : units)
            supply += unit.unitType.supplyRequired() / 2;

        return supply;
    }

    public int getEnemySupply() {
        int threat = 0;

        Point center = getCenter(false);
        if (center == null) {
            return threat;
        }

        List<RUnit> enemyUnits = unitsCenter.getUnits();
        for (RUnit unit : enemyUnits) {
            if (unit.unitType.isWorker()) {
                continue;
            }

            double dx = unit.position.x - center.x;
            double dy = unit.position.y - center.y;

            if (Math.sqrt(dx * dx + dy * dy) < 16) {
                threat += unit.unitType.supplyRequired();

                if (unit.unitType == UnitType.Protoss_Photon_Cannon
                        || unit.unitType == UnitType.Terran_Bunker
                        || unit.unitType == UnitType.Zerg_Sunken_Colony) {
                    threat += 4;
                }
            }
        }

        return threat / 2;
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
        List<RUnit> units = squad.getAliveMembers();
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
        List<RUnit> units = squad.getAliveMembers();
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
