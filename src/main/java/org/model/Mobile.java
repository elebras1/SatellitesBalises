package org.model;

import org.eventHandler.EventHandler;
import org.strategy.MovementStrategy;

import java.awt.*;

public interface Mobile {
    EventHandler getEventHandler();

    void move();

    Point getPoint();

    void setPoint(Point point);

    int getMobileWidth();

    MovementStrategy getMovementStrategy();

    void setMovementStrategy(MovementStrategy movementStrategy);
}
