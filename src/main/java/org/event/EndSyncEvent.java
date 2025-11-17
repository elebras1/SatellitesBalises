package org.event;

import org.eventHandler.AbstractEvent;
import org.model.Mobile;

public class EndSyncEvent extends AbstractEvent {
    public EndSyncEvent(Object source) {
        super(source);
    }

    @Override
    public void sendTo(Object target) {
        ((Mobile) getSource()).onEndSync((Mobile) target);
    }
}
