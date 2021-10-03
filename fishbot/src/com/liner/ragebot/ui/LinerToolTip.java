package com.liner.ragebot.ui;

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

    }
}
