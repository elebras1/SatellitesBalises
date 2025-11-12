package org.event;

import org.eventHandler.AbstractEvent;
import org.model.Mobile;

import java.awt.*;

public class SyncEvent extends AbstractEvent {
    public SyncEvent(Object source) {
        super(source);
    }

    @Override
    public void sendTo(Object target) {
        Mobile sourceMobile = (Mobile) getSource();
        Mobile targetMobile = (Mobile) target;

        targetMobile.setDataCollected(targetMobile.getDataCollected() + sourceMobile.getDataCollected());
        sourceMobile.setDataCollected(0);
        System.out.println("data synced from source to target. Target now has: " + targetMobile.getDataCollected());

        sourceMobile.getEventHandler().send(new DiveEvent(sourceMobile));
        targetMobile.stopCollectingData();
    }
}
