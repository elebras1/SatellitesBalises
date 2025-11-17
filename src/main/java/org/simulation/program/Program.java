package org.simulation.program;

import org.eventHandler.AbstractEvent;
import org.eventHandler.EventHandler;
import org.model.Buoy;
import org.model.Mobile;
import org.model.Satellite;

public interface Program {
    EventHandler getEventHandler();
    void registerListenerToModel(Class<? extends AbstractEvent> eventClass, Object listener);
    void onDataCollectionComplete(Mobile mobile);
    void onDataCollection(Mobile mobile);
    void onEndSync(Mobile mobile);
    void process();
    default void onNewBuoy(Buoy buoy) {}
    default void onNewSatellite(Satellite satellite) {}
}
