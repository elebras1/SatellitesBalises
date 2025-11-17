package org.simulation.program;

import org.event.EndSyncEvent;
import org.event.MovementEvent;
import org.event.StartSyncEvent;
import org.event.WaitingEvent;
import org.eventHandler.AbstractEvent;
import org.eventHandler.EventHandler;
import org.model.Buoy;
import org.model.Mobile;
import org.model.Satellite;
import org.simulation.SimulationContext;
import org.strategy.MovementStrategy;
import org.strategy.movement.DiveMovement;
import org.strategy.movement.ToSurfaceMovement;

public class BuoyProgram implements Program {
    private final SimulationContext context;
    private final EventHandler eventHandler;
    private final MovementStrategy movementStrategyOrigin;
    private final Buoy buoy;

    public BuoyProgram(Buoy buoy, SimulationContext context) {
        this.buoy = buoy;
        this.context = context;
        this.eventHandler = new EventHandler();
        this.movementStrategyOrigin = buoy.getMovementStrategy();
    }

    @Override
    public EventHandler getEventHandler() {
        return this.eventHandler;
    }

    @Override
    public void registerListenerToModel(Class<? extends AbstractEvent> eventClass, Object listener) {
        this.buoy.getEventHandler().registerListener(eventClass, listener);
    }

    @Override
    public void onDataCollectionComplete(Mobile mobile) {
        mobile.setMovementStrategy(new ToSurfaceMovement(this.context, 1));
    }

    @Override
    public void onDataCollection(Mobile mobile) {
        mobile.setMovementStrategy(this.movementStrategyOrigin);
        ((Buoy)mobile).collectingData();
    }

    @Override
    public void onEndSync(Mobile mobile) {
        mobile.setMovementStrategy(new DiveMovement((int) mobile.getStartDepth().getY(),1));
    }

    @Override
    public void process() {
        this.eventHandler.send(new MovementEvent(this));
    }

    @Override
    public void onNewSatellite(Satellite satellite) {
        this.registerListenerToModel(WaitingEvent.class, satellite);
        this.registerListenerToModel(StartSyncEvent.class, satellite);
        this.registerListenerToModel(EndSyncEvent.class, satellite);
    }
}
