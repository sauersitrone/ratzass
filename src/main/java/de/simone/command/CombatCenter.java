package de.simone.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import com.badlogic.gdx.ai.btree.BehaviorTree;

import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitType;
import de.simone.command.CombatOrder.PositionName;
import de.simone.command.StarCraftConstants.OrderPriority;
import de.simone.command.StarCraftConstants.OrderStatus;

/**
 * centralize the combat operations and communications between squad and the
 * rest of the military
 * 
 */
public class CombatCenter {
    private static CombatCenter instance;

    private List<CombatOrder> orders = new ArrayList<>();
    private List<CombatCenterListener> listeners = new ArrayList<>();
    private List<Squad> squads = new ArrayList<>();
    private Map<String, Position> namedAreas = new TreeMap<>();

    public BehaviorTree<CombatCenter> behaviorTree;

    public static CombatCenter getInstance() {
        if (instance == null) {
            instance = new CombatCenter();
            return instance;
        }
        return instance;
    }

    public static void init() {
        getInstance();
    }

    private CombatCenter() {
        // this.behaviorTree = RUtils.parseFile("dog.tree", this);
    }

    public void addSquad(Squad squad) {
        squads.add(squad);
    }

    public Position getArea(String areaName) {
        return namedAreas.get(areaName);
    }

    /**
     * call by RBWListener every x seconds. this method will:
     * 
     */
    public void update() {
        // update named areas
        Unit unit = UnitsCenter.getUnit(UnitType.Terran_Command_Center);
        namedAreas.put("RetreatArea", unit.getPosition());

        for (CombatCenterListener listener : listeners) {
            listener.updated(orders);
        }
    }

    public void addOrder(CombatOrder combatOrder) {
        // to avoid creating the same order, check if the order already exists in the
        // list
        Optional<CombatOrder> optional = orders.stream()
                .filter(co -> co.remitent.equals(combatOrder.remitent) && co.status == OrderStatus.Pending).findFirst();
        if (!optional.isPresent()) {
            orders.add(combatOrder);
        }

        // order priority
        if (combatOrder.priority == OrderPriority.High) {
            orders.add(0, combatOrder);
        } else {
            orders.add(combatOrder);
        }

        for (CombatCenterListener listener : listeners) {
            listener.updated(orders);
        }
    }

    public List<CombatOrder> getOrders(PositionName positionName) {
        return orders.stream().filter(co -> co.positionName == positionName && co.status == OrderStatus.Pending)
                .toList();
    }

    public void addListener(CombatCenterListener listener) {
        listeners.add(listener);
    }

}
