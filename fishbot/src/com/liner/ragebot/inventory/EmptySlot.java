package com.liner.ragebot.inventory;

import java.awt.image.BufferedImage;

public class EmptySlot extends Slot{
    public EmptySlot(int centerX, int centerY, int index, BufferedImage buffer) {
        super(centerX, centerY, index, buffer);
    }
    @Override
    public String toString() {
        return "EmptySlot{" +
                "centerX=" + centerX +
                ", centerY=" + centerY +
                ", index=" + index +
                '}';
    }
}
