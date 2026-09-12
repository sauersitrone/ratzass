package de.simone;

import java.time.LocalDateTime;
import java.util.List;

import bwapi.BWClient;
import bwapi.Color;
import bwapi.CoordinateType;
import bwapi.DefaultBWListener;
import bwapi.Game;
import bwapi.Player;
import bwapi.Position;
import bwapi.Text;
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
    public static int currentSupplyTotal = 0;
    public static int currentSupplyUsed = 0;
    public static double gameSeconds;
    public static double lastBehaviorTreeStep;
    public static double lastCenterComm;
    public static int codeSpeed;

    // Store the current units status

    private long lastFrameTime = 0;

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
        game.setRevealAll(!Env.fogOfWar);
        bwem = new BWEM(game);
        bwem.initialize();
        bwem.getMap().assignStartingLocationsToSuitableBases();
    }

    @Override
    public void onFrame() {
        Player self = game.self();
        game.setLocalSpeed(Env.speed);
        gameSeconds = game.getFrameCount() / 23.81;

        // update current resources
        currentGas = self.gas();
        currentMinerals = self.minerals();
        currentSupplyTotal = self.supplyTotal();
        currentSupplyUsed = self.supplyUsed();

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

        // RBWListener.game.drawText(CoordinateType.Screen, 8, 16, "FPS: " + game.getFPS(), Text.Blue);

        // command dispatch & logistic
        if (gameSeconds - lastCenterComm >= 0.1) {
            lastCenterComm = gameSeconds;
            long t1 = System.currentTimeMillis();

            UnitsCenter.update();
            LogisticCenter.update();
            CommandQueue.dispatchCommands();

            RUtils.step(LogisticCenter.behaviorTree);
            List<Squad> squads = UnitsCenter.getSquads();
            for (Squad squad : squads) {
                // dispach only for Squad with alive units
                if (squad.isAlive())
                    RUtils.step(squad.behaviorTree);
            }

            long t2 = System.currentTimeMillis();
            codeSpeed = (int) (t2 - t1);
        }

        // update gathered resources
        if (System.currentTimeMillis() - lastFrameTime >= 5 * 1000) {
            lastFrameTime = System.currentTimeMillis();

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