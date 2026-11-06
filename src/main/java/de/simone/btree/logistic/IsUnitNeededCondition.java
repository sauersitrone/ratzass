package de.simone.btree.logistic;

import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import bwapi.UnitType;
import de.simone.btree.Blackboard;
import de.simone.command.UnitsCenter;

public class IsUnitNeededCondition extends LogisticTask {
    @TaskAttribute(required = true)
    public UnitType unitType;

    @TaskAttribute(required = true)
    public int count;

    @Override
    public Status execute() {
        int c = UnitsCenter.getUnitCount(unitType);
        return c < count ? Status.SUCCEEDED : Status.FAILED;
    }

    @Override
    public String toString() {
        String name = super.toString();
        return name + " " + unitType.toString() + " count:" + count;
    }

    @Override
    protected Task<Blackboard> copyTo(Task<Blackboard> task) {
        IsUnitNeededCondition copy = (IsUnitNeededCondition) task;
        copy.unitType = unitType;
        copy.count = count;
        return copy;
    }
}
