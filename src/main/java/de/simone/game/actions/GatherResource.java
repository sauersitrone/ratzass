package de.simone.game.actions;

import com.badlogic.gdx.ai.GdxAI;

import bwapi.Unit;
import de.simone.Ratzass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GatherResource extends StarCraftAction {

    public GatherResource() {
        //
    }

    public GatherResource(RResource resource) {
        this.resource = resource;
    }

    @Override
    public void execute() {
        Unit unit = Ratzass.bwClient.getGame().getUnit(this.unit.unitID);
        boolean status = unit.train(this.targetUnit.unitType);
        setSuccess(status);
    }

    @Override
    public RResources requiredResources() {
        return getRequiredUnit();
    }

    @Override
    public RResources producedResources() {
        RResources resources = new RResources(resource);
        return resources;
    }
}
