package org.simulation;

import nicellipse.component.NiSpace;
import org.model.Buoy;
import org.model.Mobile;

import java.awt.*;

public class Simulation {
    private final SimulationContext context;
    private final NiSpace space;

    public Simulation(SimulationContext context) {
        this.context = context;
        this.space = new NiSpace("Simulation Space", new Dimension(context.getWidth(), context.getHeight()));
        this.initialize();
    }

    public void initialize() {
        Mobile buoy1 = new Buoy()
    }

    public void process() {

    }

}
