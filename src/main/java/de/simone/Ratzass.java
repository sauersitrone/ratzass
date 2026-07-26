package de.simone;

import java.time.LocalDateTime;

import bwapi.BWClient;
import bwapi.DefaultBWListener;
import bwapi.Game;
import bwapi.Player;
import bwapi.Unit;
import bwem.BWEM;
import tech.tablesaw.api.Table;

public class Ratzass extends DefaultBWListener {

    public static Ratzass instance;
    public static BWClient bwClient;
    public static Game game;
    public static LocalDateTime startTime;
    public static BWEM bwem;

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
        bwem = new BWEM(game);
        bwem.initialize();
        bwem.getMap().assignStartingLocationsToSuitableBases();
    }

    @Override
    public void onFrame() {
        Player player = game.self();

        _secondsNow = game.getFrameCount() / 23.81;
        _framesNow = game.getFrameCount();

        // register units statistics
        for (Unit unit : game.getAllUnits()) {
            UnitEvent unitEvent = new UnitEvent(unit);

            if (unit.getPlayer().equals(player)) {
                // do something with my units
            }
        }
    }

    @Override
    public void onUnitComplete(Unit unit) {
        UnitEvent unitEvent = new UnitEvent(unit);
        unitEvent.status = UnitEvent.EventType.CREATED;
        unitEventsTable.addColumns(unitEvent.toColumns());
    }

    @Override
    public void onUnitDestroy(Unit unit) {
        UnitEvent unitEvent = new UnitEvent(unit);
        unitEvent.status = UnitEvent.EventType.DESTROYED;
        unitEventsTable.addColumns(unitEvent.toColumns());
    }

    @Override
    public void onUnitDiscover(Unit unit) {
        if (game.self().isEnemy(unit.getPlayer())) {
            UnitEvent unitEvent = new UnitEvent(unit);
            unitEvent.isEnemy = true;
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

}