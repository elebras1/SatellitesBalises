package org.simulation.program;

import org.event.MovementEvent;
import org.event.SyncEvent;
import org.event.WaitingEvent;
import org.eventHandler.AbstractEvent;
import org.eventHandler.EventHandler;
import org.model.Buoy;
import org.model.Mobile;
import org.model.Satellite;
import org.simulation.SimulationContext;
import org.strategy.MovementStrategy;

public class SatelliteProgram implements Program {
    private final EventHandler eventHandler;
    private final Satellite satellite;

    public SatelliteProgram(Satellite satellite) {
        this.satellite = satellite;
        this.eventHandler = new EventHandler();
    }

    public Satellite getSatellite() {
        return this.satellite;
    }

    @Override
    public EventHandler getEventHandler() {
        return this.eventHandler;
    }

    @Override
    public void registerListenerToModel(Class<? extends AbstractEvent> eventClass, Object listener) {
        this.satellite.getEventHandler().registerListener(eventClass, listener);
    }

    @Override
    public void onDataCollectionComplete(Mobile mobile) {
    }

    @Override
    public void onDataCollection(Mobile mobile) {
    }

    @Override
    public void onEndSync(Mobile mobile) {

    }

    @Override
    public void process() {
        this.eventHandler.send(new MovementEvent(this));
    }

    @Override
    public void onNewBuoy(Buoy buoy) {
        buoy.getEventHandler().registerListener(WaitingEvent.class, this.satellite);
        buoy.getEventHandler().registerListener(SyncEvent.class, this.satellite);
    }
}
