package org.event;

import org.eventHandler.AbstractEvent;
import org.model.Mobile;
import org.simulation.Simulation;

public class DiveEvent extends AbstractEvent {
    public DiveEvent(Object source) {
        super(source);
    }

    @Override
    public void sendTo(Object target) {
        ((Simulation) target).dive((Mobile) getSource());
    }
}
