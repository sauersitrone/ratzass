package de.simone.command;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bwapi.Color;
import bwapi.CoordinateType;
import bwapi.UnitCommandType;
import bwapi.UnitType;
import de.simone.RBWListener;

public class Squad {
    public enum SquadStatus {
        BUILDING, IDLE, ATTACK, REGROUP, RETREAT
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
    public SquadStatus status = SquadStatus.IDLE;
    private int regroupTime = 0;
    private CommandQueue commandQueue;
    private UnitsCenter unitsCenter;
    private int maxSquadSize = 8;

    public Squad() {
        Collections.shuffle(coolSquadNames);
        this.squadID = coolSquadNames.remove(0);
        this.unitsCenter = UnitsCenter.getInstance();
        this.commandQueue = CommandQueue.getInstance();
    }

    /**
     * TODO: btree task: this method will be invoqued by commandQueue to form the
     * squad with the
     * units that are not in any squad yet. It will select the units that can attack
     * and at least one medic if possible.
     */
    public void recruitMembers() {
        this.status = SquadStatus.BUILDING;
        List<RUnit> units = unitsCenter.getUnits();
        units.removeIf(u -> !"".equals(u.squadID));
        boolean hasMedic = units.stream()
                .anyMatch(u -> u.squadID.equals(squadID) && u.unitType == UnitType.Terran_Medic);

        if (units.size() == maxSquadSize) {
            status = SquadStatus.IDLE;
            return;
        }

        int max = Math.min(maxSquadSize, units.size());
        for (int i = 0; i < max; i++) {
            RUnit unit = units.get(i);

            // at least select a medic
            if (unit.unitType == UnitType.Terran_Medic && !hasMedic) {
                unit.squadID = squadID;
                hasMedic = true;
                continue;
            }

            // if you can fight, wellcome to the squad
            if (unit.unitType.canAttack())
                unit.squadID = squadID;
        }

        regroup(true);
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
        this.status = regrouping ? SquadStatus.REGROUP : SquadStatus.IDLE;
    }

    public void stopRetreat() {
        this.status = SquadStatus.IDLE;
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
     * return the center of this squad, either in real coordinates or in tile
     * coordinates
     * 
     * @param real
     * @return
     */
    public Point getCenter(boolean real) {
        int count = 0;
        int x = 0;
        int y = 0;

        List<RUnit> units = unitsCenter.getSquadUnits(squadID);
        for (RUnit unit : units) {
            count++;
            if (real) {
                x += unit.position.x;
                y += unit.position.y;
            } else {
                x += unit.position.x / 32;
                y += unit.position.y / 32;
            }
        }

        if (count == 0) {
            return null;
        } else {
            return new Point(x / count, y / count);
        }
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

}
