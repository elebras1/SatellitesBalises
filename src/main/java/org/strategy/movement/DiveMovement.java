package org.strategy.movement;

import org.model.Mobile;
import org.strategy.MovementStrategy;

import java.awt.*;

public class DiveMovement implements MovementStrategy {
    private final int yMax;
    private int speed;


    public DiveMovement(int yMax, int speed ) {
        this.yMax = yMax;
        this.speed = speed;
    }


    @Override
    public void move(Mobile mobile) {
        Point point = mobile.getPoint();
        if (point.y>= yMax ) {

            speed = 0;
        }
        mobile.setPoint(new Point(point.x, (point.y+speed)));
    }
}
