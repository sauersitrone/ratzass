package de.simone;

import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.runtime.client.EPRuntime;
import com.espertech.esper.runtime.client.EPStatement;
import com.espertech.esper.runtime.client.UpdateListener;

public class UnitListener implements UpdateListener {
    
    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents, EPStatement statement, EPRuntime runtime) {

        for (EventBean event : newEvents) {
            Object unit = event.get("unit");
            System.out.println("UnitListener: " + unit);
        }
    }
}
