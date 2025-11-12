package org.view;

import org.model.Mobile;

import java.awt.*;

public interface View {
    void move(Point point);
    void startSync(Mobile mobile);
    void endSync();
}
