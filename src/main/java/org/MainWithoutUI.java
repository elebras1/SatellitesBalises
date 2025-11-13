package org;

import org.simulation.EditorCode;
import org.simulation.SimulationContext;
import org.simulation.SimulationWithoutUI;

public class MainWithoutUI {
    public static void main(String[] args) {
        // Créer le contexte de simulation
        SimulationContext context = new SimulationContext(800, 600, 300);
        SimulationWithoutUI simulation = new SimulationWithoutUI(context);
        EditorCode editor = new EditorCode();
        Thread simulationThread = new Thread(simulation::process);
        simulationThread.setDaemon(false);
        simulationThread.start();
    }
}
