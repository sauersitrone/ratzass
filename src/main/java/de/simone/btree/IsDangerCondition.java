package de.simone.btree;

import java.util.List;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import bwapi.Unit;
import de.simone.RBWListener;
import de.simone.command.CommandQueue.ResourceType;

public class IsDangerCondition extends RTask {

    @TaskAttribute
    int minUnits;
    @TaskAttribute
    ResourceType resourceType;

    @Override
    public Status execute() {
        List<Unit> allUnits = RBWListener.game.self().getUnits();
        int gasCount = (int) allUnits.stream().filter(u -> u.isGatheringGas()).count();
        int mineralCount = (int) allUnits.stream().filter(u -> u.isGatheringMinerals()).count();

        int count = resourceType == ResourceType.Gas ? gasCount : mineralCount;

        return count >= minUnits ? Status.SUCCEEDED : Status.FAILED;
    }
}
