package org.event;

import org.eventHandler.AbstractEvent;
import org.model.Mobile;

public class MovementEvent extends AbstractEvent {
    public MovementEvent(Object source) {
        super(source);
    }

    @Override
    public void sendTo(Object target) {
        Mobile mobile = (Mobile) target;
        mobile.move();
    }
}
