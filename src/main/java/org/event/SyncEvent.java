package org.event;

import org.eventHandler.AbstractEvent;
import org.model.Buoy;
import org.model.Mobile;
import org.model.Satellite;

import java.awt.*;

public class SyncEvent extends AbstractEvent {
    public SyncEvent(Object source) {
        super(source);
    }

    @Override
    public void sendTo(Object target) {
        ((Buoy) getSource()).endSync((Satellite) target);
    }
}
