package org.model;

import org.eventHandler.EventHandler;

public class Buoy implements Mobile {
    private final EventHandler eventHandler;

    public Buoy() {
        this.eventHandler = new EventHandler();
    }

    @Override
    public EventHandler getEventHandler() {
        return this.eventHandler;
    }

    @Override
    public void move() {

    }
}
