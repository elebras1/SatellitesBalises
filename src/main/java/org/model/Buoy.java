package org.model;

import org.eventHandler.EventHandler;

import java.awt.*;

public class Buoy implements Mobile {
    private final EventHandler eventHandler;
    private final int width;

    public Buoy(int width) {
        this.eventHandler = new EventHandler();
        this.width = width;
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

    @Override
    public int getMobileWidth() {
        return this.width;
    }
}
