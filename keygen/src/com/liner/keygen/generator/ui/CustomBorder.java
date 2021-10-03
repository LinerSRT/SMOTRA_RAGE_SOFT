package com.liner.keygen.generator.ui;

import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class CustomBorder extends AbstractBorder {
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y,
                            int width, int height) {
        super.paintBorder(c, g, x, y, width, height);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setPaint(c.getBackground());
        Shape shape = new RoundRectangle2D.Float(0, 0, c.getWidth()-1, c.getHeight()-1,9, 9);
        g2d.draw(shape);
    }
}