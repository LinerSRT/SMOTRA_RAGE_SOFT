package com.liner.ragebot.utils;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

public class StringMetrics {
    private final Font font;
    private final FontRenderContext context;

    public StringMetrics(Graphics2D g2) {
        font = g2.getFont();
        context = g2.getFontRenderContext();
    }

    public Rectangle2D getBounds(String message) {
        return font.getStringBounds(message, context);
    }

    public double getWidth(String message) {
        Rectangle2D bounds = getBounds(message);
        return bounds.getWidth();
    }

    public double getHeight(String message) {
        Rectangle2D bounds = getBounds(message);
        return bounds.getHeight();
    }
}
