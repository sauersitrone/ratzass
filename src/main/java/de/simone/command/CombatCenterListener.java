package de.simone.command;

import java.util.List;

public interface CombatCenterListener {

    public void updated(List<CombatOrder> units);
}
