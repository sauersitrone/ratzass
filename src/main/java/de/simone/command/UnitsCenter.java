package de.simone.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bridj.cpp.std.list;

import bwapi.Unit;
import bwapi.UnitType;
import de.simone.RBWListener;
import lombok.extern.java.Log;

/**
 * Represent the command center that keep track of all the units in the game.
 * UnitsCenter
 */
@Log
public class UnitsCenter {

    private static UnitsCenter instance;

    private ArrayList<Squad> squads = new ArrayList<Squad>();
    private Map<UnitType, Integer> currentUnitCounts = new TreeMap<>();
    private Map<Integer, RUnit> currentUnits = new TreeMap<>();
    private List<BuildOrder> buildOrders = new ArrayList<>();

    private UnitsCenter() {
        //
    }

    public static UnitsCenter getInstance() {
        if (instance == null) {
            instance = new UnitsCenter();
        }
        return instance;
    }

    public static void init() {
        getInstance();
    }

    public void onUnitComplete(Unit unit) {
        onUnitDiscover(unit);
    }

    public void onUnitDiscover(Unit unit) {
        RUnit rUnit = new RUnit(unit);
        rUnit.isEnemy = RBWListener.game.self().isEnemy(unit.getPlayer());
        currentUnits.put(rUnit.unitID, rUnit);
        int count = currentUnitCounts.getOrDefault(rUnit.unitType, 0);
        currentUnitCounts.put(rUnit.unitType, count + 1);
    }

    public void onUnitDestroy(Unit unit) {
        RUnit rUnit = new RUnit(unit);
        rUnit.isAlive = false;
        currentUnits.remove(rUnit.unitID);
        int count = currentUnitCounts.getOrDefault(rUnit.unitType, 1);
        currentUnitCounts.put(rUnit.unitType, count - 1);
    }

    public void buildUnit(String remitent, UnitType unitType, int quantity) {
        BuildOrder buildOrder = new BuildOrder(remitent, unitType, quantity);
        buildOrders.add(buildOrder);
        LogisticCenter.getInstance().createBuildPlan(buildOrder);
        CommandQueue commandQueue = CommandQueue.getInstance();


    }

    public RUnit getUnit(UnitType... unitTypes) {
        for (UnitType unitType : unitTypes) {
            RUnit unit = getUnit(unitType);
            if (unit != null)
                return unit;
        }
        return null;
    }

    public RUnit getUnit(UnitType unitType) {
        int count = currentUnitCounts.getOrDefault(unitType, 0);
        if (count == 0)
            return null;

        RUnit unit = currentUnits.values().stream().filter(u -> !u.isEnemy && u.unitType == unitType).findFirst().get();
        return unit;
    }

    public int getUnitCount(UnitType unitType) {
        return (int) currentUnits.values().stream().filter(u -> !u.isEnemy && u.unitType == unitType).count();
    }

    public int getEnemyUnitCount(UnitType unitType) {
        return (int) currentUnits.values().stream().filter(u -> u.isEnemy && u.unitType == unitType).count();
    }

    public List<RUnit> getUnits(UnitType unitType, int count) {
        return currentUnits.values().stream().filter(u -> !u.isEnemy && u.unitType == unitType).limit(count).toList();
    }

    public List<RUnit> getEnemyUnits(UnitType unitType, int count) {
        return currentUnits.values().stream().filter(u -> u.isEnemy && u.unitType == unitType).limit(count).toList();
    }

    public List<RUnit> getUnits(UnitType unitType) {
        return currentUnits.values().stream().filter(u -> !u.isEnemy && u.unitType == unitType).toList();
    }

    public List<RUnit> getEnemyUnits(UnitType unitType) {
        return currentUnits.values().stream().filter(u -> u.isEnemy && u.unitType == unitType).toList();
    }

    public List<RUnit> getUnits() {
        List<RUnit> units = currentUnits.values().stream().filter(u -> !u.isEnemy).toList();
        return units;
    }

    public List<RUnit> getEnemyUnits() {
        List<RUnit> units = currentUnits.values().stream().filter(u -> u.isEnemy).toList();
        return units;
    }

    public List<Squad> getSquads() {
        return new ArrayList<>(squads);
    }

    public List<RUnit> getSquadUnits(String squadID) {
        return currentUnits.values().stream().filter(u -> u.squadID.equals(squadID)).toList();
    }

}
