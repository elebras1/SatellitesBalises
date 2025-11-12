package org.model;

import org.event.*;
import org.eventHandler.EventHandler;
import org.strategy.MovementStrategy;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Buoy implements Mobile {
    private final EventHandler eventHandler;
    private final int width;
    private Point point;
    private MovementStrategy movementStrategy;
    private int dataCollected;
    private final int maxData;
    private boolean isCollecting = true;
    private final Random random;
    private final Point startDepth;



    public Buoy(int width, int maxData, Point startDepth) {
        this.startDepth = startDepth;
        this.eventHandler = new EventHandler();
        this.width = width;
        this.maxData = maxData;
        this.random = new Random();
        this.setPoint( this.startDepth);
    }

    @Override
    public EventHandler getEventHandler() {
        return this.eventHandler;
    }

    @Override
    public void move() {
        this.collectData();
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

    public Point getStartDepth() {return startDepth;}

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

    public void startSync(Satellite satellite) {
        Point sourcePosition = this.getPoint();
        Point targetPosition = satellite.getPoint();

        if ((sourcePosition.x) >= (targetPosition.x - 20) && (sourcePosition.x) <= (targetPosition.x + 20)
                && this.getDataCollected() != 0 && !this.isCollecting() && !satellite.isCollecting()) {

            this.getEventHandler().send(new StartSyncViewEvent(this));
            satellite.startSync();

            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

            scheduler.schedule(() -> {
                //simulation du transfert terminé après 2 secondes
                satellite.setDataCollected(satellite.getDataCollected() + this.getDataCollected());
                this.setDataCollected(0);

                this.getEventHandler().send(new SyncEvent(this));
                this.endSync(satellite);

                scheduler.shutdown();

            }, 2, TimeUnit.SECONDS); // attendre 2 secondes sans bloquer

        }
    }

    public void endSync(Satellite satellite) {
        satellite.endSync();
        this.getEventHandler().send(new EndSyncViewEvent(this));
        this.getEventHandler().send(new DiveEvent(this));
    }
}
