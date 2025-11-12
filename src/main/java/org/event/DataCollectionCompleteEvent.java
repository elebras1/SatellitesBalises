package org.event;

import org.eventHandler.AbstractEvent;
import org.model.Mobile;
import org.simulation.Simulation;

public class DataCollectionCompleteEvent extends AbstractEvent {
    public DataCollectionCompleteEvent(Object source) {
        super(source);
    }

    @Override
    public void sendTo(Object target) {
        ((Simulation) target).onDataCollectionComplete((Mobile) getSource());
    }
}
