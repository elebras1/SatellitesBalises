package org.event;

import org.eventHandler.AbstractEvent;

public class MovementEvent extends AbstractEvent {
    public MovementEvent(Object source) {
        super(source);
    }

    @Override
    public void sendTo(Object target) {

    }
}
