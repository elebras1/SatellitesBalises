package org.event;

import org.eventHandler.AbstractEvent;
import org.model.Mobile;

import java.awt.*;

public class WaitingEvent extends AbstractEvent {
    public WaitingEvent(Object source) {
        super(source);
    }

    @Override
    public void sendTo(Object target) {
        Mobile sourceMobile = (Mobile) getSource();
        Mobile targetMobile = (Mobile) target;

        Point sourcePosition = sourceMobile.getPoint();
        Point targetPosition = targetMobile.getPoint();

        if ((sourcePosition.x) >= (targetPosition.x - 5) && (sourcePosition.x) <= (targetPosition.x + 5) && sourceMobile.getDataCollected() != 0){
            System.out.println("Syncing data between mobiles at position: " + sourcePosition);
            sourceMobile.getEventHandler().send(new SyncEvent(sourceMobile));
        }
    }
}
