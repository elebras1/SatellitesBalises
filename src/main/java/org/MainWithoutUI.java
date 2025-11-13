package org;

import org.simulation.EditorCode;
import org.simulation.SimulationContext;
import org.simulation.SimulationInterfaceWithoutUI;

public class MainWithoutUI {
    public static void main(String[] args) {
        // Créer le contexte de simulation
        SimulationContext context = new SimulationContext(800, 600, 300);
        SimulationInterfaceWithoutUI simulation = new SimulationInterfaceWithoutUI(context);
        EditorCode editor = new EditorCode();
        Thread simulationThread = new Thread(simulation::process);
        simulationThread.setDaemon(false);
        simulationThread.start();
    }
}
