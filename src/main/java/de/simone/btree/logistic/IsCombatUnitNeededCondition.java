package de.simone.btree.logistic;

import java.util.List;

import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import bwapi.UnitType;
import de.simone.btree.Blackboard;
import de.simone.command.DogTag;
import de.simone.command.UnitsCenter;

/**
 * Checks if a combat unit of the specified type is needed based on the current
 * count of unassigned units.
 * 
 * NOTE: all combat units must be assigned to an squad
 */
public class IsCombatUnitNeededCondition extends LogisticTask {

    @TaskAttribute(required = true)
    public UnitType unitType;

    @TaskAttribute(required = true)
    public int count;

    @Override
    public Status execute() {
        List<DogTag> dogTags = UnitsCenter.getDogTags();
        int c = (int) dogTags.stream().filter(u -> u.isAlive && u.unitType == unitType && "".equals(u.squadID)).count();
        return c < count ? Status.SUCCEEDED : Status.FAILED;
    }

    @Override
    public String toString() {
        String name = super.toString();
        return name + " " + unitType.toString() + " count:" + count;
    }

    @Override
    protected Task<Blackboard> copyTo(Task<Blackboard> task) {
        IsCombatUnitNeededCondition copy = (IsCombatUnitNeededCondition) task;
        copy.unitType = unitType;
        copy.count = count;
        return copy;
    }
}
