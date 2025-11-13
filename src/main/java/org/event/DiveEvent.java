package org.event;

import org.eventHandler.AbstractEvent;
import org.model.Mobile;
import org.simulation.SimulationInterface;

public class DiveEvent extends AbstractEvent {
    public DiveEvent(Object source) {
        super(source);
    }

    @Override
    public void sendTo(Object target) {
        ((SimulationInterface) target).onEndSync((Mobile) getSource());
    }
}
