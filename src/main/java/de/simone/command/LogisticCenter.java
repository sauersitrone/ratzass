package de.simone.command;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.hstairs.ppmajal.transition.TransitionGround;

import bwapi.Unit;
import bwapi.UnitType;
import de.simone.RBWListener;
import de.simone.RUtils;
import de.simone.StarCraftException;
import de.simone.btree.Blackboard;
import de.simone.command.CommandQueue.ResourceType;
import de.simone.command.StarCraftConstants.BuildActionName;
import de.simone.command.StarCraftConstants.OrderStatus;

/**
 * Coordinates unit and building production together with resource gathering.
 * Uses a PDDL planner to create build orders that move the game toward the
 * desired state.
 */
public class LogisticCenter {
    private static String domain;
    private static String problem;
    private static String planner;
    private static List<LogisticCenterListener> listeners = new ArrayList<>();
    private static List<BuildOrder> buildOrders = new ArrayList<>();
    private static RENHSP renhsp = new RENHSP(false);

    public static BehaviorTree<Blackboard> behaviorTree;

    static {
        domain = RUtils.getResourceFile("./starcraft-domain.pddl");
        // planner = "opt-blind";
        planner = "sat-hmrp";
        Blackboard blackboard = new Blackboard();
        behaviorTree = RUtils.getBehaviorTree("logistic.tree", blackboard);
    }

    public static boolean areMyOrdersReady(UnitType unitType, int quantity) {
        List<BuildOrder> list = buildOrders.stream().filter(o -> o.unitType == unitType && o.quantity == quantity)
                .toList();
        int ready = (int) list.stream().filter(o -> o.status == OrderStatus.Completed).count();
        return ready == list.size();
    }

    public static void onUnitComplete(Unit unit) {
        Optional<BuildOrder> optional = buildOrders.stream()
                .filter(ba -> ba.status == OrderStatus.Running && ba.unitType == unit.getType())
                .findFirst();

        if (optional.isPresent()) {
            optional.get().status = OrderStatus.Completed;
        }
    }

    /**
     * perform/coordinate the necessary logistics actions. This method will:
     * - ensure that the SCVs are working
     * - check if there are any pending build or train orders
     * - start the next pending order if possible.
     * - check if there is a need to build supply depots and add them to the build
     * order if necessary.
     * - notify all registered listeners about the updated build orders.
     */
    public static void heartBeat() {
        // ensure the scv are working
        Unit unit = UnitsCenter.getIdleTerranSCV();

        Unit refinery = UnitsCenter.getUnits().stream().filter(u -> u.getType() == UnitType.Terran_Refinery).findFirst()
                .orElse(null);
        if (unit != null) {
            int gGas = (int) UnitsCenter.getUnits().stream()
                    .filter(u -> u.getType() == UnitType.Terran_SCV && u.isGatheringGas()).count();
            if (gGas < StarCraftConstants.SCV_GATHERING_GAS && refinery != null) {
                CommandQueue.gather(ResourceType.Gas);
            }

            int gMinerals = (int) UnitsCenter.getUnits().stream()
                    .filter(u -> u.getType() == UnitType.Terran_SCV && u.isGatheringMinerals()).count();
            if (gMinerals < StarCraftConstants.SCV_GATHERING_MINERALS) {
                CommandQueue.gather(ResourceType.Mineral);
            }
        }

        // is there any pending gathering mineral action?
        Optional<BuildOrder> optional = buildOrders.stream()
                .filter(ba -> ba.action == BuildActionName.gather_Mineral && ba.status == OrderStatus.Pending)
                .findFirst();
        if (optional.isPresent()) {
            // if yes, check if is there enough minerals to complete the action.
            BuildOrder buildOrder = optional.get();
            if (RBWListener.currentMinerals >= buildOrder.quantity) {
                buildOrder.status = OrderStatus.Completed;
            } else {
                return;
            }
        }

        // is there any pending gathering gas action?
        optional = buildOrders.stream()
                .filter(ba -> ba.action == BuildActionName.gather_Gas && ba.status == OrderStatus.Pending)
                .findFirst();
        if (optional.isPresent()) {
            // if yes, check if is there enough minerals to complete the action.
            BuildOrder buildOrder = optional.get();
            if (RBWListener.currentGas >= buildOrder.quantity) {
                buildOrder.status = OrderStatus.Completed;
            } else {
                return;
            }
        }

        // star the next pending build or train action
        optional = buildOrders.stream()
                .filter(bo -> (bo.action == StarCraftConstants.BuildActionName.build
                        || bo.action == StarCraftConstants.BuildActionName.train)
                        && bo.status == OrderStatus.Pending)
                .findFirst();
        if (optional.isPresent()) {
            BuildOrder buildOrder = optional.get();
            // train
            if (buildOrder.action == StarCraftConstants.BuildActionName.train) {
                Command command = CommandQueue.train(buildOrder.unitType);
                buildOrder.message = command.message;
                buildOrder.status = OrderStatus.Running;
            }

            // build
            if (buildOrder.action == StarCraftConstants.BuildActionName.build) {
                Command command = CommandQueue.build(buildOrder.unitType);
                buildOrder.message = command.message;
                buildOrder.status = OrderStatus.Running;
            }
        }

        // if no one ist pending, build supply if needed. this hast hight priority
        optional = buildOrders.stream()
                .filter(o -> o.action == BuildActionName.build && o.unitType == UnitType.Terran_Supply_Depot
                        && (o.status == OrderStatus.Completed || o.status == OrderStatus.Running))
                .findFirst();
        if (!optional.isPresent() && RBWListener.currentSupplyLeft < StarCraftConstants.TERRAN_MIN_SUPPLY) {
            addBuildOrder(UnitType.Terran_Supply_Depot, 1, true);
        }

        for (LogisticCenterListener listener : listeners) {
            listener.updated(buildOrders);
        }
    }

    /**
     * create a plan for the specified unit type and quantity using the PDDL
     * planner. The plan or result of this method will be a list of actions to be
     * executed in order to achieve the desired state of the
     * game.
     * 
     * @param unitType - the type of unit
     * @param quantity - the number of units
     * @return the plan
     */
    public static List<BuildOrder> addBuildOrder(UnitType unitType, int quantity) {
        return addBuildOrder(unitType, quantity, false);
    }

    private static List<BuildOrder> addBuildOrder(UnitType unitType, int quantity, boolean highPriority) {
        // fail save
        Optional<BuildOrder> optional = buildOrders.stream()
                .filter(bo -> bo.unitType == unitType && bo.quantity == quantity
                        && (bo.status == OrderStatus.Pending || bo.status == OrderStatus.Running))
                .findFirst();
        if (optional.isPresent()) {
            throw new StarCraftException("An order for " + quantity + " of " + unitType + " is already in.");
        }

        // the order quantity express the desired number of units to be built. but the
        // planner needs the total number of units as the goal (desired + current). (e.g
        // if i want to build
        // 1 SCV, and i already have 1, the goal must be 2)
        int units = UnitsCenter.getUnitCount(unitType) + quantity;

        Pair<UnitType, Integer> pair = Pair.of(unitType, units);
        RPDDLProblem pddlProblem = new RPDDLProblem(pair);
        pddlProblem.printProblem = true;
        problem = pddlProblem.getPDDLProblem();

        List<String> plan = solve();
        List<BuildOrder> buildOrders2 = new ArrayList<>();
        if (plan == null || plan.isEmpty()) {
            throw new StarCraftException("No plan found for build order: " + unitType + " x" + quantity);
        } else {
            buildOrders2 = BuildOrder.getBuildOrders(plan);
        }

        // build order priority
        if (highPriority) {
            int i = 0;
            // find the first non-completet task
            for (i = 0; i < buildOrders.size(); i++) {
                BuildOrder order = buildOrders.get(i);
                if (order.status == OrderStatus.Pending || order.status == OrderStatus.Running)
                    break;
            }
            buildOrders.addAll(i, buildOrders2);
        } else {
            buildOrders.addAll(buildOrders2);
        }

        for (LogisticCenterListener listener : listeners) {
            listener.updated(buildOrders);
        }

        return buildOrders2;
    }

    private static List<String> solve() {
        String[] args1 = { "-o", domain, "-f", problem, "-planner", planner };
        renhsp.parseInput(args1);
        renhsp.configurePlanner();
        if (renhsp.parsingDomainAndProblem(args1)) {
            LinkedList<ImmutablePair<BigDecimal, TransitionGround>> plan = renhsp.planning();
            List<String> planStrings = plan.stream().map(inpair -> inpair.getRight().getName()).toList();
            return planStrings;
        } else {
            System.out.println("Error parsing domain and problem files.");
            return null;
        }
    }

    /**
     * Adds a listener to the logistic center that will be notified of updates to
     * the build orders.
     * 
     * @param listener - the listener
     */
    public static void addListener(LogisticCenterListener listener) {
        listeners.add(listener);
    }

    // public static void main(String[] args) {
    //     RPDDLProblem pddlProblem = new RPDDLProblem(Pair.of(UnitType.Terran_Bunker, 1));
    //     pddlProblem.isTest = true;
    //     pddlProblem.unitsTest.add(Pair.of(UnitType.Terran_Command_Center, 1));
    //     pddlProblem.unitsTest.add(Pair.of(UnitType.Terran_SCV, 1));
    //     LogisticCenter.problem = pddlProblem.getPDDLProblem();
    //    List<String> plan = LogisticCenter.solve();
    //    plan.forEach(System.out::println);
    // }
}
