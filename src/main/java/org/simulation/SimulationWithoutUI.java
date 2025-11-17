package org.simulation;

import nicellipse.component.NiSpace;
import org.event.*;
import org.eventHandler.EventHandler;
import org.model.Buoy;
import org.model.Mobile;
import org.model.Satellite;
import org.strategy.MovementStrategy;
import org.strategy.movement.*;
import org.view.BuoyViewHeadless;
import org.view.SatelliteViewHeadless;

import java.awt.*;
import java.io.File;
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

    @Override
    public SimulationContext getContext() {
        return this.context;
    }

    @Override
    public Buoy createBuoy(int width, int maxData, int x, int y, MovementStrategy movementStrategy) throws IOException {
        Buoy buoy = this.addBuoy(width, maxData, x, y, movementStrategy);
        this.registerSatelliteSignleBuoys(this.satellites, buoy);
        return buoy;
    }

    @Override
    public Satellite createSatellite(int width, int x, int y, MovementStrategy movementStrategy) throws IOException {
        Satellite satellite = this.addSatellite(width, x, y, movementStrategy);
        this.registerSatelliteToBuoySingle(this.buoys, satellite);
        return satellite;
    }

    private void initialize() {

    }

    private void registerSatelliteSignleBuoys(List<Satellite> satellites, Buoy buoy) {
        for (Satellite satellite : satellites) {
            buoy.getEventHandler().registerListener(WaitingEvent.class, satellite);
            buoy.getEventHandler().registerListener(EndSyncEvent.class, satellite);
        }
        this.eventHandler.registerListener(MovementEvent.class, buoy);
        buoy.getEventHandler().registerListener(DataCollectionCompleteEvent.class, this);
        buoy.getEventHandler().registerListener(DataCollectionEvent.class, this);
        buoy.getEventHandler().registerListener(DiveEvent.class, this);
    }

    private void registerSatelliteToBuoySingle(List<Buoy> buoys, Satellite satellite) {
        for (Buoy buoy : buoys) {
            buoy.getEventHandler().registerListener(WaitingEvent.class, satellite);
            buoy.getEventHandler().registerListener(EndSyncEvent.class, satellite);
        }
        this.eventHandler.registerListener(MovementEvent.class, satellite);
    }

    private Satellite addSatellite(int width, int x, int y, MovementStrategy movementStrategy) throws IOException {
        Satellite satellite = new Satellite(width);
        satellite.setPoint(new Point(x, y));
        this.movementStrategies.put(satellite, movementStrategy);
        SatelliteViewHeadless satelliteView1 = new SatelliteViewHeadless(new File("src/main/resources/satellite.png"));
        satelliteView1.setLocation(satellite.getPoint());
        this.space.add(satelliteView1);
        satellite.getEventHandler().registerListener(PositionChangedEvent.class, satelliteView1);
        satellite.getEventHandler().registerListener(StartSyncViewEvent.class, satelliteView1);
        satellite.getEventHandler().registerListener(EndSyncViewEvent.class, satelliteView1);
        satellite.setMovementStrategy(movementStrategy);
        this.satellites.add(satellite);
        this.space.repaint();
        return satellite;
    }

    private Buoy addBuoy(int width, int maxData, int x, int y, MovementStrategy movementStrategy) throws IOException {
        Buoy buoy = new Buoy(width, maxData,new Point(x, y));
        System.out.println(x  +","+y);
        this.movementStrategies.put(buoy, movementStrategy);
        BuoyViewHeadless buoyView1 = new BuoyViewHeadless(new File("src/main/resources/submarine.png"));
        buoyView1.setLocation(buoy.getPoint());
        this.space.add(buoyView1);
        this.space.setComponentZOrder(buoyView1, 0);
        buoy.getEventHandler().registerListener(PositionChangedEvent.class, buoyView1);
        buoy.getEventHandler().registerListener(StartSyncViewEvent.class, buoyView1);
        buoy.getEventHandler().registerListener(EndSyncViewEvent.class, buoyView1);
        buoy.setMovementStrategy(movementStrategy);
        this.buoys.add(buoy);
        this.space.repaint();
        return buoy;
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
