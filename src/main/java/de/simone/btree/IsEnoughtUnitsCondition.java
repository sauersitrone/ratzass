package de.simone.btree;

import java.util.List;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import bwapi.UnitType;
import de.simone.command.RUnit;
import de.simone.command.UnitsCenter;

public class IsEnoughtUnitsCondition extends RTask {

    @TaskAttribute(required = true)
    public UnitType unitType;

    @TaskAttribute(required = true)
    public int count;

    @Override
    public Status execute() {
        UnitsCenter unitsCenter = UnitsCenter.getInstance();
        List<RUnit> units = unitsCenter.getUnits(unitType);

        return units.size() >= count ? Status.SUCCEEDED : Status.FAILED;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName() + " " + unitType.toString() + " count:" + count;
    }
}
