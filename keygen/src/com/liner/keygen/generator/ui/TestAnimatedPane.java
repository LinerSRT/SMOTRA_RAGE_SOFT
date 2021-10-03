package com.liner.keygen.generator.ui;



import com.liner.keygen.generator.utils.animator.AccelerateInterpolator;
import com.liner.keygen.generator.utils.animator.AnimationListenerAdapter;
import com.liner.keygen.generator.utils.animator.AnimationObject;
import com.liner.keygen.generator.utils.animator.Animator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TestAnimatedPane {
    Frame frame = new Frame("Sliding Control");
    JPanel left = new JPanel();

    public static void main(String[] args) {
        TestAnimatedPane application = new TestAnimatedPane();
        application.movePanels(100);
    }

    public TestAnimatedPane() {
        left.repaint();
        frame.setSize(600, 200);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        frame.setResizable(false);
        frame.setVisible(true);
        initializePanels();
    }

    private void initializePanels() {
        frame.setLayout(null);
        frame.add(left);
        left.move(left.getBounds().y-left.getBounds().width, left.getBounds().y);
        left.setLayout(null);
        left.setBackground(new Color(1f, 0f, 0f));
        left.setDoubleBuffered(true);
        left.setBounds(10, 10, 50, 100);
    }

    public void movePanels(int value) {
        new Animator()
                .of(new AnimationObject("slide", new int[]{left.getBounds().x, left.getBounds().x + value}))
                .setDuration(500)
                .setInterpolator(new AccelerateInterpolator())
                .setStartDelay(1000)
                .setAnimationListener(new AnimationListenerAdapter() {
                    @Override
                    public void onAnimate(Animator animator) {
                        int slide = (int) animator.getAnimatedValueOf("slide");
//                        left.setBounds(
//                                slide,
//                                left.getBounds().y,
//                                left.getBounds().width,
//                                left.getBounds().height
//                        );
                        left.move(slide, left.getBounds().y);
                        left.repaint();
                    }
                }).start(8);
    }
}