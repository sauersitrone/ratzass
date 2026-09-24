package de.simone;

import java.time.LocalDateTime;
import java.util.List;

import bwapi.BWClient;
import bwapi.Color;
import bwapi.DefaultBWListener;
import bwapi.Flag;
import bwapi.Game;
import bwapi.Player;
import bwapi.Position;
import bwapi.Unit;
import bwem.BWEM;
import de.simone.command.CommandQueue;
import de.simone.command.LogisticCenter;
import de.simone.command.Squad;
import de.simone.command.UnitsCenter;

public class RBWListener extends DefaultBWListener {

    public static BWClient bwClient;
    public static Game game;
    public static LocalDateTime startTime;
    public static BWEM bwem;
    public static LogisticCenter logisticCenter;
    public static int currentMinerals = 0;
    public static int currentGas = 0;
    private static int currentSupplyTotal = 0;
    private static int currentSupplyUsed = 0;
    public static int currentSupplyLeft = 0;
    public static double gameSeconds;
    public static double lastBehaviorTreeStep;
    public static double lastCenterComm;
    public static int codeSpeed;
    public static boolean isPaused = false;

    private RBWListener() {
        bwClient = new BWClient(this);
        bwClient.startGame();
    }

    public static void init() {
        new RBWListener();
    }

    @Override
    public void onStart() {
        game = bwClient.getGame();
        startTime = LocalDateTime.now();
        game.setRevealAll(!Config.fogOfWar);
        if (Config.userInput)
            game.enableFlag(Flag.UserInput);

        bwem = new BWEM(game);
        bwem.initialize();
        bwem.getMap().assignStartingLocationsToSuitableBases();
    }

    @Override
    public void onFrame() {
        Player self = game.self();
        game.setLocalSpeed(Config.speed);
        gameSeconds = game.getFrameCount() / 23.81;
        if (isPaused)
            game.pauseGame();
        else
            game.resumeGame();

        // update current resources
        currentGas = self.gas();
        currentMinerals = self.minerals();
        currentSupplyTotal = self.supplyTotal();
        currentSupplyUsed = self.supplyUsed();
        currentSupplyLeft = currentSupplyTotal - currentSupplyUsed;

        // test
        if (CommandQueue.currentCommand != null) {
            Position position = CommandQueue.currentCommand.position;
            // Position position = CommandQueue.currentCommand.tilePosition.toPosition();
            int left = position.x - CommandQueue.currentCommand.unitType.dimensionLeft();
            int right = position.x + CommandQueue.currentCommand.unitType.dimensionRight();
            int up = position.y - CommandQueue.currentCommand.unitType.dimensionUp();
            int down = position.y + CommandQueue.currentCommand.unitType.dimensionDown();
            Color color = RBWListener.game.self().getColor();
            RBWListener.game.drawBoxMap(left, up, right, down, color);
        }

        // RBWListener.game.drawText(CoordinateType.Screen, 8, 16, "FPS: " +
        // game.getFPS(), Text.Blue);

        /**
         * heartbeat for units center, logistic center, and command queue.
         * 
         * NOTE: much of * the listener take into account, that this coed will be
         * executed every 100ms
         */
        if (gameSeconds - lastCenterComm >= 0.1) {
            lastCenterComm = gameSeconds;
            long t1 = System.currentTimeMillis();

            UnitsCenter.controlPersonal();
            LogisticCenter.heartBeat();
            CommandQueue.dispatchCommands();

            LogisticCenter.behaviorTree.step();
            List<Squad> squads = UnitsCenter.getSquads();
            for (Squad squad : squads) {
                squad.updateStatus();
                if (squad.isAlive())
                    squad.behaviorTree.step();
            }

            long t2 = System.currentTimeMillis();
            codeSpeed = (int) (t2 - t1);
        }
    }

    /**
     * Called only when a building construction or unit training cycle completely
     * finishes.
     */
    @Override
    public void onUnitComplete(Unit unit) {
        LogisticCenter.onUnitComplete(unit);
        UnitsCenter.onUnitComplete(unit);
    }

    /**
     * Executed when a unit's health hits zero and it dies
     */
    @Override
    public void onUnitDestroy(Unit unit) {
        UnitsCenter.onUnitDestroy(unit);
    }

    /**
     * Fired the exact frame a unit becomes visible inside your fog of war.
     */
    @Override
    public void onUnitDiscover(Unit unit) {
        UnitsCenter.onUnitDiscover(unit);
    }

}