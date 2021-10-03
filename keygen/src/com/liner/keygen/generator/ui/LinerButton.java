package com.liner.keygen.generator.ui;


import com.liner.keygen.generator.utils.Other;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

public class LinerButton extends JButton {
    private final RippleEffect rippleEffect;
    private final Font font = new Font("Yandex Sans Display Medium", Font.PLAIN, 12);
    private BufferedImage icon;
    private int rippleDuration;
    private int focusingSpeed;
    private boolean isRippleEnabled;
    private boolean isFocused;
    private Timer focusTimer;
    private Color backgroundColorSecondary;
    private Color disabledForegroundColor;
    private Color backgroundPressedColor;
    private Color foregroundPressedColor;
    private Color foregroundColor;
    private Color backgroundColor;
    private Color disabledColor;
    private Color rippleColor;
    private Color focusColor;

    public LinerButton() {
        this("");
    }

    LinerButton(String text) {
        super(text);
        super.setContentAreaFilled(false);
        this.icon = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        this.backgroundColorSecondary = new Color(44, 44, 47);
        this.disabledForegroundColor = new Color(170, 170, 170);
        this.backgroundPressedColor = new Color(29, 29, 31);
        this.foregroundPressedColor = new Color(210, 210, 210);
        this.foregroundColor = new Color(255, 255, 255);
        this.backgroundColor = new Color(47, 47, 50);
        this.disabledColor = new Color(87, 87, 87);
        this.rippleColor = new Color(177, 56, 238);
        this.focusColor = new Color(177, 56, 238);
        this.isRippleEnabled = true;
        this.rippleDuration = 1000;
        this.focusingSpeed = 1000;
        this.isFocused = false;
        this.rippleEffect = RippleEffect.applyFixedTo(this);
        this.rippleEffect.setRippleDuration(rippleDuration);
        setFocusPainted(false);
        setBorderPainted(false);
        setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        setBackground(backgroundColor);
        setForeground(Color.WHITE);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isFocused) {
                    unfocus(rippleColor);
                } else {
                    Point location = e.getPoint();
                    float distanceTopLeft = Other.getDistance(location, new Point(0, 0));
                    float distanceTopRight = Other.getDistance(location, new Point(getWidth(), 0));
                    float distanceBottomLeft = Other.getDistance(location, new Point(0, getHeight()));
                    float distanceBottomRight = Other.getDistance(location, new Point(getWidth(), getHeight()));
                    rippleEffect.addRipple(e.getPoint(),
                            (int) Math.max(getWidth(),
                                    Math.max(distanceTopLeft,
                                            Math.max(distanceTopRight,
                                                    Math.max(distanceBottomLeft, distanceBottomRight)
                                            )
                                    )
                            )
                    );
                }
            }
        });
    }

    @Override
    protected void paintBorder(Graphics graphics) {

    }

    @Override
    public Dimension getMinimumSize() {
        AffineTransform affinetransform = new AffineTransform();
        FontRenderContext frc = new FontRenderContext(affinetransform,true,true);
        int textWidth = (int)(font.getStringBounds(getText(), frc).getWidth());
        int textHeight = (int)(font.getStringBounds(getText(), frc).getHeight());
        return new Dimension(icon.getWidth()+textWidth+18, textHeight+4);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        final int width = getWidth();
        final int height = getHeight();
        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        ButtonModel model = getModel();
        if (model.isPressed()) {
            graphics2D.setColor(isRippleEnabled ? backgroundColor : backgroundPressedColor);
        } else {
            if (model.isRollover()) {
                graphics2D.setColor(backgroundColorSecondary);
            } else if (model.isEnabled()) {
                graphics2D.setColor(backgroundColor);
            } else {
                graphics2D.setColor(disabledColor);
                setForeground(disabledForegroundColor);
            }
        }
        graphics2D.fillRoundRect(0,0, width, height, 8, 8);
        graphics2D.drawImage(icon,
                4, getHeight() / 2 - icon.getHeight() / 2, null
        );
        graphics2D.setColor(foregroundColor);
        Other.drawCenteredString(
                graphics2D,
                getText(),
                new Rectangle(icon.getWidth()/2+4,0,width-icon.getWidth()/2, height),
                font
        );
        if (isRippleEnabled) {
            graphics2D.setClip(
                    new RoundRectangle2D.Float(
                            0,
                            0,
                            width,
                            height,
                            8, 8
                    ));
            graphics2D.setColor(isFocused ? focusColor : rippleColor);
            rippleEffect.paint(graphics);
        }
    }

    @Override
    public void setIcon(Icon icon) {
        if(icon instanceof ImageIcon){
            setIcon((BufferedImage) ((ImageIcon)icon).getImage());
        }
    }

    public void setIcon(BufferedImage icon) {
        this.icon = icon;
        repaint();
    }

    public void setRippleEnabled(boolean rippleEnabled) {
        this.isRippleEnabled = rippleEnabled;
        repaint();
    }

    public void setRippleDuration(int rippleDuration) {
        this.rippleDuration = rippleDuration;
        this.rippleEffect.setRippleDuration(rippleDuration);
        repaint();
    }

    public void setRippleColor(Color rippleColor) {
        this.rippleColor = rippleColor;
        repaint();
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        setBackground(backgroundColor);
        repaint();
    }

    public void setBackgroundColorSecondary(Color backgroundColorSecondary) {
        this.backgroundColorSecondary = backgroundColorSecondary;
        repaint();
    }

    public void setBackgroundPressedColor(Color backgroundPressedColor) {
        this.backgroundPressedColor = backgroundPressedColor;
        repaint();
    }

    public void setDisabledForegroundColor(Color disabledForegroundColor) {
        this.disabledForegroundColor = disabledForegroundColor;
        repaint();
    }

    public void setDisabledColor(Color disabledColor) {
        this.disabledColor = disabledColor;
        repaint();
    }

    public void setForegroundColor(Color foregroundColor) {
        this.foregroundColor = foregroundColor;
        setForeground(foregroundColor);
        repaint();
    }

    public void setForegroundPressedColor(Color foregroundPressedColor) {
        this.foregroundPressedColor = foregroundPressedColor;
        repaint();
    }

    public void setFocusColor(Color focusColor) {
        this.focusColor = focusColor;
        repaint();
    }

    public void setFocusingSpeed(int focusingSpeed) {
        this.focusingSpeed = focusingSpeed;
        repaint();
    }

    public int getFocusingSpeed() {
        return focusingSpeed;
    }

    public boolean isFocused() {
        return isFocused;
    }

    public boolean isRippleEnabled() {
        return isRippleEnabled;
    }

    public Color getRippleColor() {
        return rippleColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public Color getDisabledColor() {
        return disabledColor;
    }

    public Color getFocusColor() {
        return focusColor;
    }

    public Color getForegroundColor() {
        return foregroundColor;
    }

    public Color getBackgroundColorSecondary() {
        return backgroundColorSecondary;
    }

    public Color getBackgroundPressedColor() {
        return backgroundPressedColor;
    }

    public Color getDisabledForegroundColor() {
        return disabledForegroundColor;
    }

    public Color getForegroundPressedColor() {
        return foregroundPressedColor;
    }

    public void focus(Color focusColor) {
        this.focusColor = focusColor;
        if (!isFocused) {
            isFocused = true;
            focusTimer = new Timer();
            focusTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    rippleEffect.addRipple(new Point(getWidth() / 2, getHeight() / 2), getWidth());
                }
            }, 0, focusingSpeed);
        }
    }

    public void unfocus(Color focusColor) {
        this.focusColor = focusColor;
        if (isFocused) {
            isFocused = false;
            focusTimer.cancel();
            focusTimer = null;
        }
    }

    public void toggleFocus(Color focusColor) {
        if (isFocused) {
            unfocus(focusColor);
        } else {
            focus(focusColor);
        }
    }
}
