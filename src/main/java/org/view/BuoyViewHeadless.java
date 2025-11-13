package org.view;

import nicellipse.component.NiImage;
import org.model.Mobile;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class BuoyViewHeadless extends NiImage implements View {

    int counter = 0;

    public BuoyViewHeadless(File path) throws IOException {
        super(path);
    }

    @Override
    public void move(Point point) {
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
}
