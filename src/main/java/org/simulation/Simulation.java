package org.simulation;

import nicellipse.component.NiRectangle;
import nicellipse.component.NiSpace;
import org.event.DataCollectionCompleteEvent;
import org.event.MovementEvent;
import org.event.SyncEvent;
import org.event.WaitingEvent;
import org.eventHandler.EventHandler;
import org.model.Buoy;
import org.model.Mobile;
import org.model.Satellite;
import org.strategy.movement.HorizontalMovement;
import org.strategy.movement.HorizontalMovementSatellite;
import org.strategy.movement.SinusMovement;
import org.strategy.movement.ToSurfaceMovement;
import org.view.BuoyView;
import org.view.SatelliteView;

import java.awt.*;
import java.io.File;

public class Simulation {
    private final SimulationContext context;
    private final NiSpace space;
    private final EventHandler eventHandler;

    public Simulation(SimulationContext context) {
        this.context = context;
        this.space = new NiSpace("Simulation Space", new Dimension(context.getWidth(), context.getHeight()));
        this.eventHandler = new EventHandler();
        this.initialize();
    }

    private void initialize() {
        try {
            Buoy buoy1 = new Buoy(64, 800);
            Buoy buoy2 = new Buoy(64, 4000);
            Buoy buoy3 = new Buoy(64, 2000);
            Satellite satellite = new Satellite(64);

            this.registerSatellite(satellite, buoy1, buoy2, buoy2);
            this.registers(buoy1, buoy2, buoy3, satellite);

            // set the point of the bouy
            buoy1.setPoint(new Point(this.context.getWidth() / 2, this.context.getHeight() - 150));
            BuoyView buoyView1 = new BuoyView(new File("src/main/resources/submarine.png"));
            // set the location of the buoy view to the point of the buoy
            buoyView1.setLocation(buoy1.getPoint());
            this.space.add(buoyView1);
            buoy1.getEventHandler().registerListener(MovementEvent.class,buoyView1);
            buoy1.setMovementStrategy(new HorizontalMovement(this.context, 1));

            buoy2.setPoint(new Point(this.context.getWidth() / 2 - 100, this.context.getHeight() - 200));
            BuoyView buoyView2 = new BuoyView(new File("src/main/resources/submarine.png"));
            buoyView2.setLocation(buoy2.getPoint());
            this.space.add(buoyView2);
            buoy2.getEventHandler().registerListener(MovementEvent.class,buoyView2);
            buoy2.setMovementStrategy(new HorizontalMovement(this.context, 2));

            buoy3.setPoint(new Point(this.context.getWidth() / 2 + 100, this.context.getHeight() - 250));
            BuoyView buoyView3 = new BuoyView(new File("src/main/resources/submarine.png"));
            buoyView3.setLocation(buoy3.getPoint());
            this.space.add(buoyView3);
            buoy3.getEventHandler().registerListener(MovementEvent.class,buoyView3);
            buoy3.setMovementStrategy(new SinusMovement(this.context, 1));

            // set the point of the satellite
            satellite.setPoint(new Point(this.context.getWidth() / 2, 150));
            SatelliteView satelliteView1 = new SatelliteView(new File("src/main/resources/satellite.png"));
            // set the location of the satellite view to the point of the satellite
            satelliteView1.setLocation(satellite.getPoint());
            this.space.add(satelliteView1);
            satellite.getEventHandler().registerListener(MovementEvent.class, satelliteView1);
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


    private void registers(Mobile... mobiles) {
        for(Mobile mobile : mobiles) {
            this.eventHandler.registerListener(MovementEvent.class, mobile);
            mobile.getEventHandler().registerListener(DataCollectionCompleteEvent.class, this);
        }
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

}
