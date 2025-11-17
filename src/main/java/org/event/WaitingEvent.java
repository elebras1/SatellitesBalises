package org.event;

import org.eventHandler.AbstractEvent;
import org.model.Buoy;
import org.model.Mobile;
import org.model.Satellite;

public class WaitingEvent extends AbstractEvent {
    public WaitingEvent(Object source) {
        super(source);
    }

    @Override
    public void sendTo(Object target) {
        boolean canSync = ((Mobile) getSource()).canSync(((Mobile) target));
        if (canSync) {
            ((Mobile) getSource()).onStartSync(((Mobile) target));
        }
    }
}
