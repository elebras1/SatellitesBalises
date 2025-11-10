package org.view;

import nicellipse.component.NiImage;

import java.io.File;
import java.io.IOException;

public class BuoyView extends NiImage implements View {
    public BuoyView(File path) throws IOException {
        super(path);
    }
}
