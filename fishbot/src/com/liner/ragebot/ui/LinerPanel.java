package com.liner.ragebot.ui;

import com.liner.ragebot.utils.animator.AnimationListenerAdapter;
import com.liner.ragebot.utils.animator.AnimationObject;
import com.liner.ragebot.utils.animator.Animator;
import com.liner.ragebot.utils.animator.BounceInterpolator;

import javax.swing.*;
import java.awt.*;

public class LinerPanel extends JPanel {
    private boolean expanded = true;
    private int oldX = 0;

    public LinerPanel() {
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    }


    @Override
    protected void paintComponent(Graphics a) {
        super.paintComponent(a);
        Graphics2D graphics2D = (Graphics2D) a;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics2D.setColor(getBackground());
        graphics2D.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
    }


    public void expand(boolean inverse) {
        expanded = true;
        new Animator()
                .of(new AnimationObject("slide", new int[]{getX(), oldX}))
                .setDuration(1000)
                .setInterpolator(new BounceInterpolator())
                .setStartDelay(50)
                .setAnimationListener(new AnimationListenerAdapter() {
                    @Override
                    public void onStart() {
                        setVisible(true);
                    }
                    @Override
                    public void onAnimate(Animator animator) {
                        int slide = (int) animator.getAnimatedValueOf("slide");
                        move(inverse ? -slide : slide, getBounds().y);
                    }

                    @Override
                    public void onStop() {
                        setVisible(false);
                    }
                }).start(8);
    }
    public void toggle() {
        toggle(false);
    }
    public void toggle(boolean inverse) {
        if (expanded) {
            collapse(inverse);
        } else {
            expand(inverse);
        }
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void collapse(boolean inverse) {
        expanded = false;
        oldX = getX();
        new Animator()
                .of(new AnimationObject("slide", new int[]{getX(), getX() + getWidth() * 4}))
                .setDuration(1000)
                .setInterpolator(new BounceInterpolator())
                .setStartDelay(50)
                .setAnimationListener(new AnimationListenerAdapter() {
                    @Override
                    public void onStart() {
                        setVisible(true);
                    }

                    @Override
                    public void onAnimate(Animator animator) {
                        int slide = (int) animator.getAnimatedValueOf("slide");
                        move(inverse ? -slide : slide, getBounds().y);
                    }

                    @Override
                    public void onStop() {
                        setVisible(false);
                    }
                }).start(8);
    }
}
