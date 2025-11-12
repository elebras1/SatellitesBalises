package org.event;

import org.eventHandler.AbstractEvent;
import org.model.Buoy;
import org.model.Mobile;
import org.model.Satellite;
import org.view.BuoyView;
import org.view.View;

import java.awt.*;

public class SyncViewEvent extends AbstractEvent {
    public SyncViewEvent(Object source) {
        super(source);
    }

    @Override
    public void sendTo(Object target) {
        ((View) target).startSync((Mobile) getSource());
    }
}
