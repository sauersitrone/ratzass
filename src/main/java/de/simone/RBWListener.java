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
import tech.tablesaw.api.Table;

public class RBWListener extends DefaultBWListener {

    public static RBWListener instance;
    public static BWClient bwClient;
    public static Game game;
    public static LocalDateTime startTime;
    public static BWEM bwem;
    public static CommandQueue commandQueue;
    public static UnitsCenter unitsCenter;
    public static LogisticCenter logisticCenter;
    public static Ratzass ratzass;
    public static int gatheredMinerals = 0;
    public static int gatheredGas = 0;

    // Store the current units status
    public static Table unitEventsTable;

    // second computed from frames
    public static double _secondsNow;

    // Has game been started
    private boolean isRunning = true;

    private long lastFrameTime = 0;

    @Override
    public void onStart() {
        game = bwClient.getGame();
        startTime = LocalDateTime.now();
        game.setRevealAll(!Env.fogOfWar);
        game.setLocalSpeed(Env.speed);
        bwem = new BWEM(game);
        bwem.initialize();
        bwem.getMap().assignStartingLocationsToSuitableBases();
    }

    @Override
    public void onFrame() {
        Player player = game.self();

        _secondsNow = game.getFrameCount() / 23.81;

        // update gathered resources
        if (System.currentTimeMillis() - lastFrameTime >= 5 * 1000) {
            lastFrameTime = System.currentTimeMillis();
            gatheredGas = player.gatheredGas();
            gatheredMinerals = player.gatheredMinerals();
            LogisticCenter.getInstance().update();
        }
    }

    @Override
    public void onUnitComplete(Unit unit) {
        logisticCenter.onUnitComplete(unit);
        unitsCenter.onUnitComplete(unit);
        UnitEvent unitEvent = new UnitEvent(unit);
        unitEvent.status = UnitEvent.EventType.CREATED;
        unitEventsTable.addColumns(unitEvent.toColumns());
    }

    @Override
    public void onUnitDestroy(Unit unit) {
        unitsCenter.onUnitDestroy(unit);
        UnitEvent unitEvent = new UnitEvent(unit);
        unitEvent.status = UnitEvent.EventType.DESTROYED;
        unitEventsTable.addColumns(unitEvent.toColumns());
    }

    @Override
    public void onUnitDiscover(Unit unit) {
        unitsCenter.onUnitDiscover(unit);
        UnitEvent unitEvent = new UnitEvent(unit);
        unitEvent.isEnemy = game.self().isEnemy(unit.getPlayer());
        unitEventsTable.addColumns(unitEvent.toColumns());
    }

    void run() {
        instance = this;
        unitEventsTable = Table.create("Unit Events");
        commandQueue = CommandQueue.getInstance();
        unitsCenter = UnitsCenter.getInstance();
        logisticCenter = LogisticCenter.getInstance();
        ratzass = Ratzass.getInstance();
        ratzass.start();

        if (!isRunning) {
            isRunning = true;

            bwClient = new BWClient(this);
            bwClient.startGame();
        }
    }

}