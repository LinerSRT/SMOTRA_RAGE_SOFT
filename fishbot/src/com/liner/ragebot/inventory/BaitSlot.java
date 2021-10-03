package com.liner.ragebot.inventory;

import java.awt.image.BufferedImage;

public class BaitSlot extends Slot{
    private final int baitType;
    private int baitAmount;

    public BaitSlot(
            int centerX,
            int centerY,
            int index,
            BufferedImage buffer,
            int baitType,
            int baitAmount
    ) {
        super(centerX, centerY, index, buffer);
        this.baitType = baitType;
        this.baitAmount = baitAmount;
    }

    public int getBaitType() {
        return baitType;
    }

    public int getBaitAmount() {
        return baitAmount;
    }

    public void setBaitAmount(int baitAmount) {
        this.baitAmount = baitAmount;
    }

    @Override
    public String toString() {
        return "BaitSlot{" +
                "centerX=" + centerX +
                ", centerY=" + centerY +
                ", index=" + index +
                ", baitType=" + baitType +
                ", baitAmount=" + baitAmount +
                '}';
    }
}
