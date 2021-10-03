package com.liner.ragebot.inventory;

import java.awt.image.BufferedImage;

public class RodSlot extends Slot{
    private final int rodType;
    private boolean broken;

    public RodSlot(int centerX, int centerY, int index, BufferedImage buffer, int rodType, boolean broken) {
        super(centerX, centerY, index, buffer);
        this.rodType = rodType;
        this.broken = broken;
    }

    public int getRodType() {
        return rodType;
    }

    public boolean isBroken() {
        return broken;
    }

    public void setBroken(boolean broken) {
        this.broken = broken;
    }

    @Override
    public String toString() {
        return "RodSlot{" +
                "centerX=" + centerX +
                ", centerY=" + centerY +
                ", index=" + index +
                ", rodType=" + rodType +
                ", broken=" + broken +
                '}';
    }
}
