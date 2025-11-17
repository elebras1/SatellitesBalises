package org.event;

import org.eventHandler.AbstractEvent;
import org.model.Buoy;
import org.model.Mobile;
import org.model.Satellite;

public class StartSyncEvent extends AbstractEvent {
    public StartSyncEvent(Object source) {
        super(source);
    }

    @Override
    public void sendTo(Object target) {
        ((Mobile) getSource()).onStartSync((Mobile) target);
    }
}

