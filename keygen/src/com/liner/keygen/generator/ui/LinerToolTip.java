package com.liner.keygen.generator.ui;

import javax.swing.*;
import java.awt.*;

public class LinerToolTip extends JToolTip {

    public LinerToolTip() {
        setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        setBackground(new Color(0,0,0,0));
        setForeground(Color.WHITE);
        setOpaque(false);
    }

    @Override
    public void paint(Graphics a) {
        Graphics2D graphics2D = (Graphics2D) a;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics2D.setColor(new Color(47, 47, 50));
        graphics2D.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(a);
    }
}
