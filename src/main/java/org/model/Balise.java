package org.model;

import org.eventHandler.EventHandler;

public class Balise implements Mobile {
    private final EventHandler eventHandler;

    public Balise() {
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
