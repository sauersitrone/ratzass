package de.simone.btree.logistic;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import bwapi.UnitType;
import de.simone.command.CombatCenter;
import de.simone.command.Squad;
import de.simone.command.Squad.SquadStatus;
import de.simone.command.Squad.SquadType;

public class CreateSquadTask extends LogisticTask {

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
        
        if (currentSquad.status == SquadStatus.Assembled) {
            CombatCenter.addSquad(currentSquad);
        }
        return currentSquad.status == SquadStatus.Assembling ? Status.RUNNING : Status.SUCCEEDED;
    }

    private List<UnitType> parseMembers(String members) {
        List<UnitType> memberList = new ArrayList<>();
        String[] memberArray = members.split(",");
        for (String member : memberArray) {
            String[] memberParts = member.trim().split("\\*", 2);
            int count = memberParts.length == 2 ? Integer.parseInt(memberParts[0].trim()) : 1;
            UnitType unitType = UnitType.valueOf(memberParts[memberParts.length - 1].trim());
            for (int i = 0; i < count; i++) {
                memberList.add(unitType);
            }
        }
        return memberList;
    }

    @Override
    public String toString() {
        return super.toString() + " type:" + type + " members:" + members;
    }
}
