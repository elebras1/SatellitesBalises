package org.model;

import org.eventHandler.EventHandler;

public class Satellite implements Mobile {
    private final EventHandler eventHandler;

    public Satellite() {
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
