package com.liner.ragebot.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DialogMouseListener extends MouseAdapter {
    private final JFrame component;
    private Point mousePoint = null;

    public DialogMouseListener(JFrame component) {
        this.component = component;
    }

    public void mouseReleased(MouseEvent mouseEvent) {
        mousePoint = null;
    }

    public void mousePressed(MouseEvent mouseEvent) {
        mousePoint = mouseEvent.getPoint();
    }

    public void mouseDragged(MouseEvent mouseEvent) {
        Point location = mouseEvent.getLocationOnScreen();
        component.setLocation(location.x - mousePoint.x, location.y - mousePoint.y);
    }
}