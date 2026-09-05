package de.simone;

import bwapi.Unit;
import bwapi.UnitCommand;
import bwapi.UnitCommandType;
import bwapi.UnitType;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

public class UnitEvent {

    public static enum EventType {
        UNKNOW,
        CREATED,
        DESTROYED
    }

    public int id;
    public UnitType type;
    public UnitCommandType lastCommand;
    public int hitPoints;
    public int killCount;
    public EventType status = EventType.UNKNOW;
    public boolean isEnemy = false;
    public int gasResources = 0;
    public int mineralResources = 0;
    public int totalResources = 0;

    public UnitEvent(Unit unit) {
        this.id = unit.getID();
        this.type = unit.getType();
        this.lastCommand = unit.getLastCommand() == null ? UnitCommandType.Unknown : unit.getLastCommand().getType();
        this.hitPoints = unit.getHitPoints();
        this.killCount = unit.getKillCount();
        this.gasResources = unit.getType().gasPrice();
        this.mineralResources = unit.getType().mineralPrice();
        this.totalResources = this.gasResources + this.mineralResources;
        this.isEnemy = RBWListener.game.self().isEnemy(unit.getPlayer());
    }

    public static void createColumns(Table table) {
        table.addColumns(
                IntColumn.create("id"),
                StringColumn.create("type"),
                StringColumn.create("lastCommand"),
                IntColumn.create("hitPoints"),
                IntColumn.create("killCount"),
                StringColumn.create("status"),
                BooleanColumn.create("isEnemy"),
                IntColumn.create("gasResources"),
                IntColumn.create("mineralResources"),
                IntColumn.create("totalResources"));
    }

    public void update(Table table) {
        Row row = null;

        // is an id already in the table?
        for (int i = 0; i < table.rowCount(); i++) {
            Row row2 = table.row(i);
            if (row2.getInt("id") == id)
                row = row2;
        }

        // then is a new row
        if (row == null)
            row = table.appendRow();

        // update the values
        row.setInt("id", id);
        row.setString("type", type.toString());
        row.setString("lastCommand", lastCommand.toString());
        row.setInt("hitPoints", hitPoints);
        row.setInt("killCount", killCount);
        row.setString("status", status.toString());
        row.setBoolean("isEnemy", isEnemy);
        row.setInt("gasResources", gasResources);
        row.setInt("mineralResources", mineralResources);
        row.setInt("totalResources", totalResources);
    }
}
