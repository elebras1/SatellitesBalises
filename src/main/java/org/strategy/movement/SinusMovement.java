package org.strategy.movement;

import org.model.Mobile;
import org.simulation.SimulationContext;
import org.strategy.MovementStrategy;

import java.awt.*;

public class SinusMovement implements MovementStrategy {

    private final SimulationContext simulationContext;
    private int speed;
    private double phase = 0;

    public SinusMovement(SimulationContext simulationContext, int speed) {
        this.simulationContext = simulationContext;
        this.speed = speed;
    }

    @Override
    public void move(Mobile mobile) {
        Point point = mobile.getPoint();

        int newX = point.x + speed;

        phase += 0.01;
        int baseY = point.y;
        int newY = (int) (baseY + Math.sin(phase) * 20);

        if (newX >= simulationContext.getWidth() || newX <= 0) {
            speed *= -1;
        }
        mobile.setPoint(new Point(newX, newY));
    }
}
