package org.view;

import nicellipse.component.NiImage;
import nicellipse.component.NiSpace;
import org.graphicasset.AntennaSignal;
import org.model.Mobile;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class SatelliteView extends JComponent implements View {

    private JComponent graphicElement;
    private JComponent syncSignal;

    // graphic element in the case of image
    public SatelliteView(File path) throws IOException {
        this.graphicElement = new NiImage(path);
        syncSignal = null;
    }

    // graphic element in the case of other components
    public SatelliteView(JComponent graphicElement) {
        this.graphicElement = graphicElement;
        syncSignal = null;
    }

    @Override
    public JComponent getGraphicElement() {
        return graphicElement;
    }

    @Override
    public void setGraphicElement(JComponent graphicElement) {
        NiSpace parent = (NiSpace) this.graphicElement.getParent();
        parent.remove(this.graphicElement);
        this.graphicElement = graphicElement;
        parent.add(this.graphicElement);
    }

    @Override
    public void move(Point point) {
        this.setLocation(point);
        this.graphicElement.setLocation(point);
        if (syncSignal != null) {
            this.syncSignal.setLocation(new Point(point.x - 137,point.y - 137));
        }
    }

    @Override
    public void startSync(Mobile mobile) {
        Point point = mobile.getPoint();
        syncSignal = new AntennaSignal(new Point(point.x - 137,point.y - 137)).createSignal();
        this.graphicElement.getParent().add(syncSignal);
        this.graphicElement.getParent().setComponentZOrder(syncSignal, 0);
        this.graphicElement.getParent().repaint();
    }

    @Override
    public void endSync() {
        if (syncSignal != null) {
            this.graphicElement.getParent().remove(syncSignal);
            this.graphicElement.getParent().repaint();
            syncSignal = null;
        }
    }
}
