
package org.event;

import org.eventHandler.AbstractEvent;
import org.model.Mobile;
import org.simulation.SimulationInterface;

public class DataCollectionEvent extends AbstractEvent {
    public DataCollectionEvent(Object source) {
        super(source);
    }

    @Override
    public void sendTo(Object target) {
        ((SimulationInterface) target).onDataCollection((Mobile) getSource());
    }
}
