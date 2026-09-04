package de.simone.btree;

import java.util.List;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import de.simone.command.CombatCenter;
import de.simone.command.CombatOrder;
import de.simone.command.CombatOrder.PositionName;

public class RetrivePositionTask extends RTask {

    @TaskAttribute(required = true)
    public PositionName position;

    @Override
    public Status execute() {
        CombatCenter combatCenter = CombatCenter.getInstance();
        List<CombatOrder> combatOrders = combatCenter.getOrders(position);

        return combatOrders.isEmpty() ? Status.FAILED : Status.SUCCEEDED;
    }
}
