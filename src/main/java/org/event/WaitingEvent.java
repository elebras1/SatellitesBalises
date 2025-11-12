package org.event;

import org.eventHandler.AbstractEvent;
import org.model.Buoy;
import org.model.Mobile;
import org.model.Satellite;

import java.awt.*;

public class WaitingEvent extends AbstractEvent {
    public WaitingEvent(Object source) {
        super(source);
    }

    @Override
    public void sendTo(Object target) {
        ((Buoy) getSource()).startSync((Satellite) target);
    }
}
