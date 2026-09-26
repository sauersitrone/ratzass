package de.simone.command;

import bwapi.Position;
import de.simone.RBWListener;
import de.simone.command.CombatCenter.RequestName;
import de.simone.command.StarCraftConstants.OrderStatus;

public class CombatRequest {
    public RequestName request = RequestName.Unknown;
    public String squadID = "";
    public int cycle = RBWListener.game.getFrameCount();

    // the status field should only be changed by Task classes
    public OrderStatus status = OrderStatus.Queued;

    public String message = "";
    public Position position = null;

    private CombatRequest() {
        //
    }

    public CombatRequest(Squad squad, RequestName request, Position position) {
        this.request = request;
        this.squadID = squad.squadID;
        this.position = position;
    }
}
