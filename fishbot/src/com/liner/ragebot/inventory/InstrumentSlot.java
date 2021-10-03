package com.liner.ragebot.inventory;

import com.liner.ragebot.Core;
import com.liner.ragebot.game.ImageSearch;
import java.awt.image.BufferedImage;

public class InstrumentSlot extends Slot {
    private boolean empty;

    public InstrumentSlot(int centerX, int centerY, BufferedImage buffer, boolean empty) {
        super(centerX, centerY, -1, buffer);
        this.empty = empty;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public static InstrumentSlot init(BufferedImage buffer) {
        int width = 75;
        int height = 75;
        int x = 148;
        int y = 506;
        BufferedImage bufferedImage = buffer.getSubimage(x, y, width, height);
        ImageSearch imageSearch = new ImageSearch(bufferedImage);
        return new InstrumentSlot(
                x + (width / 2),
                y + (height / 2),
                bufferedImage,
                imageSearch.exists(Core.Inventory.instrument_slot_empty)
        );
    }

    @Override
    public String toString() {
        return "InstrumentSlot{" +
                "centerX=" + centerX +
                ", centerY=" + centerY +
                ", index=" + index +
                ", empty=" + empty +
                '}';
    }
}
