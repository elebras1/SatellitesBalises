package org.simulation;

public class SimulationExecutor implements Executor {

    @Override
    public void execute(String code) {
        SimulationContext context = new SimulationContext(800, 600, 300);
        Simulation simulation = new Simulation(context);
        simulation.process();
    }
}
