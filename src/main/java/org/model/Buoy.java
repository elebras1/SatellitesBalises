package org.model;

import org.event.DataCollectionCompleteEvent;
import org.event.MovementEvent;
import org.eventHandler.EventHandler;
import org.strategy.MovementStrategy;

import java.awt.*;
import java.util.Random;

public class Buoy implements Mobile {
    private final EventHandler eventHandler;
    private final int width;
    private Point point;
    private MovementStrategy movementStrategy;
    private int dataCollected;
    private final int maxData;
    private boolean isCollecting = true;
    private final Random random;

    public Buoy(int width, int maxData) {
        this.eventHandler = new EventHandler();
        this.width = width;
        this.maxData = maxData;
        this.random = new Random();
    }

    @Override
    public EventHandler getEventHandler() {
        return this.eventHandler;
    }

    @Override
    public void move() {
        this.collectData();
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

    private void collectData() {
        if (this.isCollecting && this.dataCollected < this.maxData) {
            this.dataCollected += 1 + this.random.nextInt(5);
            if (this.dataCollected >= this.maxData) {
                this.dataCollected = this.maxData;
                this.onDataCollectionComplete();
            }
        }
    }

    private void onDataCollectionComplete() {
        this.isCollecting = false;
        this.eventHandler.send(new DataCollectionCompleteEvent(this));
    }
}
