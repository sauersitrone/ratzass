package de.simone.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import bwapi.Pair;
import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitType;
import de.simone.RBWListener;
import de.simone.UnitEvent;
import lombok.extern.java.Log;
import tech.tablesaw.api.Table;

/**
 * Represent the command center that keep track of all the units in the game.
 * UnitsCenter
 */
@Log
public class UnitsCenter {

    private static UnitsCenter instance;

    private ArrayList<Squad> squads = new ArrayList<Squad>();
    private Map<Integer, UnitDocument> unitDocuments = new TreeMap<>();
    private List<UnitsCenterListener> listeners = new ArrayList<>();
    public Table unitEventsTable;

    private UnitsCenter() {
        unitEventsTable = Table.create("Unit Events");
        UnitEvent.createColumns(unitEventsTable);
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

    public void addListener(UnitsCenterListener listener) {
        listeners.add(listener);
    }

    public void update() {
        List<Unit> units = RBWListener.game.getAllUnits();
        for (Unit unit : units) {
            UnitEvent unitEvent = new UnitEvent(unit);
            unitEvent.status = UnitEvent.EventType.CREATED;
            unitEvent.update(unitEventsTable);
        }

    }

    public void onUnitComplete(Unit unit) {
        UnitEvent unitEvent = new UnitEvent(unit);
        unitEvent.status = UnitEvent.EventType.CREATED;
        unitEvent.update(unitEventsTable);
        listeners.forEach(l -> l.updated(unitEventsTable));
    }

    public void onUnitDiscover(Unit unit) {
        UnitEvent unitEvent = new UnitEvent(unit);
        unitEvent.update(unitEventsTable);
        listeners.forEach(l -> l.updated(unitEventsTable));

        UnitDocument doc = new UnitDocument(unit);
        unitDocuments.put(doc.unitID, doc);
    }

    public void onUnitDestroy(Unit unit) {
        UnitEvent unitEvent = new UnitEvent(unit);
        unitEvent.status = UnitEvent.EventType.DESTROYED;
        unitEvent.update(unitEventsTable);
        listeners.forEach(l -> l.updated(unitEventsTable));

        UnitDocument doc = unitDocuments.get(unit.getID());
        if (doc != null)
            doc.isAlive = false;
    }

    //
    public List<UnitDocument> getDocuments() {
        return unitDocuments.values().stream().filter(u -> !u.isEnemy).toList();
    }

    //
    public List<UnitDocument> getDocuments(UnitType unitType) {
        return getDocuments().stream().filter(u -> !u.isEnemy && u.unitType == unitType).toList();
    }

    //
    public UnitDocument getDocument(UnitType unitType) {
        List<UnitDocument> units = getDocuments(unitType);
        if (!units.isEmpty())
            return units.getFirst();

        return null;
    }

    public List<UnitDocument> getEnemies() {
        return unitDocuments.values().stream().filter(u -> u.isEnemy).toList();
    }

    public int getEnemyUnitCount(UnitType unitType) {
        return (int) getEnemies().stream().filter(u -> u.unitType == unitType).count();
    }

    public List<UnitDocument> getEnemyUnits(UnitType unitType) {
        return getEnemies().stream().filter(u -> u.unitType == unitType).toList();
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

    //
    public List<UnitDocument> getSquadUnits(String squadID) {
        List<UnitDocument> list = getDocuments();
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
        Unit trainer = UnitsCenter.getUnit(whatBuilds.getKey());
        return trainer;
    }

    public static Unit getUnit(UnitType unitType) {
        List<Unit> units = getUnits();
        Unit unit = units.stream().filter(u -> u.getType() == unitType).findFirst().orElse(null);
        return unit;
    }

    public static List<Unit> getUnits() {
        boolean isAlly = !RBWListener.game.self().isEnemy(RBWListener.game.self());
        List<Unit> units = RBWListener.game.getAllUnits().stream().filter(u -> isAlly).toList();
        return units;
    }

    public static Unit getIdleTerranSCV() {
        Unit unit = getUnits().stream()
                .filter(u -> u.getType() == UnitType.Terran_SCV && u.isIdle()).findFirst().orElse(null);
        return unit;
    }

    public static Unit getFreeTerranSCV() {
        Unit unit = getUnits().stream()
                .filter(u -> u.getType() == UnitType.Terran_SCV
                        && (u.isGatheringGas() || u.isGatheringMinerals() || u.isIdle()))
                .findFirst().orElse(null);
        return unit;
    }

    public static int getUnitCount(UnitType unitType) {
        int count = (int) getUnits().stream().filter(u -> u.getType() == unitType).count();
        return count;
    }

    public static List<Unit> getEnemyUnits(Position center, int radious) {
        boolean isEnemy = RBWListener.game.self().isEnemy(RBWListener.game.self());
        List<Unit> units = RBWListener.game.getUnitsInRadius(center, 200);
        List<Unit> enemies = units.stream().filter(u -> isEnemy).toList();
        return enemies;
    }

    public static List<Unit> getEnemyUnits() {
        boolean isEnemy = RBWListener.game.self().isEnemy(RBWListener.game.self());
        List<Unit> units = RBWListener.game.getAllUnits().stream().filter(u -> isEnemy).toList();
        return units;
    }
}
