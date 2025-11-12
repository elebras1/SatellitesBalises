package org.simulation;

import nicellipse.component.NiRectangle;
import nicellipse.component.NiSpace;
import org.event.*;
import org.eventHandler.EventHandler;
import org.model.Buoy;
import org.model.Mobile;
import org.model.Satellite;
import org.strategy.MovementStrategy;
import org.strategy.movement.HorizontalMovement;
import org.strategy.movement.HorizontalMovementSatellite;
import org.strategy.movement.SinusMovement;
import org.strategy.movement.ToSurfaceMovement;
import org.view.BuoyView;
import org.view.SatelliteView;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Simulation {
    private final SimulationContext context;
    private final NiSpace space;
    private final EventHandler eventHandler;
    private Map<Mobile, MovementStrategy> movementStrategies;

    public Simulation(SimulationContext context) {
        this.context = context;
        this.space = new NiSpace("Simulation Space", new Dimension(context.getWidth(), context.getHeight()));
        this.eventHandler = new EventHandler();
        this.movementStrategies = new HashMap<>();
        this.initialize();
    }

    private void initialize() {
        try {
            Buoy buoy1 = this.addBuoy(64, 800, this.context.getWidth() / 2, this.context.getHeight() - 150, new HorizontalMovement(this.context, 1));
            Buoy buoy2 = this.addBuoy(64, 4000, this.context.getWidth() / 2 - 100, this.context.getHeight() - 200, new HorizontalMovement(this.context, 2));
            Buoy buoy3 = this.addBuoy(64, 2000, this.context.getWidth() / 2 + 100, this.context.getHeight() - 250, new SinusMovement(this.context, 1));
            Satellite satellite = new Satellite(64);

            this.registerSatellite(satellite, buoy1, buoy2, buoy2);
            this.registers(buoy1, buoy2, buoy3);
            this.registers(satellite);

            // set the point of the satellite
            satellite.setPoint(new Point(this.context.getWidth() / 2, 150));
            SatelliteView satelliteView1 = new SatelliteView(new File("src/main/resources/satellite.png"));
            // set the location of the satellite view to the point of the satellite
            satelliteView1.setLocation(satellite.getPoint());
            this.space.add(satelliteView1);
            satellite.getEventHandler().registerListener(PositionChangedEvent.class, satelliteView1);
            satellite.setMovementStrategy(new HorizontalMovementSatellite(this.context, 1));

        } catch (Exception exception) {
            exception.printStackTrace();
        }
        this.addSea();
    }

    private void registerSatellite(Satellite satellite, Buoy... buoys) {
        for(Buoy buoy : buoys) {
            buoy.getEventHandler().registerListener(WaitingEvent.class, satellite);
            buoy.getEventHandler().registerListener(SyncEvent.class, satellite);
        }
    }


    private void registers(Buoy... buoys) {
        for(Mobile buoy : buoys) {
            this.eventHandler.registerListener(MovementEvent.class, buoy);
            buoy.getEventHandler().registerListener(DataCollectionCompleteEvent.class, this);
            buoy.getEventHandler().registerListener(DataCollectionEvent.class, this);
        }
    }

    private void registers(Satellite... satellites) {
        for(Mobile satellite : satellites) {
            this.eventHandler.registerListener(MovementEvent.class, satellite);
        }
    }

    private Buoy addBuoy(int width, int maxData, int x, int y, MovementStrategy movementStrategy) throws IOException {
        Buoy buoy = new Buoy(width, maxData);
        buoy.setPoint(new Point(x, y));
        this.movementStrategies.put(buoy, movementStrategy);
        BuoyView buoyView1 = new BuoyView(new File("src/main/resources/submarine.png"));
        buoyView1.setLocation(buoy.getPoint());
        this.space.add(buoyView1);
        buoy.getEventHandler().registerListener(PositionChangedEvent.class,buoyView1);
        buoy.setMovementStrategy(movementStrategy);
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
        this.space.openInWindow();

        while (true) {
            try {
                Thread.sleep(10);
                this.eventHandler.send(new MovementEvent(this));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void onDataCollectionComplete(Mobile mobile) {
        mobile.setMovementStrategy(new ToSurfaceMovement(this.context, 1));
    }

    public void onDataCollection(Mobile mobile) {
        mobile.setMovementStrategy(this.movementStrategies.get(mobile));
    }
}
