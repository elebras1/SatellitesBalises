package org.model;

import org.eventHandler.EventHandler;

public interface Mobile {
    EventHandler getEventHandler();

    void move();
}
