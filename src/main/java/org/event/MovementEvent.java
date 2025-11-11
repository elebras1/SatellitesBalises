package org.event;

import nicellipse.component.NiImage;
import org.eventHandler.AbstractEvent;
import org.model.Mobile;
import org.simulation.Simulation;
import org.view.View;

public class MovementEvent extends AbstractEvent {
    public MovementEvent(Object source) {
        super(source);
    }

    @Override
    public void sendTo(Object target) {
        // TODO Need to change this part to avoid instanceof
        if (getSource() instanceof Simulation) {
            Mobile mobile = (Mobile) target;
            mobile.move();
        }else{
            Mobile mobile = (Mobile) getSource();
            View view = (View) target;
            view.move(mobile.getPoint());
        }
    }
}
