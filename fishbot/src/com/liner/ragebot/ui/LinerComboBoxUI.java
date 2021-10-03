package com.liner.ragebot.ui;

import com.liner.ragebot.Core;
import com.liner.ragebot.utils.ImageUtils;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class LinerComboBoxUI extends BasicComboBoxUI {

    private final Color backgroundColor = new Color(36,37,39);
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
        button.setIcon(Core.Icon.downArrowIcon);
        button.setText("");
        button.setBackground(new Color(36,37,39));
        button.setName("ComboBox.arrowButton");
        button.setIconPosition(LinerButton.IconPosition.CENTER);
        button.setBackgroundColor(backgroundColor);
        return button;
    }

    @Override
    protected ComboPopup createPopup() {
        BasicComboPopup basicComboPopup = new BasicComboPopup(comboBox);
        basicComboPopup.setBorder(new LineBorder(new Color(255,143,11)));
        return basicComboPopup;
    }

    @Override
    public void paint(Graphics graphics, JComponent jComponent) {
        isEnabled = jComponent.isEnabled();
        Color prevColor = graphics.getColor();
        jComponent.setBackground(new Color(0,0,0,0));
        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
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