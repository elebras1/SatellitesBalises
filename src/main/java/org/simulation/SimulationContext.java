package org.simulation;

public class SimulationContext implements Context {
    private final int width;
    private final int height;
    private final int seaLevel;

    public SimulationContext(int width, int height, int seaLevel) {
        this.width = width;
        this.height = height;
        this.seaLevel = seaLevel;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getSeaLevel() {
        return seaLevel;
    }
}
