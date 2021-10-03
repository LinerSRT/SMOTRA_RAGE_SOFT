package com.liner.ragebot.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;

public class RenderComboBox extends BasicComboBoxRenderer {
    private final Color backgroundColor = new Color(36,37,39);
    private final Color backgroundColorSecondary = new Color(29, 30, 31);
    private final Color backgroundPressedColor = new Color(29, 30, 31);
    private final Color foregroundColor = new Color(255, 255, 255);
    private final Color foregroundPressedColor = new Color(210, 210, 210);

    public RenderComboBox() {
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (isSelected) {
            setBackground(backgroundPressedColor);
            setForeground(foregroundPressedColor);
        } else {
            setBackground(backgroundColor);
            setForeground(foregroundColor);
        }
        setFont(list.getFont());
        if (value == null) {
            setText("");
        } else {
            setText(value.toString());
        }
        return this;
    }

    @Override
    public JToolTip createToolTip() {
        return new LinerToolTip();
    }
}