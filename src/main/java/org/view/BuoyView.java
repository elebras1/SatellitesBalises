package org.view;

import nicellipse.component.NiImage;
import org.graphicasset.AntennaSignal;
import org.model.Mobile;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class BuoyView extends NiImage implements View {

    private Component ellipse;

    public BuoyView(File path) throws IOException {
        super(path);
        this.ellipse = null;
    }

    @Override
    public void move(Point point) {
        this.setLocation(point);
        if (ellipse != null) {
            this.ellipse.setLocation(new Point(point.x - 137,point.y - 137));
        }
    }

    @Override
    public void startSync(Mobile mobile) {
        Point point = mobile.getPoint();
        ellipse = this.add(new AntennaSignal(new Point(point.x - 137,point.y - 137)).createSignal());
        this.getParent().add(ellipse);
        this.getParent().repaint();
        System.out.println("Sync BuoyView started.");
    }

    @Override
    public void endSync() {
        if (ellipse != null) {
            this.getParent().remove(ellipse);
            this.getParent().repaint();
            ellipse = null;
        }
        System.out.println("Sync BuoyView ended.");
    }
}
