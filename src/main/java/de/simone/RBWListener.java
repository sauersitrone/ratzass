package de.simone;

import java.time.LocalDateTime;

import bwapi.BWClient;
import bwapi.DefaultBWListener;
import bwapi.Game;
import bwapi.Player;
import bwapi.Unit;
import bwem.BWEM;
import de.simone.command.CommandQueue;
import de.simone.command.LogisticCenter;
import de.simone.command.UnitsCenter;

public class RBWListener extends DefaultBWListener {

    public static RBWListener instance;
    public static BWClient bwClient;
    public static Game game;
    public static LocalDateTime startTime;
    public static BWEM bwem;
    public static UnitsCenter unitsCenter;
    public static LogisticCenter logisticCenter;
    public static int currentMinerals = 0;
    public static int currentGas = 0;
    public static int currentSupplyTotal = 0;
    public static int currentSupplyUsed = 0;
    public static double gameSeconds;
    public static double lastBehaviorTreeStep;
    public static double lastCenterComm;

    // Store the current units status

    private long lastFrameTime = 0;

    private RBWListener() {
        logisticCenter = LogisticCenter.getInstance();
        unitsCenter = UnitsCenter.getInstance();

        bwClient = new BWClient(this);
        bwClient.startGame();
    }

    public static RBWListener getInstance() {
        if (instance == null) {
            instance = new RBWListener();
        }
        return instance;
    }

    public static void init() {
        getInstance();
    }

    @Override
    public void onStart() {
        game = bwClient.getGame();
        startTime = LocalDateTime.now();
        game.setRevealAll(!Env.fogOfWar);
        bwem = new BWEM(game);
        bwem.initialize();
        bwem.getMap().assignStartingLocationsToSuitableBases();

    }

    @Override
    public void onFrame() {
        Player self = game.self();
        game.setLocalSpeed(Env.speed);
        // System.out.println("RBWListener.onFrame() " + game.getFrameCount() + " - " + Env.speed);
        // game.drawTextScreen(10, 15, "Playing as " + self.getName() + " - " + self.getRace());

        gameSeconds = game.getFrameCount() / 23.81;

        // behavior tree step (every 0.100 milliseconds)
        if (gameSeconds - lastBehaviorTreeStep >= 1) {
            lastBehaviorTreeStep = gameSeconds;
            // RUtils.step(CombatCenter.getInstance().behaviorTree);
        }

        // command dispatch & logistic
        if (gameSeconds - lastCenterComm >= 1) {
            lastCenterComm = gameSeconds;
            RUtils.step(LogisticCenter.getInstance().behaviorTree);
            LogisticCenter.getInstance().update();
            CommandQueue.getInstance().dispatchCommands();
            UnitsCenter.getInstance().update();
        }

        // update gathered resources
        if (System.currentTimeMillis() - lastFrameTime >= 5 * 1000) {
            lastFrameTime = System.currentTimeMillis();
            currentGas = self.gas();
            currentMinerals = self.minerals();
            currentSupplyTotal = self.supplyTotal();
            currentSupplyUsed = self.supplyUsed();
        }
    }

    /**
     * Called only when a building construction or unit training cycle completely
     * finishes.
     */
    @Override
    public void onUnitComplete(Unit unit) {
        logisticCenter.onUnitComplete(unit);
        unitsCenter.onUnitComplete(unit);
    }

    /**
     * Executed when a unit's health hits zero and it dies
     */
    @Override
    public void onUnitDestroy(Unit unit) {
        unitsCenter.onUnitDestroy(unit);
    }

    /**
     * Fired the exact frame a unit becomes visible inside your fog of war.
     */
    @Override
    public void onUnitDiscover(Unit unit) {
        unitsCenter.onUnitDiscover(unit);
    }

}