package org.strategy.movement;

import org.model.Mobile;
import org.strategy.MovementStrategy;

import java.awt.*;

public class diveMovement implements MovementStrategy {
    private final int yMax;
    private int speed;


    public diveMovement(int yMax, int speed ) {
        this.yMax = yMax;
        this.speed = speed;
    }


    @Override
    public void move(Mobile mobile) {
        Point point = mobile.getPoint();
        if (point.y + speed + mobile.getMobileWidth() >= yMax ) {
            speed = 0;
        }
        mobile.setPoint(new Point((point.y+speed), point.y));
    }
}
