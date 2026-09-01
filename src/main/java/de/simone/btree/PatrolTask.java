package de.simone.btree;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import bwapi.Position;
import de.simone.command.Squad;
import de.simone.command.UnitsCenter;

public class PatrolTask extends RTask {

    // x,y position to patrol
    @TaskAttribute(required = true)
    public String position;

    @Override
    public Status execute() {        
        Position patrolPosition = null;

        if ("Random".equalsIgnoreCase(position)) {
            // TODO: check if is there a chokpoint or a point of interest to patrol
            // TODO: implement random patrol position logic
            patrolPosition = new Position(0, 0);
        }

        // postion coordinates
        if (position.contains(",")) {
            String[] coords = position.split(",");
            int x = Integer.parseInt(coords[0].trim());
            int y = Integer.parseInt(coords[1].trim());
            patrolPosition = new Position(x, y);
        }
         
        Squad squad = UnitsCenter.getInstance().getSquads(3);
        if (squad == null) {
            return Status.FAILED;
        }
        squad.patrol(patrolPosition);

        return Status.SUCCEEDED;
    }
}
