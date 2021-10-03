package com.liner.keygen.generator.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class RoundedPanel extends JPanel {
    public RoundedPanel() {
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    }


    @Override
    protected void paintComponent(Graphics a) {
        super.paintComponent(a);
        Graphics2D graphics2D = (Graphics2D) a;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics2D.setColor(getBackground());
        graphics2D.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
    }

}
