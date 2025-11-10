package org.strategy.movement;

import org.model.Mobile;
import org.simulation.SimulationContext;
import org.strategy.MovementStrategy;

import java.awt.*;

public class HorizontalMovementSatellite implements MovementStrategy {

    private final SimulationContext simulationContext;
    private final int speed;

    public HorizontalMovementSatellite(SimulationContext simulationContext, int speed) {
        this.simulationContext = simulationContext;
        this.speed = speed;
    }

    @Override
    public void move(Mobile mobile) {
        Point point = mobile.getPoint();
        mobile.setPoint(new Point((point.x+speed % simulationContext.getWidth()), point.y));
    }
}
