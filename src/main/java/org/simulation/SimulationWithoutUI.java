package org.simulation;

import nicellipse.component.NiRectangle;
import nicellipse.component.NiSpace;
import org.event.*;
import org.eventHandler.EventHandler;
import org.model.Buoy;
import org.model.Mobile;
import org.model.Satellite;
import org.strategy.MovementStrategy;
import org.strategy.movement.*;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimulationWithoutUI implements SimulationInterface, World {
    private final SimulationContext context;
    private final NiSpace space;
    private final EventHandler eventHandler;
    private Map<Mobile, MovementStrategy> movementStrategies;
    private List<Satellite> satellites;
    private List<Buoy> buoys;


    public SimulationWithoutUI(SimulationContext context) {
        this.context = context;
        this.space = new NiSpace("Simulation Space", new Dimension(context.getWidth(), context.getHeight()));
        this.eventHandler = new EventHandler();
        this.movementStrategies = new HashMap<>();
        this.satellites = new ArrayList<>();
        this.buoys = new ArrayList<>();
        this.initialize();
    }

    private void initialize() {
        try {
            Buoy buoy1 = this.addBuoy(64, 800, this.context.getWidth() / 2, this.context.getHeight() - 150, new HorizontalMovement(this.context, 1));
            Buoy buoy2 = this.addBuoy(64, 4000, this.context.getWidth() / 2 - 100, this.context.getHeight() - 200, new HorizontalMovement(this.context, 2));
            Buoy buoy3 = this.addBuoy(64, 2000, this.context.getWidth() / 2 + 100, this.context.getHeight() - 250, new SinusMovement(this.context, 1));
            Satellite satellite1 = this.addSatellite(64, this.context.getWidth() / 2, 150, new HorizontalMovementSatellite(this.context, 1));
            Satellite satellite2 = this.addSatellite(64, (this.context.getWidth()-30) / 2, 100, new HorizontalMovementSatellite(this.context, 1));

            this.registerSatellite(this.satellites, this.buoys);
            this.registersListBuoys(this.buoys);
            this.registersListSatellites(this.satellites);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void registerSatellite(List<Satellite> satellites, List<Buoy> buoys) {
        for (Satellite satellite : satellites) {
            for (Buoy buoy : buoys) {
                buoy.getEventHandler().registerListener(WaitingEvent.class, satellite);
                buoy.getEventHandler().registerListener(SyncEvent.class, satellite);
            }
        }
    }

    private void registersListBuoys(List<Buoy> buoys) {
        for(Mobile buoy : buoys) {
            this.eventHandler.registerListener(MovementEvent.class, buoy);
            buoy.getEventHandler().registerListener(DataCollectionCompleteEvent.class, this);
            buoy.getEventHandler().registerListener(DataCollectionEvent.class, this);
            buoy.getEventHandler().registerListener(DiveEvent.class, this);
        }
    }

    private void registersListSatellites(List<Satellite> satellites) {
        for(Mobile satellite : satellites) {
            this.eventHandler.registerListener(MovementEvent.class, satellite);
        }
    }

    private Satellite addSatellite(int width, int x, int y, MovementStrategy movementStrategy) throws IOException {
        Satellite satellite = new Satellite(width);
        satellite.setPoint(new Point(x, y));
        this.movementStrategies.put(satellite, movementStrategy);
        satellite.setMovementStrategy(movementStrategy);
        this.satellites.add(satellite);
        return satellite;
    }

    private Buoy addBuoy(int width, int maxData, int x, int y, MovementStrategy movementStrategy) throws IOException {
        Buoy buoy = new Buoy(width, maxData,new Point(x, y));
        this.movementStrategies.put(buoy, movementStrategy);
        buoy.setMovementStrategy(movementStrategy);
        this.buoys.add(buoy);
        return buoy;
    }

    private void addSea() {
        NiRectangle sea = new NiRectangle();
        sea.setDimension(new Dimension(this.context.getWidth(), this.context.getHeight() - this.context.getSeaLevel()));
        sea.setBackground(Color.BLUE);
        sea.setLocation(0, this.context.getSeaLevel());
        this.space.add(sea);
    }

    public void process() {
        while (true) {
            try {
                Thread.sleep(10);
                this.eventHandler.send(new MovementEvent(this));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDataCollectionComplete(Mobile mobile) {
        mobile.setMovementStrategy(new ToSurfaceMovement(this.context, 1));
    }

    @Override
    public void onDataCollection(Mobile mobile) {
        mobile.setMovementStrategy(this.movementStrategies.get(mobile));
        ((Buoy)mobile).collectingData();
    }

    @Override
    public void onEndSync(Mobile mobile) {
        mobile.setMovementStrategy(new DiveMovement((int) mobile.getStartDepth().getY(),1));
    }
}
