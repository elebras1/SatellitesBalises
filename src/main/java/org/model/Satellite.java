package org.model;


import org.event.PositionChangedEvent;
import org.event.SyncViewEvent;
import org.eventHandler.EventHandler;
import org.strategy.MovementStrategy;

import java.awt.*;

public class Satellite implements Mobile {
    private final EventHandler eventHandler;
    private final int width;
    private Point point;
    private MovementStrategy movementStrategy;
    private int dataCollected;
    private boolean isCollecting;


    public Satellite(int width) {
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
        this.eventHandler.send(new PositionChangedEvent(this));
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

    @Override
    public int getDataCollected() {
        return dataCollected;
    }

    @Override
    public void setDataCollected(int dataCollected) {
        this.dataCollected = dataCollected;
    }

    @Override
    public Point getStartDepth() {
        return null;
    }

    @Override
    public void collectingData() {
        this.isCollecting = true;
    }

    @Override
    public void stopCollectingData() {
        this.isCollecting = false;
    }

    @Override
    public boolean isCollecting() {
        return this.isCollecting;
    }

    public void startSync() {
        this.collectingData();
        this.eventHandler.send(new SyncViewEvent(this));
    }

    public void endSync() {
        this.stopCollectingData();
    }
}
