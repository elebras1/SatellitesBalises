package org.event;

import org.eventHandler.AbstractEvent;
import org.model.Mobile;
import org.simulation.program.BuoyProgram;
import org.simulation.program.Program;

public class DiveEvent extends AbstractEvent {
    public DiveEvent(Object source) {
        super(source);
    }

    @Override
    public void sendTo(Object target) {
        ((Program) target).onEndSync((Mobile) getSource());
    }
}
