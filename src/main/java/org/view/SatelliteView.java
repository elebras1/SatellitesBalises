package org.view;

import nicellipse.component.NiImage;

import java.io.File;
import java.io.IOException;

public class SatelliteView extends NiImage implements View {
    public SatelliteView(File path) throws IOException {
        super(path);
    }
}
