package org.event;

import org.eventHandler.AbstractEvent;

public class SyncEvent extends AbstractEvent {
    public SyncEvent(Object source) {
        super(source);
    }

    @Override
    public void sendTo(Object target) {

    }
}
