package org.simulation;

import org.model.Buoy;
import org.model.Satellite;
import org.strategy.MovementStrategy;

import java.io.IOException;

public interface World {

    SimulationContext getContext();

    Buoy createBuoy(int width, int maxData, int x, int y, MovementStrategy movementStrategy) throws IOException;

    Satellite createSatellite(int width, int x, int y, MovementStrategy movementStrategy) throws IOException;
}
