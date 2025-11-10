package org.strategy.movement;

import org.model.Mobile;
import org.strategy.MovementStrategy;

import java.awt.*;

public class HorizontalMovement implements MovementStrategy {

    private int xMax;
    private int vitesse;
    private int largeurMobile;

    public HorizontalMovement(int xMax, int vitesse, int largeurMobile) {
        this.xMax = xMax;
        this.vitesse = vitesse;
        this.largeurMobile = largeurMobile;
    }

    @Override
    public void move(Mobile mobile) {
        Point point = mobile.getPoint();
        mobile.setPoint(new Point((point.x+vitesse), point.y));
        if (point.x+ vitesse >= xMax || point.x + vitesse <= 0) {
            vitesse = vitesse * -1;
        }
    }
}
