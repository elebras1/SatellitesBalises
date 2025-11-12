
package org.event;

import org.eventHandler.AbstractEvent;
import org.model.Mobile;
import org.simulation.Simulation;
import org.strategy.movement.DiveMovement;
import org.strategy.movement.ToSurfaceMovement;

public class DataCollectionEvent extends AbstractEvent {
    public DataCollectionEvent(Object source) {
        super(source);
    }

    @Override
    public void sendTo(Object target) {
        Mobile sourceMobile = (Mobile) getSource();
        ((Simulation) target).onDataCollection((Mobile) this.source);
    }
}
