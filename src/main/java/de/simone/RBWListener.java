package de.simone;

import java.time.LocalDateTime;
import java.util.List;

import com.badlogic.gdx.ai.GdxAI;

import bwapi.BWClient;
import bwapi.Color;
import bwapi.DefaultBWListener;
import bwapi.Flag;
import bwapi.Game;
import bwapi.Player;
import bwapi.Race;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
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
    public static int currentSupplyTotal = 0;
    public static int currentSupplyUsed = 0;
    public static int currentSupplyLeft = 0;
    public static double gameSeconds;
    public static double lastBehaviorTreeStep;
    public static double lastTick;
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
        if (Config.fogOfWar)
            game.enableFlag(Flag.CompleteMapInformation);

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
        currentSupplyTotal = (int) self.supplyTotal(Race.Terran) / 2;
        currentSupplyUsed = (int) self.supplyUsed(Race.Terran) / 2;
        currentSupplyLeft = currentSupplyTotal - currentSupplyUsed;

        // test autocamera parameter
        // if (Config.autoCamera && CommandQueue.currentCommand != null &&
        // (CommandQueue.currentCommand.position != null
        // || CommandQueue.currentCommand.tilePosition != null)) {
        // if (CommandQueue.currentCommand.position != null)
        // game.setScreenPosition(CommandQueue.currentCommand.position);
        // if (CommandQueue.currentCommand.tilePosition != null)
        // game.setScreenPosition(CommandQueue.currentCommand.tilePosition.toPosition());
        // }

        if (CommandQueue.currentCommand != null && CommandQueue.currentCommand.tilePosition != null) {
            // The target tilePosition is a build footprint’s top-left tile.
            TilePosition tilePosition = CommandQueue.currentCommand.tilePosition;
            UnitType unitType = CommandQueue.currentCommand.unitType;
            int left = tilePosition.x * TilePosition.SIZE_IN_PIXELS;
            int top = tilePosition.y * TilePosition.SIZE_IN_PIXELS;
            int right = left + unitType.tileWidth() * TilePosition.SIZE_IN_PIXELS;
            int bottom = top + unitType.tileHeight() * TilePosition.SIZE_IN_PIXELS;
            Color color = RBWListener.game.self().getColor();
            RBWListener.game.drawBoxMap(left, top, right, bottom, color);
        }

        // RBWListener.game.drawText(CoordinateType.Screen, 8, 16, "FPS: " +
        // game.getFPS(), Text.Blue);

        /**
         * heartbeat for units center, logistic center, and command queue.
         * 
         * NOTE: much of * the listener take into account, that this coed will be
         * executed every 100ms
         */
        if (gameSeconds - lastTick >= 0.1) {
            lastTick = gameSeconds;
            long t1 = System.currentTimeMillis();
            GdxAI.getTimepiece().update((float) gameSeconds);

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