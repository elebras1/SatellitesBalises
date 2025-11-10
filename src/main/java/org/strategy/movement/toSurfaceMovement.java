package org.strategy.movement;

import org.model.Mobile;
import org.strategy.MovementStrategy;

import java.awt.*;

public class toSurfaceMovement implements MovementStrategy {
    private final int yMin;
    private int speed;


    public toSurfaceMovement( int yMin, int speed ) {
        this.yMin = yMin;
        this.speed = speed;
    }


    @Override
    public void move(Mobile mobile) {
        Point point = mobile.getPoint();
        if (point.y + speed <= yMin) {
            speed = 0;
        }
        mobile.setPoint(new Point((point.y+speed), point.y));
    }
}
