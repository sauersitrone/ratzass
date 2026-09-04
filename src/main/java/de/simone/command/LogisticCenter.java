package de.simone.command;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.hstairs.ppmajal.transition.TransitionGround;

import bwapi.Order;
import bwapi.Pair;
import bwapi.Unit;
import bwapi.UnitType;
import de.simone.RBWListener;
import de.simone.RUtils;
import de.simone.command.CommandQueue.ResourceType;
import de.simone.command.StarCraftConstants.BuildActionName;
import de.simone.command.StarCraftConstants.OrderPriority;
import de.simone.command.StarCraftConstants.OrderStatus;

/**
 * control the creation of units and buildings, and the gathering of resources.
 * It uses a PDDL planner to determine the best course of action to achieve the
 * desired state of the game.
 * 
 */
public class LogisticCenter {

    private static LogisticCenter instance;
    private String domain;
    private String problem;
    private String planner;
    private List<BuildOrder> buildOrders = new ArrayList<>();
    private List<LogisticCenterListener> listeners = new ArrayList<>();
    public BehaviorTree<LogisticCenter> behaviorTree;

    public static LogisticCenter getInstance() {
        if (instance == null) {
            instance = new LogisticCenter();
            return instance;
        }
        return instance;
    }

    public static void init() {
        getInstance();
    }

    private LogisticCenter() {
        this.domain = RUtils.getResourceFile("./starcraft-domain.pddl");
        this.planner = "opt-blind";
        this.behaviorTree = RUtils.parseFile("logistic.tree", this);

        // this.planner = "sat-hmrp";
    }

    public boolean areMyOrdersReady(UnitType unitType, int quantity) {
        List<BuildOrder> list = buildOrders.stream().filter(o -> o.unitType == unitType && o.quantity == quantity)
                .toList();
        int ready = (int) list.stream().filter(o -> o.status == OrderStatus.Completed).count();
        return ready == list.size();
    }

    public void onUnitComplete(Unit unit) {
        Optional<BuildOrder> optional = buildOrders.stream()
                .filter(ba -> ba.status == OrderStatus.Running && ba.unitType == unit.getType())
                .findFirst();

        if (optional.isPresent()) {
            optional.get().status = OrderStatus.Completed;
        }
    }

    /**
     * call by RBWListener every x seconds. this method will:
     * - check if is there at leas a refinery. if not, create it
     * - update the status of the pending gathering actions.
     * - check the pending build and train actions. if the resources are enough,
     * dispatch the command to the CommandQueue
     * - check the supply min threshold. if the supply is low, create a build order
     * for supplyDepot
     * - check the min number of SCV. if the number of SCV is low, create a build
     * order for SCV
     * - check if there is any idle SCV. if there is, set it to gather minerals or
     * gas, depending on the current number of SCV gathering each resource.
     * - notify the listeners about the updated buildOrders
     */
    public void update() {
        List<Unit> units = UnitsCenter.getUnits();

        // ensure the scv are working
        Unit unit = UnitsCenter.getIdleTerranSCV();
        Unit refinery = UnitsCenter.getUnits().stream().filter(u -> u.getType() == UnitType.Terran_Refinery).findFirst()
                .orElse(null);
        if (unit != null) {
            int gGas = (int) UnitsCenter.getUnits().stream()
                    .filter(u -> u.getType() == UnitType.Terran_SCV && u.isGatheringGas()).count();
            if (gGas < StarCraftConstants.SCV_GATHERING_GAS && refinery != null) {
                CommandQueue.getInstance().gather(ResourceType.Gas);
            }

            int gMinerals = (int) (int) UnitsCenter.getUnits().stream()
                    .filter(u -> u.getType() == UnitType.Terran_SCV && u.isGatheringMinerals()).count();
            if (gMinerals < StarCraftConstants.SCV_GATHERING_MINERALS) {
                CommandQueue.getInstance().gather(ResourceType.Mineral);
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

        // resources check.

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
                Command command = CommandQueue.getInstance().train(buildOrder.unitType);
                buildOrder.message = command.message;
                buildOrder.status = OrderStatus.Running;
            }

            // build
            if (buildOrder.action == StarCraftConstants.BuildActionName.build) {
                Command command = CommandQueue.getInstance().build(buildOrder.unitType);
                buildOrder.message = command.message;
                buildOrder.status = OrderStatus.Running;
            }
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
     */
    public void addBuildOrder(BuildOrder buildOrder) {
        // to avoid creating the same build order, check if the remitent already has a
        // pending build order.
        Optional<BuildOrder> optional = buildOrders.stream()
                .filter(bo -> bo.unitType == buildOrder.unitType
                        && (bo.status == OrderStatus.Pending || bo.status == OrderStatus.Running))
                .findFirst();
        if (optional.isPresent()) {
            return;
        }

        // the goal muss express the total units (e.g if i want to build 1 SCV, and i
        // already have 1, the goal must be 2, not 1)
        Pair<UnitType, Integer> pair = new Pair<>(buildOrder.unitType, buildOrder.quantity);
        PddlProblem pddlProblem = new PddlProblem(pair);
        pddlProblem.printProblem = true;
        this.problem = pddlProblem.getPDDLProblem();

        List<String> plan = solve();
        List<BuildOrder> buildOrders2 = new ArrayList<>();
        if (plan == null || plan.isEmpty()) {
            buildOrder.status = OrderStatus.Error;
            buildOrder.message = "No plan found for build order: " + buildOrder.unitType + " x" + buildOrder.quantity;
        } else {
            buildOrder.status = OrderStatus.Pending;
            buildOrders2 = BuildOrder.getBuildOrders(plan);
        }

        // build order priority
        if (buildOrder.priority == OrderPriority.High) {
            buildOrders.addAll(0, buildOrders2);
        } else {
            buildOrders.addAll(buildOrders2);
        }

        for (LogisticCenterListener listener : listeners) {
            listener.updated(buildOrders);
        }
    }

    private List<String> solve() {
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

    public void addListener(LogisticCenterListener listener) {
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
