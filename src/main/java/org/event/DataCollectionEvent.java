
package org.event;

import org.eventHandler.AbstractEvent;
import org.model.Mobile;
import org.simulation.Simulation;

public class DataCollectionEvent extends AbstractEvent {
    public DataCollectionEvent(Object source) {
        super(source);
    }

    @Override
    public void sendTo(Object target) {
        ((Simulation) target).onDataCollection((Mobile) this.source);
        System.out.println(" mvt strat : before data collection ");
    }
}
