package org.strategy.movement;

import org.event.DataCollectionCompleteEvent;
import org.event.MovementEvent;
import org.event.WaitingEvent;
import org.eventHandler.EventHandler;
import org.model.Mobile;
import org.simulation.SimulationContext;
import org.strategy.MovementStrategy;

import java.awt.*;

public class ToSurfaceMovement implements MovementStrategy {
    private final SimulationContext simulationContext;
    private int speed;


    public ToSurfaceMovement(SimulationContext simulationContext, int speed) {
        this.simulationContext = simulationContext;
        this.speed = speed;
    }


    @Override
    public void move(Mobile mobile) {
        Point point = mobile.getPoint();
        if (point.y <= this.simulationContext.getSeaLevel()) {
            mobile.getEventHandler().send(new WaitingEvent(mobile));
            speed = 0;
        }
        point.y -= this.speed;
    }
}
