package org;

import org.simulation.Simulation;
import org.simulation.SimulationContext;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        SimulationContext simulationContext = new SimulationContext(800, 600, 300);
        Simulation simulation = new Simulation(simulationContext);
    }
}