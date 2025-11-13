package org.event;

import org.eventHandler.AbstractEvent;
import org.model.Mobile;
import org.simulation.SimulationInterface;

public class DataCollectionCompleteEvent extends AbstractEvent {
    public DataCollectionCompleteEvent(Object source) {
        super(source);
    }

    @Override
    public void sendTo(Object target) {
        ((SimulationInterface) target).onDataCollectionComplete((Mobile) getSource());
    }
}
