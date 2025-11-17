package org.view;

import nicellipse.component.NiImage;
import org.model.Mobile;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class SatelliteViewHeadless extends JComponent implements View {

    int counter = 0;

    public SatelliteViewHeadless(File path) throws IOException {

    }

    public SatelliteViewHeadless(JComponent graphicElement) {

    }

    @Override
    public JComponent getGraphicElement() {
        return null;
    }

    @Override
    public void setGraphicElement(JComponent graphicElement) {

    }

    @Override
    public void move(Point point) {
        if (counter % 300 == 0) {
            System.out.println("Satellite moved to: " + point);
        }
        counter++;
    }

    @Override
    public void startSync(Mobile mobile) {
        System.out.println("Starting sync satellite to buoy: " + mobile + " at point: " + this.getLocation());
    }

    @Override
    public void endSync() {
        System.out.println("Ending sync satellite to buoy at point: " + this.getLocation());
    }
}
