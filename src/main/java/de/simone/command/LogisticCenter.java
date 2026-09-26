package de.simone.command;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private static ExecutorService executorService;
    private static int voucherIdx = 0;
    // private static Future<List<BuildOrder>> futureOrder;
    public static Map<String, List<BuildOrder>> orders = new TreeMap<>();
    public final static BehaviorTree<Blackboard> behaviorTree;

    static {
        domain = RUtils.getResourceFile("./starcraft-domain.pddl");
        // planner = "opt-blind";
        planner = "sat-hmrp";
        Blackboard blackboard = new Blackboard();
        behaviorTree = RUtils.getBehaviorTree("logistic.tree", blackboard);
        executorService = Executors.newSingleThreadExecutor();
    }

    public static void onUnitComplete(Unit unit) {
        // silent return if a enemy unit scaut my position
        if (unit.getPlayer().isEnemy(RBWListener.game.self()))
            return;

        Optional<BuildOrder> optional = buildOrders.stream()
                .filter(ba -> ba.getStatus() == OrderStatus.Running && ba.unitType == unit.getType())
                .findFirst();

        if (optional.isPresent()) {
            BuildOrder order = optional.get();
            order.setStatus(OrderStatus.Completed);
        }
    }

    /**
     * perform/coordinate the necessary logistics actions. This method will:
     * - ensure that the SCVs are working
     * - check if there are any pending build or train orders
     * - start the next queued order if possible.
     * order if necessary.
     * - notify all registered listeners about the updated build orders.
     */
    public static void heartBeat() {
        /**
         * ensure the scv are working
         */
        Unit unit = UnitsCenter.getIdleTerranSCV();
        if (unit != null) {
            // at least 2 gathering gas
            Unit refinery = UnitsCenter.getUnits().stream().filter(u -> u.getType() == UnitType.Terran_Refinery)
                    .findFirst().orElse(null);
            int gGas = (int) UnitsCenter.getUnits().stream()
                    .filter(u -> u.getType() == UnitType.Terran_SCV && u.isGatheringGas()).count();
            if (gGas < StarCraftConstants.SCV_GATHERING_GAS && refinery != null) {
                CommandQueue.gather(ResourceType.Gas);
            }

            // the rest, minerals
            CommandQueue.gather(ResourceType.Mineral);
        }

        /**
         * is there any queued gathering mineral action?
         */
        Optional<BuildOrder> optional = buildOrders.stream()
                .filter(ba -> ba.action == BuildActionName.gather_Mineral
                        && (ba.getStatus() == OrderStatus.Queued || ba.getStatus() == OrderStatus.Running))
                .findFirst();
        if (optional.isPresent()) {
            // if yes, check if is there enough minerals to complete the action.
            BuildOrder buildOrder = optional.get();
            buildOrder.setStatus(OrderStatus.Running);
            if (RBWListener.currentMinerals >= buildOrder.quantity) {
                buildOrder.setStatus(OrderStatus.Completed);
            } else {
                // listeners.forEach(l -> l.update(buildOrders));
                return;
            }
        }

        /**
         * is there any queued gathering gas action?
         */
        optional = buildOrders.stream()
                .filter(ba -> ba.action == BuildActionName.gather_Gas
                        && (ba.getStatus() == OrderStatus.Queued || ba.getStatus() == OrderStatus.Running))
                .findFirst();
        if (optional.isPresent()) {
            // if yes, check if is there enough gas to complete the action.
            BuildOrder buildOrder = optional.get();
            buildOrder.setStatus(OrderStatus.Running);
            if (RBWListener.currentGas >= buildOrder.quantity) {
                buildOrder.setStatus(OrderStatus.Completed);
            } else {
                // listeners.forEach(l -> l.update(buildOrders));
                return;
            }
        }

        /**
         * only 1 build at the time. this aboid selectind adjacents areas to 2 o more builds
         */
        optional = buildOrders.stream()
                .filter(bo -> (bo.action == StarCraftConstants.BuildActionName.build)
                        && bo.getStatus() == OrderStatus.Running)
                .findFirst();
        if (optional.isPresent())
            return;

        /**
         * start the next queued build or train action
         */
        optional = buildOrders.stream()
                .filter(bo -> (bo.action == StarCraftConstants.BuildActionName.build
                        || bo.action == StarCraftConstants.BuildActionName.train)
                        && bo.getStatus() == OrderStatus.Queued)
                .findFirst();
        if (optional.isPresent()) {
            BuildOrder buildOrder = optional.get();
            // train
            if (buildOrder.action == StarCraftConstants.BuildActionName.train) {
                Command command = CommandQueue.train(buildOrder.unitType);
                buildOrder.message = command.message;
                buildOrder.setStatus(OrderStatus.Running);
            }

            // build
            if (buildOrder.action == StarCraftConstants.BuildActionName.build) {
                Command command = CommandQueue.build(buildOrder.unitType);
                buildOrder.message = command.message;
                buildOrder.setStatus(OrderStatus.Running);
            }
        }

        // notify all listeners about the updated build orders
        listeners.forEach(l -> l.update(buildOrders));
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
    public static String addBuildOrder(UnitType unitType, int quantity) {
        // fail save
        Optional<BuildOrder> optional = buildOrders.stream()
                .filter(bo -> bo.unitType == unitType && bo.quantity == quantity
                        && (bo.getStatus() == OrderStatus.Queued || bo.getStatus() == OrderStatus.Running))
                .findFirst();
        if (optional.isPresent()) {
            throw new StarCraftException("An order for " + quantity + " of " + unitType + " is already in.");
        }

        String voucher = "V-" + ++voucherIdx;
        executorService.submit(() -> solve(voucher, unitType, quantity));
        // futureOrder = executorService.submit(() -> solve(voucher, unitType,
        // quantity));

        List<BuildOrder> buildOrders2 = new ArrayList<>();
        orders.put(voucher, buildOrders2);
        return voucher;
    }

    public static List<BuildOrder> getBuildOrders(String voucher) {
        return orders.get(voucher);
    }

    private static List<BuildOrder> solve(String voucher, UnitType unitType, int quantity) {
        // configure the PDDL problem for the given unit type and quantity
        Pair<UnitType, Integer> pair = Pair.of(unitType, quantity);
        RPDDLProblem pddlProblem = new RPDDLProblem(pair);
        pddlProblem.printProblem = true;
        problem = pddlProblem.getPDDLProblem();

        // parse and configure the planner
        String[] args1 = { "-o", domain, "-f", problem, "-planner", planner };
        renhsp.parseInput(args1);
        renhsp.configurePlanner();
        if (!renhsp.parsingDomainAndProblem(args1))
            throw new StarCraftException("Error parsing domain and problem files.");

        // solve the planning problem
        LinkedList<ImmutablePair<BigDecimal, TransitionGround>> plan = renhsp.planning();
        List<String> planStrings = plan.stream().map(inpair -> inpair.getRight().getName()).toList();

        if (planStrings == null || planStrings.isEmpty())
            throw new StarCraftException("No plan found for build order: " + unitType + " x" + quantity);

        // convert the plan strings into build orders
        List<BuildOrder> buildOrders2 = new ArrayList<>();
        // TODO: test if the plan is better that way. without optimization may is faster
        buildOrders2 = BuildOrder.getBuildOrders(planStrings);
        // for (String action : planStrings) {
        // BuildOrder buildOrder = new BuildOrder(UnitType.None, -1);
        // if (action.equals("gather-Mineral") || action.equals("gather-Gas")) {
        // buildOrder.action = action.equals("gather-Mineral") ?
        // BuildActionName.gather_Mineral
        // : BuildActionName.gather_Gas;
        // buildOrder.quantity = action.equals("gather-Mineral") ?
        // StarCraftConstants.MINERAL_LOAD
        // : StarCraftConstants.GAS_LOAD;
        // buildOrders2.add(buildOrder);
        // continue;
        // }

        // String[] action_UnitName = action.split("-");
        // buildOrder = new BuildOrder(UnitType.valueOf(action_UnitName[1]), 1);
        // buildOrder.action = BuildActionName.valueOf(action_UnitName[0]);
        // buildOrders2.add(buildOrder);
        // }

        List<BuildOrder> buildOrders3 = orders.get(voucher);
        buildOrders3.addAll(buildOrders2);
        buildOrders.addAll(buildOrders2);

        for (LogisticCenterListener listener : listeners) {
            listener.update(buildOrders);
        }
        return buildOrders2;
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
    // RPDDLProblem pddlProblem = new RPDDLProblem(Pair.of(UnitType.Terran_Bunker,
    // 1));
    // pddlProblem.isTest = true;
    // pddlProblem.unitsTest.add(Pair.of(UnitType.Terran_Command_Center, 1));
    // pddlProblem.unitsTest.add(Pair.of(UnitType.Terran_SCV, 1));
    // LogisticCenter.problem = pddlProblem.getPDDLProblem();
    // List<String> plan = LogisticCenter.solve();
    // plan.forEach(System.out::println);
    // }
}
