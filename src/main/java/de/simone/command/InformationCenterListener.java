package de.simone.command;


import java.util.List;

import bwapi.Unit;

public interface InformationCenterListener {

    public void informationUpdated(List<Unit> units);
}
