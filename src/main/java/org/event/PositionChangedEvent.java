package org.event;

import org.eventHandler.AbstractEvent;
import org.model.Mobile;
import org.view.View;

public class PositionChangedEvent extends AbstractEvent {
    public PositionChangedEvent(Object source) {
        super(source);
    }

    @Override
    public void sendTo(Object target) {
        ((View) target).move(((Mobile)getSource()).getPoint());
    }
}
