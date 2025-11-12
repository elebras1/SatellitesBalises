package org.view;

import nicellipse.component.NiImage;
import org.graphicasset.AntennaSignal;
import org.model.Mobile;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class SatelliteView extends NiImage implements View {
    public SatelliteView(File path) throws IOException {
        super(path);
    }

    @Override
    public void move(Point point) {
        this.setLocation(point);
    }

    @Override
    public void startSync(Mobile mobile) {
        Point point = mobile.getPoint();
        this.add(new AntennaSignal(new Point(0,0)).createSignal());
        System.out.println("Sync SatelliteView started.");
    }

    @Override
    public void endSync() {
        this.removeAll();
        System.out.println("Sync SatelliteView ended.");
    }
}
