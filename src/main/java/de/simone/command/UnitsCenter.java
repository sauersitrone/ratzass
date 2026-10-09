package de.simone.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import bwapi.Order;
import bwapi.Pair;
import bwapi.Position;
import bwapi.TechType;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.UpgradeType;

/**
 * Manages and tracks all units, unit events, and combat squads.
 * Provides access to friendly and enemy units and notifies listeners about unit
 * event updates.
 */
public class UnitsCenter {
    private static ArrayList<Squad> squads = new ArrayList<Squad>();
    private static Map<Integer, DogTag> dogTags = new TreeMap<>();
    private static List<UnitsCenterListener> listeners = new ArrayList<>();

    public static void addListener(UnitsCenterListener listener) {
        listeners.add(listener);
    }

    /**
     * Call by RBWListener on every x frames to update the status of all units.
     */
    public static void controlPersonal() {
        // List<Unit> units = RBWListener.game.getAllUnits();

        // remove dead squads
        squads.removeIf(s -> !s.isAlive());

        listeners.forEach(l -> l.updatePersonal(getDogTags()));
    }

    public static void onUnitComplete(Unit unit) {
        DogTag tag = new DogTag(unit);
        dogTags.put(tag.unit.getID(), tag);
        listeners.forEach(l -> l.updatePersonal(getDogTags()));
    }

    public static void onUnitDiscover(Unit unit) {
        DogTag tag = new DogTag(unit);
        dogTags.put(tag.unit.getID(), tag);
        listeners.forEach(l -> l.updatePersonal(getDogTags()));
    }

    public static void onUnitDestroy(Unit unit) {
        DogTag tag = dogTags.get(unit.getID());
        if (tag != null)
            tag.isAlive = false;

        listeners.forEach(l -> l.updatePersonal(getDogTags()));
    }

    /**
     * Get all dog tags of friendly units.
     * 
     * @return - the units
     */
    public static List<DogTag> getDogTags() {
        List<DogTag> dogTags2 = dogTags.values().stream().filter(u -> !u.isEnemy).toList();
        dogTags2.stream().filter(d -> d.unit.isCompleted()).toList();
        // TODO: reviw the code. entweder unodificable list oder nie remove( ausfüren)
        return dogTags2;
    }

    /**
     * Get all dog tags of friendly units of the specified unit type.
     * 
     * @param unitType - the unit type
     * @return - the units
     */
    public static List<DogTag> getDogTags(UnitType unitType) {
        return getDogTags().stream().filter(u -> !u.isEnemy && u.unitType == unitType).toList();
    }

    //
    public static DogTag getDogTag(UnitType unitType) {
        List<DogTag> units = getDogTags(unitType);
        if (!units.isEmpty())
            return units.getFirst();

        return null;
    }

    public static List<DogTag> getEnemies() {
        return dogTags.values().stream().filter(u -> u.isEnemy).toList();
    }

    public static int getEnemyUnitCount(UnitType unitType) {
        return (int) getEnemies().stream().filter(u -> u.unitType == unitType).count();
    }

    public static List<DogTag> getEnemyUnits(UnitType unitType) {
        return getEnemies().stream().filter(u -> u.unitType == unitType).toList();
    }

    public static void addSquad(Squad squad) {
        squads.add(squad);
    }

    public static List<Squad> getSquads() {
        return new ArrayList<>(squads);
    }

    /**
     * Get the largest squad that has size less than or equal to the given size.
     * 
     * @param size - the size
     * @return the squad
     */
    public static Squad getSquads(int size) {
        List<Squad> squads = getSquads();
        if (squads.isEmpty()) {
            return null;
        }
        // reverse
        squads.sort((s1, s2) -> Integer.compare(s2.getAliveMembers().size(), s1.getAliveMembers().size()));

        squads.removeIf(s -> s.getAliveMembers().size() > size);
        return squads.isEmpty() ? null : squads.get(0);
    }

    //
    public static List<DogTag> getSquadUnits(String squadID) {
        List<DogTag> list = getDogTags();
        return list.stream().filter(u -> u.squadID.equals(squadID)).toList();
    }

    public static Unit getUnit(UnitType... unitTypes) {
        for (UnitType unitType : unitTypes) {
            Unit unit = getUnit(unitType);
            if (unit != null)
                return unit;
        }
        return null;
    }

    public static Unit resolveTrainer(UnitType unitType) {
        Pair<UnitType, Integer> whatBuilds = unitType.whatBuilds();
        List<Unit> units = new ArrayList<>(UnitsCenter.getUnits(whatBuilds.getKey()));
        units.removeIf(u -> u.isTraining() && u.getTrainingQueueCount() == 5);
        Unit trainer = units.isEmpty() ? null : units.get(0);
        return trainer;
    }

    public static Unit resolveResearch(TechType techType) {
        UnitType unitType = techType.whatResearches();
        Unit trainer = UnitsCenter.getUnit(unitType);
        return trainer;
    }

    public static Unit resolveUpgrade(UpgradeType upgradeType) {
        UnitType unitType = upgradeType.whatUpgrades();
        Unit trainer = UnitsCenter.getUnit(unitType);
        return trainer;
    }

    /**
     * Returns a random unit of the specified type. the randomization is used to
     * avoid that the same unit is always used for the same task.
     * 
     * @param unitType - the type
     * @return the unit
     */
    public static Unit getUnit(UnitType unitType) {
        List<Unit> units = getUnits(unitType);
        Collections.shuffle(units);
        Unit unit = units.isEmpty() ? null : units.get(0);
        return unit;
    }

    public static List<Unit> getUnits(UnitType unitType) {
        List<Unit> units = getUnits();
        List<Unit> units2 = units.stream().filter(u -> u.getType() == unitType).toList();
        return units2;
    }

    /**
     * Get a list of all units controlled by the player. the method returns only
     * units that are completed and not dead.
     * 
     * @return the list
     */
    public static List<Unit> getUnits() {
        boolean isEnemy = RBWListener.game.self().isEnemy(RBWListener.game.self());
        List<Unit> units = RBWListener.game.getAllUnits().stream()
                .filter(u -> !isEnemy && u.isCompleted()).toList();
        return units;
    }

    /**
     * Get a free SCV that is idle and not currently executing any orders.
     * 
     * @return the scv
     */
    public static Unit getSCVForGather() {
        List<Unit> units = getUnits().stream()
                .filter(u -> u.getType() == UnitType.Terran_SCV && u.isIdle() && u.getOrder() == Order.None)
                .toList();
        List<Unit> units2 = new ArrayList<>(units);
        Collections.shuffle(units2);
        Unit unit = units2.isEmpty() ? null : units2.get(0);
        return unit;
    }

    public static Unit getFreeTerranSCV() {
        List<Unit> units = getUnits().stream()
                .filter(u -> u.getType() == UnitType.Terran_SCV
                        && (u.isGatheringGas() || u.isGatheringMinerals() || u.isIdle()))
                .toList();
        List<Unit> units2 = new ArrayList<>(units);
        Collections.shuffle(units2);
        Unit unit = units2.isEmpty() ? null : units2.get(0);
        return unit;
    }

    /**
     * Get the count of units of the specified type controlled by the player.
     * 
     * @param unitType - the type of unit
     * @return the count
     */
    public static int getUnitCount(UnitType unitType) {
        int count = (int) getUnits().stream().filter(u -> u.getType() == unitType).count();
        return count;
    }

    /**
     * Return a list of enemy units currently in sight of the given unit.
     * 
     * @param unit - the unit
     * @return - the enemies
     */
    public static List<Unit> getEnemyUnits(Unit unit) {
        Position center = unit.getPosition();
        List<Unit> units = RBWListener.game.getUnitsInRadius(center, unit.getType().sightRange());
        List<Unit> enemies = units.stream().filter(u -> u.getPlayer().isEnemy(RBWListener.game.self())).toList();
        return enemies;
    }

    // public static List<Unit> getEnemyUnits(Position center, int radious) {
    // boolean isEnemy = RBWListener.game.self().isEnemy(RBWListener.game.self());
    // List<Unit> units = RBWListener.game.getUnitsInRadius(center, radious);
    // List<Unit> enemies = units.stream().filter(u -> isEnemy).toList();
    // return enemies;
    // }

    public static List<Unit> getEnemyUnits() {
        List<Unit> units = RBWListener.game.getAllUnits().stream()
                .filter(u -> u.getPlayer().isEnemy(RBWListener.game.self())).toList();
        return units;
    }

    /**
     * Returns the closest unit from the given list to the specified unit.
     * 
     * @param units - the list of units
     * @param unit  - the reference unit
     * @return the closest unit
     */
    public static Unit getClosestUnit(List<Unit> units, Unit unit) {
        Unit closestUnit = null;
        for (Unit unit2 : units) {
            if (closestUnit == null
                    || unit2.getDistance(unit.getPosition()) < closestUnit.getDistance(unit.getPosition())) {
                closestUnit = unit2;
            }
        }
        return closestUnit;
    }
}
