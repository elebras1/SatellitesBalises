package org.strategy.movement;

import org.model.Mobile;
import org.strategy.MovementStrategy;

import java.awt.*;

public class VerticalMovement implements MovementStrategy {
    private final int yMax;
    private final int yMin;
    private int speed;


    public VerticalMovement(int yMax, int yMin) {
        this.yMax = yMax;
        this.yMin = yMin;
    }


    @Override
    public void move(Mobile mobile) {
        Point point = mobile.getPoint();
        if (point.y + speed + mobile.getMobileWidth() >= yMax || point.y + speed <= yMin) {
            speed = speed * -1;
        }
        mobile.setPoint(new Point((point.y+speed), point.y));
    }
}
