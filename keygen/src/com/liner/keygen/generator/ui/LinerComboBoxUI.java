package com.liner.keygen.generator.ui;


import com.liner.keygen.Core;
import com.liner.keygen.generator.utils.ImageUtils;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;

public class LinerComboBoxUI extends BasicComboBoxUI {

    private final Color backgroundColor = new Color(47, 47, 50);
    private final Color disabledColor = new Color(87, 87, 87);
    private final Color disabledForegroundColor = new Color(170, 170, 170);
    private boolean isEnabled = true;
    @Override
    protected void installDefaults() {
        super.installDefaults();
        LookAndFeel.uninstallBorder(comboBox);
        comboBox.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 2));
        UIManager.put("ComboBox.disabledForeground", disabledForegroundColor);
        UIManager.put("ComboBox.disabledBackground", disabledColor);
    }


    @Override
    protected JButton createArrowButton() {
        final LinerButton button = new LinerButton();
        button.setIcon(new ImageIcon(ImageUtils.resize(Core.Icons.ARROW_DOWN, 12, 12)));
        button.setText("");
        button.setBackground(new Color(0,0,0,0));
        button.setName("ComboBox.arrowButton");
        return button;
    }

    @Override
    protected ComboPopup createPopup() {
        BasicComboPopup basicComboPopup = new BasicComboPopup(comboBox);
        basicComboPopup.setBorder(new LineBorder(new Color(47, 47, 50)));
        return basicComboPopup;
    }

    @Override
    public void paint(Graphics graphics, JComponent jComponent) {
        isEnabled = jComponent.isEnabled();
        Color prevColor = graphics.getColor();
        jComponent.setBackground(new Color(0,0,0,0));
        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setColor(isEnabled?backgroundColor:disabledColor);
        graphics2D.fillRoundRect(0,0,jComponent.getWidth(), jComponent.getHeight(), 8,8);
        graphics2D.setColor(prevColor);
        super.paint(graphics, jComponent);
    }

    @Override
    public void paintCurrentValue(Graphics graphics, Rectangle rectangle, boolean b) {
        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setColor(isEnabled?backgroundColor:disabledColor);
        graphics2D.fillRect(4,4, rectangle.width+4, rectangle.height+4);
        super.paintCurrentValue(graphics, rectangle, b);
    }
}