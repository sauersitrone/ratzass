package de.simone.command;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.hstairs.ppmajal.transition.TransitionGround;

import bwapi.Order;
import bwapi.Pair;
import bwapi.Unit;
import bwapi.UnitType;
import de.simone.RBWListener;
import de.simone.RUtils;
import de.simone.command.BuildOrder.BuildActionName;
import de.simone.command.CommandQueue.ResourceType;
import de.simone.command.StarCraftConstants.OrderPriority;
import de.simone.command.StarCraftConstants.OrderStatus;

/**
 * control the creation of units and buildings, and the gathering of resources.
 * It uses a PDDL planner to determine the best course of action to achieve the
 * desired state of the game.
 * 
 */
public class LogisticCenter {

    private static final int terranMinSupply = 5;
    private static final int terranMinSCV = 6;
    private static final int scvGatheringMinerals = 4;
    private static final int scvGatheringGas = 2;

    private static LogisticCenter instance;
    private String domain;
    private String problem;
    private String planner;
    private List<BuildOrder> buildOrders = new ArrayList<>();
    private List<BuildAction> buildActions = new ArrayList<>();
    private List<LogisticCenterListener> listeners = new ArrayList<>();

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
        // this.planner = "sat-hmrp";
    }

    private void updateBuildOrdersStatus() {
        for (BuildOrder buildOrder : buildOrders) {
            OrderStatus status = OrderStatus.Pending;

            // if any action end in error, the build order is in error
            Optional<BuildAction> optional = buildOrder.getBuildActions().stream()
                    .filter(n -> n.status == OrderStatus.Error).findFirst();
            status = optional.isPresent() ? OrderStatus.Error : status;

            // if all actions are completed, the build order is completed
            int count = (int) buildOrder.getBuildActions().stream()
                    .filter(n -> n.status == OrderStatus.Completed).count();
            status = count == buildOrder.getBuildActions().size() ? OrderStatus.Completed : status;

            buildOrder.status = status;
        }
    }

    public void onUnitComplete(Unit unit) {
        Optional<BuildAction> optional = buildActions.stream()
                .filter(ba -> ba.status == OrderStatus.Pending && ba.unitType == unit.getType())
                .findFirst();

        if (optional.isPresent()) {
            optional.get().status = OrderStatus.Completed;
            updateBuildOrdersStatus();
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
        List<Unit> units = new ArrayList<>(RBWListener.game.getAllUnits());

        // ---------------------
        List<Unit> idleSCVs2 = units.stream().filter(u -> u.getType() == UnitType.Terran_SCV && u.isIdle()).toList();
        if (!idleSCVs2.isEmpty()) {
            int gMinerals = (int) idleSCVs2.stream().filter(u -> u.getOrder() == Order.MiningMinerals).count();
            if (gMinerals < scvGatheringMinerals) {
                CommandQueue.getInstance().gather(ResourceType.Mineral);
            }
        }
        // ---------------------

        // first day order: build a refinery if not already built
        List<Unit> refineries = units.stream().filter(u -> u.getType() == UnitType.Terran_Refinery).toList();
        if (refineries.isEmpty()) {
            BuildOrder buildOrder = new BuildOrder(getClass().getSimpleName(), UnitType.Terran_Refinery, 1);
            buildOrder.priority = OrderPriority.High;
            addBuildOrder(buildOrder);
        }

        // if is there enough minerals, change the status of the pending gathering
        // actions
        Optional<BuildAction> optional = buildActions.stream()
                .filter(ba -> ba.action == BuildActionName.gather_Mineral && ba.status == OrderStatus.Pending
                        && RBWListener.currentMinerals >= ba.quantity)
                .findFirst();
        if (optional.isPresent()) {
            optional.get().status = OrderStatus.Completed;
        }

        // if is there enough gas, change the status of the pending gathering actions
        optional = buildActions.stream()
                .filter(ba -> ba.action == BuildActionName.gather_Gas && ba.status == OrderStatus.Pending
                        && RBWListener.currentGas >= ba.quantity)
                .findFirst();
        if (optional.isPresent()) {
            optional.get().status = OrderStatus.Completed;
        }

        // check if the next buildAction can be started
        optional = buildActions.stream()
                .filter(ba -> (ba.action == BuildActionName.build || ba.action == BuildActionName.train)
                        && ba.status == OrderStatus.Pending)
                .findFirst();
        if (optional.isPresent()) {
            BuildAction buildAction = optional.get();
            // train
            if (buildAction.action == BuildActionName.train) {
                Command trainCommand = CommandQueue.getInstance().train(buildAction.unitType);
                buildAction.message = trainCommand.message;
            }

            // build
            if (buildAction.action == BuildActionName.build) {
                Command command = CommandQueue.getInstance().build(buildAction.unitType);
                buildAction.message = command.message;
            }
        }

        // check if the min number of scv
        List<Unit> scvs = units.stream().filter(u -> u.getType() == UnitType.Terran_SCV).toList();
        if (scvs.size() < terranMinSCV) {
            int tot = terranMinSCV - scvs.size();
            BuildOrder buildOrder = new BuildOrder(getClass().getSimpleName(), UnitType.Terran_SCV, tot);
            buildOrder.priority = OrderPriority.High;
            addBuildOrder(buildOrder);
        }

        // check no idle SCV. if there is an idle SCV, set for gathering minerals or gas
        List<Unit> idleSCVs = units.stream().filter(u -> u.getType() == UnitType.Terran_SCV && u.isIdle()).toList();
        if (!idleSCVs.isEmpty()) {
            int gGas = (int) idleSCVs.stream().filter(u -> u.getOrder() == Order.HarvestGas).count();
            if (gGas < scvGatheringGas) {
                // CommandQueue.getInstance().gather(ResourceType.Gas);
            }

            int gMinerals = (int) idleSCVs.stream().filter(u -> u.getOrder() == Order.MiningMinerals).count();
            if (gMinerals < scvGatheringMinerals) {
                CommandQueue.getInstance().gather(ResourceType.Mineral);
            }
        }

        // check the supply. if the supply is low, create a build order for supply
        if (RBWListener.currentSupplyTotal - RBWListener.currentSupplyUsed < terranMinSupply) {
            BuildOrder buildOrder = new BuildOrder(getClass().getSimpleName(), UnitType.Terran_Supply_Depot, 1);
            buildOrder.priority = OrderPriority.High;
            addBuildOrder(buildOrder);
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
                .filter(bo -> bo.remitent.equals(buildOrder.remitent) && bo.unitType == buildOrder.unitType
                        && bo.status == OrderStatus.Pending)
                .findFirst();
        if (optional.isPresent()) {
            return;
        }

        // the goal muss expre the toal units (e.g if i want to build 1 SCV, and i
        // already have 1, the goal must be 2, not 1)
        Pair<UnitType, Integer> pair = new Pair<>(buildOrder.unitType, buildOrder.quantity);
        PddlProblem pddlProblem = new PddlProblem(pair);
        pddlProblem.printProblem = true;
        this.problem = pddlProblem.getPDDLProblem();

        List<String> plan = solve();
        if (plan == null || plan.isEmpty()) {
            buildOrder.status = OrderStatus.Error;
            buildOrder.message = "No plan found for build order: " + buildOrder.unitType + " x" + buildOrder.quantity;
        } else {
            buildOrder.status = OrderStatus.Pending;
            buildOrder.setBuildActions(plan);
        }

        // build order priority
        if (buildOrder.priority == OrderPriority.High) {
            buildOrders.add(0, buildOrder);
            buildActions.addAll(0, buildOrder.getBuildActions());
        } else {
            buildOrders.add(buildOrder);
            buildActions.addAll(buildOrder.getBuildActions());
        }

        for (LogisticCenterListener listener : listeners) {
            listener.updated(buildOrders);
        }
    }

    public BuildOrder getBuildOrder(String remitent) {
        return buildOrders.stream().filter(bo -> bo.remitent.equals(remitent)).findFirst().orElse(null);
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
