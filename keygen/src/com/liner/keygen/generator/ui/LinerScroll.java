package com.liner.keygen.generator.ui;

import javax.swing.*;
import java.awt.*;

public class LinerScroll extends JScrollPane {

    public LinerScroll(Component component) {
        super(component);
        setBorder(BorderFactory.createEmptyBorder());
    }

    @Override
    protected void paintBorder(Graphics graphics) {

    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
    }
}
