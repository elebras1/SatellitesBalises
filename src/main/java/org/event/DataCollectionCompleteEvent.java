package org.event;

import org.eventHandler.AbstractEvent;
import org.model.Mobile;
import org.simulation.program.BuoyProgram;
import org.simulation.program.Program;

public class DataCollectionCompleteEvent extends AbstractEvent {
    public DataCollectionCompleteEvent(Object source) {
        super(source);
    }

    @Override
    public void sendTo(Object target) {
        ((Program) target).onDataCollectionComplete((Mobile) getSource());
    }
}
