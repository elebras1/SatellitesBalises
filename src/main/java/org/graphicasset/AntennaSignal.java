package org.graphicasset;

import nicellipse.component.NiEllipse;
import nicellipse.component.NiSpace;
import org.view.SatelliteView;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class AntennaSignal {

    Point point;
    int spacing;
    int size;

    public AntennaSignal(Point point) {
        this.point = point;
        this.size = 340;
        this.spacing = 30;
    }

    public NiEllipse createSignal() {
        NiEllipse signal = new NiEllipse();
        signal.setBounds(point.x, point.y, size, size);
        signal.setBorderColor(Color.BLACK);
        signal.setOpaque(false);
        signal.setBackground(new Color(0,0,0,0));

        NiEllipse previous = signal;
        for (int i = 1; i < 8; i++) {
            NiEllipse signal2 = new NiEllipse();
            signal2.setBounds(spacing, spacing, size - (i*(2*spacing)), size - (i*(2*spacing)));
            signal2.setBorderColor(Color.BLACK);
            signal2.setOpaque(false);
            signal2.setBackground(new Color(0,0,0,0));
            previous.add(signal2);
            previous = signal2;
        }
        return signal;
    }

    /*public static void main(String args[]) throws IOException {
        AntennaSignal antennaSignal = new AntennaSignal(new Point(-50,-50));
        NiSpace space = new NiSpace("Test Antenna Signal", new Dimension(800, 600));
        space.setBounds(0,0,800,600);

        space.openInWindow();

        SatelliteView satelliteView = new SatelliteView(new File("src/main/resources/satellite.png"));
        satelliteView.setLocation(new Point(237,237));
        space.add(satelliteView);
        space.repaint();
        satelliteView.add(antennaSignal.createSignal());
        space.repaint();

    }*/
}
