package org.event;

import org.eventHandler.AbstractEvent;
import org.model.Mobile;
import org.view.View;

public class EndSyncViewEvent extends AbstractEvent {
    public EndSyncViewEvent(Object source) {
        super(source);
    }

    @Override
    public void sendTo(Object target) {
        ((View) target).endSync();
    }
}
