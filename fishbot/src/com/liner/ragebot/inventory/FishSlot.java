package com.liner.ragebot.inventory;

import java.awt.image.BufferedImage;

public class FishSlot extends Slot{
    private final int fishType;
    private int fishAmount;

    public FishSlot(
            int centerX,
            int centerY,
            int index,
            BufferedImage buffer,
            int fishType,
            int fishAmount
    ) {
        super(centerX, centerY, index, buffer);
        this.fishType = fishType;
        this.fishAmount = fishAmount;
    }

    public int getFishType() {
        return fishType;
    }

    public int getFishAmount() {
        return fishAmount;
    }

    public void setFishAmount(int fishAmount) {
        this.fishAmount = fishAmount;
    }

    @Override
    public String toString() {
        return "FishSlot{" +
                "centerX=" + centerX +
                ", centerY=" + centerY +
                ", index=" + index +
                ", fishType=" + fishType +
                ", fishAmount=" + fishAmount +
                '}';
    }
}
