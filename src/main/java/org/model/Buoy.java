package org.model;

import org.event.MovementEvent;
import org.eventHandler.EventHandler;
import org.strategy.MovementStrategy;

import java.awt.*;

public class Buoy implements Mobile {
    private final EventHandler eventHandler;
    private final int width;
    private Point point;
    private MovementStrategy movementStrategy;

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
        this.movementStrategy.move(this);
        this.eventHandler.send(new MovementEvent(this));
    }

    @Override
    public Point getPoint() {
        return point;
    }

    @Override
    public void setPoint(Point point) {
        this.point = point;
    }

    @Override
    public int getMobileWidth() {
        return this.width;
    }

    @Override
    public MovementStrategy getMovementStrategy() {
        return movementStrategy;
    }

    @Override
    public void setMovementStrategy(MovementStrategy movementStrategy) {
        this.movementStrategy = movementStrategy;
    }
}
