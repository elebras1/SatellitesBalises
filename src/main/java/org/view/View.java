package org.view;

import org.model.Mobile;

import javax.swing.*;
import java.awt.*;

public interface View {
    JComponent getGraphicElement();
    void setGraphicElement(JComponent graphicElement);
    void move(Point point);
    void startSync(Mobile mobile);
    void endSync();
    void remove();
}
