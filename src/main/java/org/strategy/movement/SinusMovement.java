package org.strategy.movement;

import org.model.Mobile;
import org.simulation.SimulationContext;
import org.strategy.MovementStrategy;

import java.awt.*;

public class SinusMovement implements MovementStrategy {

    private final SimulationContext simulationContext;
    private int speed;
    private double phase = 0;
    private final double amplitude = 40;
    private final double frequency = 0.02;
    private int initialY;

    public SinusMovement(SimulationContext simulationContext, int speed) {
        this.simulationContext = simulationContext;
        this.speed = speed;
        this.initialY = 0;
    }

    @Override
    public void move(Mobile mobile) {
        Point point = mobile.getPoint();

        if (initialY == 0) {
            initialY = point.y;
        }

        phase += frequency;
        double newX = point.x + speed;
        double newY = initialY + Math.sin(phase) * amplitude;

        if (newX + mobile.getMobileWidth() >= simulationContext.getWidth() || newX <= 0) {
            speed *= -1;
        }

        mobile.setPoint(new Point((int) newX, (int) newY));
    }
}
