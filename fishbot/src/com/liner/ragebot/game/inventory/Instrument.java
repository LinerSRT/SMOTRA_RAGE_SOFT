package com.liner.ragebot.game.inventory;

import java.awt.image.BufferedImage;

public class Instrument {
    private final BufferedImage buffer;
    private boolean empty;
    private int metaData;
    private int centerX;
    private int centerY;

    public Instrument(BufferedImage buffer, boolean empty, int metaData, int centerX, int centerY) {
        this.buffer = buffer;
        this.empty = empty;
        this.metaData = metaData;
        this.centerX = centerX;
        this.centerY = centerY;
    }


    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public int getMetaData() {
        return metaData;
    }

    public void setMetaData(int metaData) {
        this.metaData = metaData;
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

    @Override
    public String toString() {
        return "Instrument{" +
                "empty=" + empty +
                ", metaData=" + metaData +
                ", centerX=" + centerX +
                ", centerY=" + centerY +
                '}';
    }
}
