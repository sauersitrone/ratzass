package de.simone.command;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.hstairs.ppmajal.PDDLProblem.PDDLObjects;
import com.hstairs.ppmajal.PDDLProblem.PDDLProblem;
import com.hstairs.ppmajal.PDDLProblem.PDDLSearchEngine;
import com.hstairs.ppmajal.conditions.BoolPredicate;
import com.hstairs.ppmajal.conditions.Condition;
import com.hstairs.ppmajal.conditions.PDDLObject;
import com.hstairs.ppmajal.domain.PDDLDomain;
import com.hstairs.ppmajal.pddl.heuristics.advanced.H1;
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
    public static BehaviorTree<Blackboard> behaviorTree;
    public static List<BuildOrder> buildOrders = new ArrayList<>();
    static {
        domain = RUtils.getResourceFile("./starcraft-domain.pddl");
        planner = "opt-blind";
        // planner = "sat-hmrp";
        behaviorTree = RUtils.parseFile("logistic.tree");
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
     * Called by RBWListener every x seconds. This method will:
     * - ensure that the SCVs are gathering resources
     * - check if there are any pending build or train orders
     * - start the next pending order if possible.
     * - check if there is a need to build supply depots and add them to the build
     * order if necessary.
     * - notify all registered listeners about the updated build orders.
     */
    public static void update() {

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

            int gMinerals = (int) (int) UnitsCenter.getUnits().stream()
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

        // build supply if needed. this hast hight priority
        if (RBWListener.currentSupplyTotal - RBWListener.currentSupplyUsed < StarCraftConstants.TERRAN_MIN_SUPPLY) {
            addBuildOrder(UnitType.Terran_Supply_Depot, 1, true);
        }

        for (LogisticCenterListener listener : listeners) {
            listener.updated(buildOrders);
        }
    }

    /**
     * create a plan for the buildOrder using the PDDL planner. The plan will be a
     * list of actions to be executed in order to achieve the desired state of the
     * game. this acction chante the status of the buildOrder to ERROR if no plan is
     * found. see the message attribute of the buildOrder for more information.
     * 
     * @param buildOrder - the order
     * @return a list of build orders to be executed in order to achieve the desired
     *         state of the game.
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

        // the goal muss express the total units (e.g if i want to build 1 SCV, and i
        // already have 1, the goal must be 2, not 1)
        Pair<UnitType, Integer> pair = Pair.of(unitType, quantity);

        // PDDLDomain pddlDomain = new PDDLDomain(domain);
        // PDDLProblem pddlProblem = new PDDLProblem(pddlDomain);

        // pddlProblem.setGoals(BoolPredicate.getPredicate("total-units", pair));

        // PDDLObject pddlObject = new PDDLObject(pddlProblem);

        // PDDLObjects pddlObjects = new PDDLObjects(pddlProblem);

        // pddlProblem.setObjects(BoolPredicate.getPredicate("total-units", pair));

        // PDDLSearchEngine searchEngine = new PDDLSearchEngine(pddlProblem, new H1(pddlProblem)); // manager of the search
        //                                                                                         // strategies
        // LinkedList<Pair<BigDecimal, Object>> plans = searchEngine.WAStar();

        RPddlProblem pddlProblem = new RPddlProblem(pair);
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
        RENHSP p = new RENHSP(false);
        String[] args1 = { "-o", domain, "-f", problem, "-planner", planner };
        p.parseInput(args1);
        p.configurePlanner();
        if (p.parsingDomainAndProblem(args1)) {
            LinkedList<ImmutablePair<BigDecimal, TransitionGround>> plan = p.planning();
            List<String> planStrings = plan.stream().map(inpair -> inpair.getRight().getName()).toList();
            return planStrings;
        } else {
            System.out.println("Error parsing domain and problem files.");
            return null;
        }
    }

    public static void addListener(LogisticCenterListener listener) {
        listeners.add(listener);
    }

    // public static void main(String[] args) {
    // LogisticCenter logistics = new LogisticCenter();

    // PddlProblem pddlProblem = new PddlProblem(new Pair<>(UnitType.Terran_SCV,
    // 2));
    // pddlProblem.isTest = true;
    // pddlProblem.unitsTest.add(new Pair<>(UnitType.Resource_Mineral_Field, 1));
    // pddlProblem.unitsTest.add(new Pair<>(UnitType.Resource_Vespene_Geyser, 1));
    // pddlProblem.unitsTest.add(new Pair<>(UnitType.Terran_Command_Center, 1));
    // pddlProblem.unitsTest.add(new Pair<>(UnitType.Terran_SCV, 1));
    // logistics.problem = pddlProblem.getPDDLProblem();
    // logistics.solve();
    // }
}
