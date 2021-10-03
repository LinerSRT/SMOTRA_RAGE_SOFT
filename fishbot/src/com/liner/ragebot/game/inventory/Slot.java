package com.liner.ragebot.game.inventory;

import java.awt.image.BufferedImage;
import java.util.List;

public class Slot {
    private final BufferedImage buffer;
    private final BufferedImage counterBuffer;
    private final BufferedImage statusBuffer;
    private SlotType slotType;
    private boolean hasCounter;
    private int metaData;
    private int health;
    private int centerX;
    private int centerY;
    private int index;

    public Slot(BufferedImage buffer, BufferedImage counterBuffer, BufferedImage statusBuffer) {
        this.buffer = buffer;
        this.counterBuffer = counterBuffer;
        this.statusBuffer = statusBuffer;
        this.slotType = SlotType.EMPTY;
        this.hasCounter = false;
        this.metaData = -1;
        this.health = -1;
        this.centerX = 0;
        this.centerY = 0;
        this.index = -1;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public SlotType getSlotType() {
        return slotType;
    }

    public void setSlotType(SlotType slotType) {
        this.slotType = slotType;
    }

    public boolean isHasCounter() {
        return hasCounter;
    }

    public void setHasCounter(boolean hasCounter) {
        this.hasCounter = hasCounter;
    }

    public int getMetaData() {
        return metaData;
    }

    public void setMetaData(int metaData) {
        this.metaData = metaData;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getCenterX() {
        return centerX;
    }

    public void setCenterX(int centerX) {
        this.centerX = centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public void setCenterY(int centerY) {
        this.centerY = centerY;
    }

    public BufferedImage getBuffer() {
        return buffer;
    }

    public BufferedImage getCounterBuffer() {
        return counterBuffer;
    }

    public BufferedImage getStatusBuffer() {
        return statusBuffer;
    }

    @Override
    public String toString() {
        return "Slot{" +
                "slotType=" + slotType +
                ", hasCounter=" + hasCounter +
                ", metaData=" + metaData +
                ", durability=" + health +
                ", centerX=" + centerX +
                ", centerY=" + centerY +
                '}';
    }

    public static boolean isRod(Slot slot){
        return slot.getSlotType() == SlotType.ROD;
    }

    public static boolean isBrokenRod(Slot slot){
        return isRod(slot) && slot.getHealth() == 100;
    }

    public static boolean isBait(Slot slot){
        return slot.getSlotType() == SlotType.BAIT;
    }

    public static boolean isFish(Slot slot){
        return slot.getSlotType() == SlotType.FISH;
    }

}
