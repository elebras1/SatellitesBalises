package org.event;

import org.eventHandler.AbstractEvent;

public class WaitingEvent extends AbstractEvent {
    public WaitingEvent(Object source) {
        super(source);
    }

    @Override
    public void sendTo(Object target) {

    }
}
