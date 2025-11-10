package org.model;

import org.eventHandler.EventHandler;

import java.awt.*;

public interface Mobile {
    EventHandler getEventHandler();

    void move();

    Point getPoint();

    void setPoint(Point point);
}
