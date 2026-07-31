package de.simone.game.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import bwapi.UnitType;

public class RResources {

    public List<RResource> resources = new ArrayList<>();
    public Map<UnitType, Integer> units = new TreeMap<>();

    public RResources() {
        //
    }

    public RResources(RResource resource) {
        this.resources.add(resource);
    }

    public RResources(Map<UnitType, Integer> units) {
        this.units.putAll(units);
    }

}
