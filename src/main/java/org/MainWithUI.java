package org;

import org.simulation.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class MainWithUI {
    public static void main(String[] args) {
        // Créer le contexte de simulation
        SimulationContext context = new SimulationContext(800, 600, 300);
        SimulationInterfaceWithUI simulation = new SimulationInterfaceWithUI(context);
        EditorCode editor = new EditorCode();
        Thread simulationThread = new Thread(simulation::process);
        simulationThread.setDaemon(false);
        simulationThread.start();
    }
}