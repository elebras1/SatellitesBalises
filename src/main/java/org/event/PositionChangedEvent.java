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
        Mobile mobile = (Mobile) this.getSource();
        View view = (View) target;
        view.move(mobile.getPoint());
    }
}
