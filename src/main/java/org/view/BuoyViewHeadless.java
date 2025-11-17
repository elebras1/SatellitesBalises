package org.view;

import nicellipse.component.NiImage;
import org.model.Mobile;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class BuoyViewHeadless extends JComponent implements View {

    int counter = 0;

    public BuoyViewHeadless(File path) throws IOException {

    }

    public BuoyViewHeadless(JComponent graphicElement) {

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
        this.setLocation(point);
        if (counter % 300 == 0) {
            System.out.println("Buoy moved to point: " + point);
        }
        counter++;
    }

    @Override
    public void startSync(Mobile mobile) {
        System.out.println("Starting sync buoy to satellite: " + mobile + " at point: " + this.getLocation());
    }

    @Override
    public void endSync() {
        System.out.println("Ending sync buoy to satellite at point: " + this.getLocation());
    }

    @Override
    public void remove() {
        System.out.println("Removing buoy to satellite at point: " + this.getLocation());
    }
}
