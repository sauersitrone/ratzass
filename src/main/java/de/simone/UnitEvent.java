package de.simone;

import java.util.ArrayList;
import java.util.List;

import bwapi.Unit;
import bwapi.UnitType;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.columns.Column;

public class UnitEvent {

    public static enum EventType {
        CREATED,
        OPERATIVE,
        HEALED,
        DAMAGED,
        DESTROYED
    }

    public int id;
    public UnitType type;
    public String name;
    public int hitPoints;
    public int killCount;
    public EventType status = EventType.OPERATIVE;
    public boolean isEnemy = false;
    public int gasResources = 0;
    public int mineralResources = 0;
    public int totalResources = 0;

    public UnitEvent(Unit unit) {
        this.id = unit.getID();
        this.type = unit.getType();
        this.name = unit.getType().toString();
        this.hitPoints = unit.getHitPoints();
        this.killCount = unit.getKillCount();
        this.gasResources = unit.getType().gasPrice();
        this.mineralResources = unit.getType().mineralPrice();
        this.totalResources = this.gasResources + this.mineralResources;
    }

    public Column<?>[] toColumns() {
        List<Column<?>> columns = new ArrayList<>();
        columns.add(IntColumn.create("id", id));
        columns.add(StringColumn.create("type", type.toString()));
        columns.add(StringColumn.create("name", name));
        columns.add(IntColumn.create("hitPoints", hitPoints));
        columns.add(IntColumn.create("killCount", killCount));
        columns.add(StringColumn.create("status", status.toString()));
        columns.add(BooleanColumn.create("isEnemy", isEnemy));
        columns.add(IntColumn.create("gasResources", gasResources));
        columns.add(IntColumn.create("mineralResources", mineralResources));
        columns.add(IntColumn.create("totalResources", totalResources));
        return columns.toArray(new Column<?>[0]);
    }
}
