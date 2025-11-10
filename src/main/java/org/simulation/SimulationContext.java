package org.simulation;

public class SimulationContext {
    private int width;
    private int height;
    private int seaLevel;

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
