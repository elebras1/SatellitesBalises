package org.event;

import org.eventHandler.AbstractEvent;
import org.model.Mobile;
import org.view.View;

public class StartSyncViewEvent extends AbstractEvent {
    public StartSyncViewEvent(Object source) {
        super(source);
    }

    @Override
    public void sendTo(Object target) {
        ((View) target).startSync((Mobile) getSource());
    }
}
