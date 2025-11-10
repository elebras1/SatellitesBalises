package org.strategy.movement;

import org.model.Mobile;
import org.strategy.MovementStrategy;

import java.awt.*;

public class HorizontalMovement implements MovementStrategy {

    private int xMax;
    private int speed;
    private int mobileWidth;

    public HorizontalMovement(int xMax, int speed, int mobileWidth) {
        this.xMax = xMax;
        this.speed = speed;
        this.mobileWidth = mobileWidth;
    }


    @Override
    public void move(Mobile mobile) {
        Point point = mobile.getPoint();
        if (point.x + speed + mobileWidth >= xMax || point.x + speed <= 0) {
            speed = speed * -1;
        }
        mobile.setPoint(new Point((point.x+speed), point.y));
    }
}
