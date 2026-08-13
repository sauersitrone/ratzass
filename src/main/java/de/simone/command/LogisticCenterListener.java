package de.simone.command;

import java.util.List;

public interface LogisticCenterListener {

    public void updated(List<BuildOrder> buildOrders);
}
