package org.model;

import org.eventHandler.EventHandler;

import java.awt.*;

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

    @Override
    public Point getPoint() {
        return null;
    }

    @Override
    public void setPoint(Point point) {

    }
}
