package com.liner.ragebot.ui;

import javax.swing.*;
import java.awt.*;

public class RoundedPanel extends JPanel {
    private int topLeft;
    private int topRight;
    private int bottomLeft;
    private int bottomRight;
    private Color headerColor;
    private Color bottomColor;

    public RoundedPanel() {
        setOpaque(false);
        this.topLeft = 8;
        this.topRight = 8;
        this.bottomLeft = 8;
        this.bottomRight = 8;
        this.headerColor = getBackground();
        this.bottomColor = getBackground();
        setBorder(BorderFactory.createEmptyBorder(Math.max(topLeft, topRight)/2, 4, Math.max(bottomLeft, bottomRight)/2, 4));
    }

    @Override
    protected void paintComponent(Graphics a) {
        Graphics2D graphics2D = (Graphics2D) a;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        final int width = getWidth();
        final int height = getHeight();
        graphics2D.setColor(headerColor);
        graphics2D.fillRoundRect(0, 0, width/2+width/4, height/2, topLeft, topLeft);
        graphics2D.fillRoundRect(width/2, 0, width/2, height/2, topRight, topRight);
        graphics2D.setColor(bottomColor);
        graphics2D.fillRoundRect(0, height/2, width/2+width/4, height/2, bottomLeft, bottomLeft);
        graphics2D.fillRoundRect(width/2, height/2, width/2, height/2, bottomRight, bottomRight);
        graphics2D.setColor(getBackground());
        int paddingTop = Math.max(topLeft, topRight)/2;
        int paddingBottom = Math.max(bottomLeft, bottomRight);
        graphics2D.fillRect(0, paddingTop, width, height-paddingBottom);
        super.paintComponent(a);
    }

    public void setTopLeft(int topLeft) {
        this.topLeft = topLeft;
        setBorder(BorderFactory.createEmptyBorder(Math.max(topLeft, topRight)/2, 4, Math.max(bottomLeft, bottomRight)/2, 4));
        repaint();
    }

    public void setTopRight(int topRight) {
        this.topRight = topRight;
        setBorder(BorderFactory.createEmptyBorder(Math.max(topLeft, topRight)/2, 4, Math.max(bottomLeft, bottomRight)/2, 4));
        repaint();
    }

    public void setBottomLeft(int bottomLeft) {
        this.bottomLeft = bottomLeft;
        setBorder(BorderFactory.createEmptyBorder(Math.max(topLeft, topRight)/2, 4, Math.max(bottomLeft, bottomRight)/2, 4));
        repaint();
    }

    public void setBottomRight(int bottomRight) {
        this.bottomRight = bottomRight;
        setBorder(BorderFactory.createEmptyBorder(Math.max(topLeft, topRight)/2, 4, Math.max(bottomLeft, bottomRight)/2, 4));
        repaint();
    }

    public void setHeaderColor(Color headerColor) {
        this.headerColor = headerColor;
        repaint();
    }

    public void setBottomColor(Color bottomColor) {
        this.bottomColor = bottomColor;
        repaint();
    }

    public int getTopLeft() {
        return topLeft;
    }

    public int getTopRight() {
        return topRight;
    }

    public int getBottomLeft() {
        return bottomLeft;
    }

    public int getBottomRight() {
        return bottomRight;
    }

    public Color getHeaderColor() {
        return headerColor;
    }

    public Color getBottomColor() {
        return bottomColor;
    }
}
