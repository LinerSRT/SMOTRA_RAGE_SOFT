package com.liner.ragebot.inventory;

import java.awt.image.BufferedImage;

public class WeaponSlot extends Slot{
    public WeaponSlot(int centerX, int centerY, int index, BufferedImage buffer) {
        super(centerX, centerY, index, buffer);
    }

    @Override
    public String toString() {
        return "WeaponSlot{" +
                "centerX=" + centerX +
                ", centerY=" + centerY +
                ", index=" + index +
                '}';
    }
}
