package org.view;

import nicellipse.component.NiImage;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class BuoyView extends NiImage implements View {
    public BuoyView(File path) throws IOException {
        super(path);
    }

    @Override
    public void move(Point point) {
        this.setLocation(point);
    }
}
