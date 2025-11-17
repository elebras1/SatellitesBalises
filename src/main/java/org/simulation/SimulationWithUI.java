package org.simulation;

import nicellipse.component.NiRectangle;
import nicellipse.component.NiSpace;
import org.event.*;
import org.model.Buoy;
import org.model.Satellite;
import org.simulation.program.BuoyProgram;
import org.simulation.program.Program;
import org.simulation.program.SatelliteProgram;
import org.strategy.MovementStrategy;
import org.view.BuoyView;
import org.view.SatelliteView;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimulationWithUI implements World {
    private final SimulationContext context;
    private final NiSpace space;
    private final List<Program> programs;


    public SimulationWithUI(SimulationContext context) {
        this.context = context;
        this.space = new NiSpace("Simulation Space", new Dimension(context.getWidth(), context.getHeight()));
        this.programs = new ArrayList<>();
        this.initialize();
    }

    @Override
    public SimulationContext getContext() {
        return this.context;
    }

    @Override
    public Buoy createBuoy(int width, int maxData, int x, int y, MovementStrategy movementStrategy) throws IOException {
        Buoy buoy = this.addBuoy(width, maxData, x, y, movementStrategy);
        BuoyProgram program = new BuoyProgram(buoy, this.context);
        this.programs.add(program);
        this.registerNewBuoy(buoy, program);
        return buoy;
    }

    @Override
    public Satellite createSatellite(int width, int x, int y, MovementStrategy movementStrategy) throws IOException {
        Satellite satellite = this.addSatellite(width, x, y, movementStrategy);
        SatelliteProgram program = new SatelliteProgram(satellite);
        this.programs.add(program);
        this.registerNewSatellite(satellite, program);
        return satellite;
    }

    private void initialize() {
        this.addSea();
    }

    private void registerNewBuoy(Buoy buoy, BuoyProgram buoyProgram) {
        for (Program program : this.programs) {
            program.onNewBuoy(buoy);
        }
        buoyProgram.getEventHandler().registerListener(MovementEvent.class, buoy);
        buoy.getEventHandler().registerListener(DataCollectionCompleteEvent.class, buoyProgram);
        buoy.getEventHandler().registerListener(DataCollectionEvent.class, buoyProgram);
        buoy.getEventHandler().registerListener(DiveEvent.class, buoyProgram);
    }

    private void registerNewSatellite(Satellite satellite, SatelliteProgram satelliteProgram) {
        for (Program program : this.programs) {
            program.onNewSatellite(satellite);
        }
        satelliteProgram.getEventHandler().registerListener(MovementEvent.class, satellite);
    }

    private Satellite addSatellite(int width, int x, int y, MovementStrategy movementStrategy) throws IOException {
        Satellite satellite = new Satellite(width);
        satellite.setPoint(new Point(x, y));
        SatelliteView satelliteView1 = new SatelliteView(new File("src/main/resources/satellite.png"));
        satelliteView1.setLocation(satellite.getPoint());
        this.space.add(satelliteView1);
        satellite.getEventHandler().registerListener(PositionChangedEvent.class, satelliteView1);
        satellite.getEventHandler().registerListener(StartSyncViewEvent.class, satelliteView1);
        satellite.getEventHandler().registerListener(EndSyncViewEvent.class, satelliteView1);
        satellite.setMovementStrategy(movementStrategy);
        return satellite;
    }

    private Buoy addBuoy(int width, int maxData, int x, int y, MovementStrategy movementStrategy) throws IOException {
        Buoy buoy = new Buoy(width, maxData,new Point(x, y));
        BuoyView buoyView1 = new BuoyView(new File("src/main/resources/submarine.png"));
        buoyView1.setLocation(buoy.getPoint());
        this.space.add(buoyView1);
        this.space.setComponentZOrder(buoyView1, 0);
        buoy.getEventHandler().registerListener(PositionChangedEvent.class, buoyView1);
        buoy.getEventHandler().registerListener(StartSyncViewEvent.class, buoyView1);
        buoy.getEventHandler().registerListener(EndSyncViewEvent.class, buoyView1);
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
                for(Program program : this.programs) {
                    program.process();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
