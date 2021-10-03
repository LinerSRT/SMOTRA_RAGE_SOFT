package com.liner.ragebot.ui;

import com.liner.ragebot.utils.Other;
import com.liner.ragebot.utils.animator.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;

public class CircleProgressBar extends JComponent {
    private int value;
    private int indeterminateValue;
    private int minValue;
    private int maxValue;
    private boolean indeterminate;
    private boolean drawText;
    private Color foregroundColor;
    private final Color backgroundColor;
    private final Stroke backgroundStroke;
    private final Stroke foregroundStroke;
    private boolean invisible;

    public CircleProgressBar() {
        this.foregroundColor = new Color(177, 56, 238);
        this.backgroundColor = getBackground();
        this.backgroundStroke = new BasicStroke(2.0F, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        this.foregroundStroke = new BasicStroke(4.0F, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        this.value = 0;
        this.minValue = 0;
        this.maxValue = 100;
        this.indeterminate = false;

    }

    public void setIndeterminate(boolean indeterminate) {
        this.indeterminate = indeterminate;
        if (indeterminate) {
            animateIndeterminate();
        }
    }

    public void setDrawText(boolean drawText) {
        this.drawText = drawText;
    }

    public void setProgress(int progress, boolean animate) {
        if (animate) {
            int oldProgress = this.value;
            new Animator()
                    .of(new AnimationObject("value", new int[]{oldProgress, progress}))
                    .setInterpolator(new DecelerateInterpolator())
                    .setDuration(1000)
                    .setStartDelay(100)
                    .setAnimationListener(new AnimationListenerAdapter() {
                        @Override
                        public void onAnimate(Animator animator) {
                            CircleProgressBar.this.value = (int) animator.getAnimatedValueOf("value");
                            repaint();
                        }
                    }).start();
        } else {
            this.value = progress;
            repaint();
        }
    }

    private void animateIndeterminate() {
        new Animator()
                .of(new AnimationObject("indeterminateValue", new int[]{0, 360}))
                .setInterpolator(new LinearInterpolator())
                .setDuration(1000)
                .setStartDelay(0)
                .setAnimationListener(new AnimationListenerAdapter() {
                    @Override
                    public void onAnimate(Animator animator) {
                        indeterminateValue = (int) animator.getAnimatedValueOf("indeterminateValue");
                        repaint();
                    }

                    @Override
                    public void onStop() {
                        if (indeterminate) {
                            indeterminateValue = 0;
                            animateIndeterminate();
                        }
                    }
                }).start();
    }


    @Override
    public Dimension getMinimumSize() {
        return new Dimension(24, 24);
    }

    public void setForegroundColor(Color color) {
        this.foregroundColor = color;
        repaint();
    }

    @Override
    public void paint(Graphics a) {
        if(!invisible) {
            Graphics2D graphics2D = (Graphics2D) a;
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            graphics2D.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
            graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            fillBackground(graphics2D);
            if (!indeterminate) {
                fillValue(graphics2D, value);
            } else {
                fillIndeterminateValue(graphics2D, indeterminateValue, 25);
            }
        }
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
        repaint();
    }

    private void fillBackground(Graphics2D graphics2D) {
        final int padding = 8;
        final int diameter = Math.min(getWidth(), getHeight()) - padding;
        graphics2D.setStroke(backgroundStroke);
        graphics2D.setColor(backgroundColor);
        graphics2D.drawOval(padding / 2, padding / 2, diameter, diameter);
    }


    private void fillValue(Graphics2D graphics2D, int value) {
        if(value <= 0)
            return;
        final int padding = 8;
        final int diameter = Math.min(getWidth(), getHeight()) - padding;
        graphics2D.setStroke(new BasicStroke(1));
        graphics2D.setColor(foregroundColor);
        for (int i = 0; i < 360; i++) {
            final double angle = Math.toRadians(map(i, new double[]{0, 360}, new double[]{-90, 270}));
            float x = (float) (((padding / 2) + diameter / 2) + Math.cos(angle) * (diameter / 2));
            float y = (float) (((padding / 2) + diameter / 2) + Math.sin(angle) * (diameter / 2));
            if (i <= getAngle(value))
                graphics2D.fill(new Arc2D.Double(
                        x - 1, y - 1, 2, 2,
                        0, 360,
                        Arc2D.CHORD
                ));

        }
        if(drawText && !indeterminate){
            graphics2D.setColor(Color.WHITE);
            Other.drawCenteredString(
                    graphics2D,
                    value+"%",
                    new Rectangle(0,0, getWidth(), getHeight()),
                    new Font("Yandex Sans Text Bold", Font.PLAIN, 12)
            );
        }
    }

    private void fillIndeterminateValue(Graphics2D graphics2D, float positionAngle, int size) {
        AffineTransform old = graphics2D.getTransform();
        graphics2D.rotate(Math.toRadians(positionAngle), getWidth() / 2f, getHeight() / 2f);
        fillValue(graphics2D, size);
        graphics2D.setTransform(old);
    }

    private float getAngle(int value) {
        return (value * 360f / 100f);
    }

    public int getValue() {
        return value;
    }

    public static double map(double value, double[] from, double[] to) {
        return (to[1] - to[0]) / (from[1] - from[0]) * (value - from[0]) + to[0];
    }
    public void setValue(int value){
        setValue(value, true);
    }
    public void setValue(int value, boolean animate) {
        this.value = value;
        if(animate) {
            int oldProgress = this.value;
            new Animator()
                    .of(new AnimationObject("value", new int[]{oldProgress, value}))
                    .setInterpolator(new DecelerateInterpolator())
                    .setDuration(1000)
                    .setStartDelay(100)
                    .setAnimationListener(new AnimationListenerAdapter() {
                        @Override
                        public void onAnimate(Animator animator) {
                            CircleProgressBar.this.value = (int) animator.getAnimatedValueOf("value");
                            repaint();
                        }
                    }).start();
        } else {
            repaint();
        }
    }

    public int getIndeterminateValue() {
        return indeterminateValue;
    }

    public void setIndeterminateValue(int indeterminateValue) {
        this.indeterminateValue = indeterminateValue;
        repaint();
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
        repaint();
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        repaint();
    }

    public boolean isIndeterminate() {
        return indeterminate;
    }

    public boolean isDrawText() {
        return drawText;
    }

    public Color getForegroundColor() {
        return foregroundColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public Stroke getBackgroundStroke() {
        return backgroundStroke;
    }

    public Stroke getForegroundStroke() {
        return foregroundStroke;
    }

    public boolean isInvisible() {
        return invisible;
    }
}
