package de.simone.btree;

import java.util.List;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import de.simone.command.Squad;
import de.simone.command.Squad.SquadType;
import de.simone.command.UnitsCenter;

public class IsSquadNeededCondition extends RTask {

    @TaskAttribute(required = true)
    public SquadType type;

    @TaskAttribute(required = true)
    public int count;

    @Override
    public Status execute() {
        UnitsCenter unitsCenter = UnitsCenter.getInstance();
        List<Squad> units = unitsCenter.getSquads();
        long c = units.stream().filter(s -> s.type == type).count();
        return c < count ? Status.SUCCEEDED : Status.FAILED;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName() + " " + type.toString() + " count:" + count;
    }
}
