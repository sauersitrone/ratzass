package de.simone.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import bwapi.Pair;
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

    public static RUnit resolveTrainer(UnitType unitType) {
        for (UnitType unitType2 : UnitType.values()) {
            Pair<UnitType, Integer> whatBuilds = unitType2.whatBuilds();
            if (whatBuilds.getKey() == unitType)
                return UnitsCenter.getInstance().getUnit(whatBuilds.getKey());
        }
        return null;
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

    public static int getUnitCount(UnitType unitType) {
       return (int) RBWListener.game.getAllUnits().stream()
                .filter(u -> !RBWListener.game.self().isEnemy(u.getPlayer()) && u.getType() == unitType).count();
    }

    public int getEnemyUnitCount(UnitType unitType) {
        return (int) currentUnits.values().stream().filter(u -> u.isEnemy && u.unitType == unitType).count();
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

    public static Unit getIdleTerranSCV() {
        Unit unit = RBWListener.game.getAllUnits().stream()
                .filter(u -> u.getType() == UnitType.Terran_SCV && u.isIdle()).findFirst().orElse(null);
        return unit;
    }

    public static Unit getFreeTerranSCV() {
        Unit unit = RBWListener.game.getAllUnits().stream()
                .filter(u -> u.getType() == UnitType.Terran_SCV && (u.isGatheringGas() || u.isGatheringMinerals()))
                .findFirst().orElse(null);
        return unit;
    }

    public void addSquad(Squad squad) {
        squads.add(squad);
    }

    public List<Squad> getSquads() {
        return new ArrayList<>(squads);
    }

    /**
     * Get the largest squad that has size less than or equal to the given size.
     * 
     * @param size - the size
     * @return the squad
     */
    public Squad getSquads(int size) {
        List<Squad> squads = getSquads();
        if (squads.isEmpty()) {
            return null;
        }
        // reverse
        squads.sort((s1, s2) -> Integer.compare(s2.getAliveMembers().size(), s1.getAliveMembers().size()));

        squads.removeIf(s -> s.getAliveMembers().size() > size);
        return squads.isEmpty() ? null : squads.get(0);
    }

    /**
     * return all members of the squad (death or alives)
     * 
     * @param squadID - the id
     * @return the members
     */
    public List<RUnit> getSquadUnits(String squadID) {
        return currentUnits.values().stream().filter(u -> u.squadID.equals(squadID)).toList();
    }

}
