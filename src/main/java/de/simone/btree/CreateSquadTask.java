package de.simone.btree;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import bwapi.UnitType;
import de.simone.command.CombatCenter;
import de.simone.command.Squad;
import de.simone.command.Squad.SquadStatus;
import de.simone.command.Squad.SquadType;

public class CreateSquadTask extends RTask {

    @TaskAttribute(required = true)
    public SquadType type;

    // coma separated list of unitTypes to recruit
    @TaskAttribute(required = true)
    public String members;

    private Squad currentSquad;

    @Override
    public Status execute() {
        List<UnitType> memberList = parseMembers(members);

        if (getStatus() == Status.RUNNING) {
            currentSquad.recruitMembers();
        } else {
            currentSquad = new Squad(type, memberList);
            currentSquad.recruitMembers();
        }
        
        if (currentSquad.status == SquadStatus.Idle) {
            CombatCenter.getInstance().addSquad(currentSquad);
        }
        return currentSquad.status == SquadStatus.Building ? Status.RUNNING : Status.SUCCEEDED;
    }

    private List<UnitType> parseMembers(String members) {
        List<UnitType> memberList = new ArrayList<>();
        String[] memberArray = members.split(",");
        for (String member : memberArray) {
            UnitType unitType = UnitType.valueOf(member.trim());
            memberList.add(unitType);
        }
        return memberList;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName() + " type:" + type + " members:" + members;
    }
}
