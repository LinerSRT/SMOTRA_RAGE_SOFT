package com.liner.ragebot.ui;


import com.liner.ragebot.utils.SafePropertySetter;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;
import org.jdesktop.core.animation.timing.interpolators.AccelerationInterpolator;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RippleEffect {
    private final List<RippleAnimation> ripples = new ArrayList<>();
    private final JComponent target;
    private final SwingTimerTimingSource timer;
    private int rippleDuration = 1000;

    private RippleEffect(final JComponent component) {
        this.target = component;
        timer = new SwingTimerTimingSource();
        timer.init();
    }

    public void setRippleDuration(int rippleDuration) {
        this.rippleDuration = rippleDuration;
    }

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        for (int i = 0; i < ripples.size(); i++) {
            RippleAnimation rippleAnimation = ripples.get(i);
            float rippleOpacity = rippleAnimation.rippleOpacity.getValue().floatValue();
            Point rippleCenter = rippleAnimation.rippleCenter;
            int rippleRadius = rippleAnimation.rippleRadius.getValue();
            Color fg = g2.getColor();
            g2.setColor(new Color(fg.getRed() / 255f, fg.getGreen() / 255f, fg.getBlue() / 255f, rippleOpacity));
            g2.setPaint(
                    new RadialGradientPaint(
                            rippleCenter,
                            Math.max(1, rippleRadius),
                            new float[]{0f, 1f},
                            new Color[]{new Color(0, 0, 0, 0), g2.getColor()}
                    ));
            g2.fillOval(rippleCenter.x - rippleRadius, rippleCenter.y - rippleRadius, 2 * rippleRadius, 2 * rippleRadius);
        }

    }

    public void addRipple(Point point, int maxRadius) {
        final RippleAnimation ripple = new RippleAnimation(point, maxRadius);
        ripples.add(ripple);
        ripple.start();
    }

    public static RippleEffect applyTo(final JComponent target) {
        final RippleEffect rippleEffect = new RippleEffect(target);
        target.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                rippleEffect.addRipple(e.getPoint(),
                        target.getWidth() * 2
                );
            }
        });
        return rippleEffect;
    }

    public static RippleEffect applyFixedTo(final JComponent target) {
        return new RippleEffect(target);
    }

    public class RippleAnimation {
        private final Point rippleCenter;
        private final int maxRadius;
        private final SafePropertySetter.Property<Integer> rippleRadius = SafePropertySetter.animatableProperty(target, 25);
        private final SafePropertySetter.Property<Double> rippleOpacity = SafePropertySetter.animatableProperty(target, 0.0);
        private RippleAnimation(Point rippleCenter, int maxRadius) {
            this.rippleCenter = rippleCenter;
            this.maxRadius = maxRadius;
        }

        void start() {
            new Animator.Builder(timer)
                    .setDuration(rippleDuration, TimeUnit.MILLISECONDS)
                    .setEndBehavior(Animator.EndBehavior.HOLD)
                    .setInterpolator(new AccelerationInterpolator(0.2, 0.19))
                    .addTarget(SafePropertySetter.getTarget(rippleRadius, 0, maxRadius / 2, maxRadius, maxRadius))
                    .addTarget(SafePropertySetter.getTarget(rippleOpacity, 0.0, 0.4, 0.3, 0.0))
                    .addTarget(new TimingTargetAdapter() {
                        @Override
                        public void end(Animator source) {
                            ripples.remove(RippleAnimation.this);
                        }
                    })
                    .build().start();
        }
    }
}
