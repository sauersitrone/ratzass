package de.simone.btree.logistic;

import java.util.List;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import de.simone.command.Squad;
import de.simone.command.Squad.SquadType;
import de.simone.command.UnitsCenter;

public class IsSquadNeededCondition extends LogisticTask {

    @TaskAttribute(required = true)
    public SquadType type;

    @TaskAttribute(required = true)
    public int count;

    @Override
    public Status execute() {
        List<Squad> units = UnitsCenter.getSquads();
        long c = units.stream().filter(s -> s.type == type).count();
        return c < count ? Status.SUCCEEDED : Status.FAILED;
    }

    @Override
    public String toString() {
        return super.toString() + " " + type.toString() + " count:" + count;
    }
}
