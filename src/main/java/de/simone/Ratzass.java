package de.simone;

import java.time.LocalDateTime;

import com.espertech.esper.runtime.client.EPRuntime;

import bwapi.BWClient;
import bwapi.DefaultBWListener;
import bwapi.Game;
import bwapi.Player;
import bwapi.Unit;
import de.simone.game.RGame;
import tech.tablesaw.api.Table;

public class Ratzass extends DefaultBWListener {

    public static Ratzass instance;
    public static BWClient bwClient;
    public static Game game;
    public static EPRuntime ePRuntime;
    public static LocalDateTime startTime;

    // Store the current units status
    public static Table unitEventsTable;

    // second computed from frames
    public static double _secondsNow;

    // Cached current frames count
    public static int _framesNow;

    // Has game been started
    private boolean _isStarted = false;

    // Is game currently paused
    private boolean _isPaused = false;


    @Override
    public void onStart() {
        game = bwClient.getGame();
        startTime = LocalDateTime.now();
        game.setRevealAll(!Env.fogOfWar);
    }

    @Override
    public void onFrame() {
        Player player = game.self();

        _secondsNow = game.getFrameCount() / 23.81;
        _framesNow = game.getFrameCount();

        // register units statistics
        for (Unit unit : game.getAllUnits()) {
            UnitEvent unitEvent = new UnitEvent(unit);
            ePRuntime.getEventService().sendEventBean(unitEvent, unitEvent.getClass().getSimpleName());

            if (unit.getPlayer().equals(player)) {
                // do something with my units
            }
        }
    }

    @Override
    public void onUnitComplete(Unit unit) {
        UnitEvent unitEvent = new UnitEvent(unit);
        unitEvent.status = UnitEvent.EventType.CREATED;
        // ePRuntime.getEventService().sendEventBean(unitEvent,
        // unitEvent.getClass().getSimpleName());
        unitEventsTable.addColumns(unitEvent.toColumns());
    }

    @Override
    public void onUnitDestroy(Unit unit) {
        UnitEvent unitEvent = new UnitEvent(unit);
        unitEvent.status = UnitEvent.EventType.DESTROYED;
        // ePRuntime.getEventService().sendEventBean(unitEvent,
        // unitEvent.getClass().getSimpleName());
        unitEventsTable.addColumns(unitEvent.toColumns());
    }

    @Override
    public void onUnitDiscover(Unit unit) {
        if (game.self().isEnemy(unit.getPlayer())) {
            UnitEvent unitEvent = new UnitEvent(unit);
            unitEvent.isEnemy = true;
            // ePRuntime.getEventService().sendEventBean(unitEvent,
            // unitEvent.getClass().getSimpleName());
            unitEventsTable.addColumns(unitEvent.toColumns());
        }
    }

    void run() {
        instance = this;
        unitEventsTable = Table.create("Unit Events");
        if (!_isStarted) {
            _isPaused = false;
            _isStarted = true;

            bwClient = new BWClient(this);
            bwClient.startGame();
        }
    }

    public void exitGame() {
        if (!Env.isTesting)
            RUtils.printEndOfGameStats();

        killProcesses();
    }

    private void killProcesses() {
        System.out.println("\nKilling StarCraft process... ");
        RUtils.killStarcraftProcess();

        System.out.println("Killing Chaoslauncher process... ");
        RUtils.killChaosLauncherProcess();

        System.out.println("Exit...");
        System.exit(0);
    }

}