package de.simone.btree.combat;

import java.util.List;

import bwapi.Position;
import bwapi.Unit;
import de.simone.command.Squad;
import de.simone.command.UnitsCenter;

public class isDangerousCondition extends CombatTask {

    @Override
    public Status execute() {
        Squad squad = getObject();
        int myUnits = squad.getAliveMembers().size();
        Position center = squad.getPosition();
        List<Unit> enemies = UnitsCenter.getEnemyUnits(center, 200);
        Status status = enemies.size() > myUnits ? Status.SUCCEEDED : Status.FAILED;
        
        if(status == Status.SUCCEEDED)
        sendCommunication("We are in dangerous situation !!");

        return status;
    }
}
