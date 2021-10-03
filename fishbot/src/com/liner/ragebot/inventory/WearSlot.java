package com.liner.ragebot.inventory;

import java.awt.image.BufferedImage;

public class WearSlot extends Slot{
    public WearSlot(int centerX, int centerY, int index, BufferedImage buffer) {
        super(centerX, centerY, index, buffer);
    }

    @Override
    public String toString() {
        return "WearSlot{" +
                "centerX=" + centerX +
                ", centerY=" + centerY +
                ", index=" + index +
                '}';
    }
}
