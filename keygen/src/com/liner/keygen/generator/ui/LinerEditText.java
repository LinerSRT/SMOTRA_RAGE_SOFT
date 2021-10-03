package com.liner.keygen.generator.ui;

import com.liner.keygen.generator.utils.Other;
import com.liner.keygen.generator.utils.SafePropertySetter;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.interpolators.SplineInterpolator;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class LinerEditText extends JTextField {
    private final Line line;
    private final RippleEffect rippleEffect;
    private final Color backgroundColor;
    private boolean isRippleEnabled;
    private int rippleDuration;
    private int focusingSpeed;
    private int limitChar;
    private boolean isFocused;
    private Color rippleColor;
    private Timer focusTimer;
    private Color focusColor;
    private BufferedImage icon;

    public LinerEditText() {
        this("");
    }

    public LinerEditText(String s) {
        super(s);
        this.line = new Line(this);
        this.icon = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        this.backgroundColor = new Color(47, 47, 50);
        this.rippleColor = new Color(177, 56, 238);
        this.focusColor = new Color(177, 56, 238);
        this.isRippleEnabled = true;
        this.rippleDuration = 1000;
        this.focusingSpeed = 1000;
        this.limitChar = Integer.MAX_VALUE;
        this.isFocused = false;
        this.rippleEffect = RippleEffect.applyFixedTo(this);
        this.rippleEffect.setRippleDuration(rippleDuration);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        Color foregroundColor = new Color(177, 56, 238);
        setSelectionColor(rippleColor);
        setCaretColor(rippleColor);
        Color disabledColor = new Color(87, 87, 87);
        setDisabledTextColor(disabledColor);
        setBackground(backgroundColor);
        Color textColor = new Color(255, 255, 255);
        setForeground(textColor);
        setSelectedTextColor(textColor);
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

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                unfocus(rippleColor);
            }
        });
    }

    @Override
    public JToolTip createToolTip() {
        return new LinerToolTip();
    }

    @Override
    protected void paintComponent(Graphics a) {
        Graphics2D graphics2D = (Graphics2D) a;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics2D.setColor(backgroundColor);
        graphics2D.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
        graphics2D.setColor(rippleColor);
        graphics2D.fillRoundRect((int) ((getWidth() - line.getWidth()) / 2) + 1, 1, (int) line.getWidth() - 3, getHeight() - 1, 8, 8);
        graphics2D.setColor(getBackground());
        graphics2D.fillRoundRect(0, 0, getWidth() - 1, getHeight() / 2, 8, 8);
        graphics2D.fillRect(0, 4, getWidth() - 1, getHeight() - 8);
        graphics2D.drawImage(icon, 6, line.getWidth() <= 1 ? getHeight() / 2 - icon.getHeight() / 2 : 4, null);
        graphics2D.translate(icon.getWidth() + 6, 0);
        super.paintComponent(graphics2D);
        graphics2D.translate(-icon.getWidth() - 6, 0);
    }

    @Override
    protected void paintBorder(Graphics a) {
        Graphics2D graphics2D = (Graphics2D) a;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        if (isRippleEnabled) {
            graphics2D.setClip(
                    new RoundRectangle2D.Float(
                            0,
                            0,
                            getWidth(),
                            getHeight(),
                            8, 8
                    ));
            graphics2D.setColor(isFocused ? focusColor : rippleColor);
            rippleEffect.paint(graphics2D);
        }
    }

    @Override
    protected Document createDefaultModel() {
        return new LimitDocument();
    }

    private class LimitDocument extends PlainDocument {
        @Override
        public void insertString( int offset, String  str, AttributeSet attr ) throws BadLocationException {
            if (str == null) return;
            if ((getLength() + str.length()) <= limitChar) {
                super.insertString(offset, str, attr);
            }
        }
    }

    public void setLimitChar(int limitChar) {
        this.limitChar = limitChar;
    }

    public int getLimitChar() {
        return limitChar;
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
        setCaretColor(rippleColor);
        setSelectionColor(rippleColor);
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

    @Override
    public void setText(String s) {
        super.setText(s);
        if (line != null)
            line.update();
    }

    @Override
    protected void processFocusEvent(FocusEvent focusEvent) {
        super.processFocusEvent(focusEvent);
        if (line != null)
            line.update();
    }

    @Override
    protected void processKeyEvent(KeyEvent keyEvent) {
        super.processKeyEvent(keyEvent);
        if (line != null)
            line.update();
    }

    public static class Line {
        private final SwingTimerTimingSource timer;
        private final JComponent target;
        private org.jdesktop.core.animation.timing.Animator animator;
        private SafePropertySetter.Property<Double> width;

        public Line(JComponent target) {
            this.target = target;
            this.timer = new SwingTimerTimingSource();
            timer.init();
            width = SafePropertySetter.animatableProperty(target, 0d);
        }

        public void update() {
            if (animator != null) {
                animator.stop();
            }
            animator = new org.jdesktop.core.animation.timing.Animator.Builder(timer)
                    .setDuration(200, TimeUnit.MILLISECONDS)
                    .setEndBehavior(Animator.EndBehavior.HOLD)
                    .setInterpolator(new SplineInterpolator(0.4, 0, 0.2, 1))
                    .addTarget(SafePropertySetter.getTarget(width, width.getValue(), target.isFocusOwner() ? (double) target.getWidth() + 1 : 0d))
                    .build();
            animator.start();
        }

        public double getWidth() {
            return width.getValue();
        }
    }
}
