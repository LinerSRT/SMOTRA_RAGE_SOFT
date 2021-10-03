package com.liner.ragebot.inventory;

import com.liner.ragebot.Core;
import com.liner.ragebot.game.ImageSearch;
import com.liner.ragebot.utils.ImageUtils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Slot {
    public final int centerX;
    public final int centerY;
    public final int index;
    public final BufferedImage buffer;

    public Slot(int centerX, int centerY, int index, BufferedImage buffer) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.index = index;
        this.buffer = buffer;
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public int getIndex() {
        return index;
    }

    public BufferedImage getBuffer() {
        return buffer;
    }

    public static Slot init(BufferedImage screen, int index) {
        int width = 110;
        int height = 105;
        int margin = 5;
        int x = 415 + ((index % 6) * (width + margin * 2) + margin);
        int y = 79 + ((index / 6) * (height + margin * 2) + margin);
        BufferedImage buffer = screen.getSubimage(x, y, width, height);
        if (
                ImageUtils.isColorPresent(buffer, 1, buffer.getHeight() - 1,
                        new Color(55, 72, 233)
                )) {
            return new FishSlot(
                    x + (width / 2),
                    y + (height / 2),
                    index,
                    buffer,
                    0,
                    0
                    );
        } else if (
                ImageUtils.isColorPresent(buffer, 1, buffer.getHeight() - 1,
                        new Color(153, 112, 63)
                )
        ) {
            int baitType = - 1;
            ImageSearch baitSearch = new ImageSearch(buffer);
            for (int i = 0; i < Core.Bait.baits.length; i++) {
                if(baitSearch.exists(Core.Bait.baits[i])){
                    baitType = i;
                    break;
                }
            }
            return new BaitSlot(
                    x + (width / 2),
                    y + (height / 2),
                    index,
                    buffer,
                    baitType,
                    0
            );
        } else if (
                ImageUtils.isColorPresent(buffer, 1, buffer.getHeight() - 1,
                        new Color(93, 142, 207)
                )) {
            int rodType = -1;
            ImageSearch rodSearch = new ImageSearch(buffer);
            for (int i = 0; i < Core.Rod.rods.length; i++) {
                if(rodSearch.exists(Core.Rod.rods[i])){
                    rodType = i;
                    break;
                }
            }
            if(rodType != -1){
                return new RodSlot(
                        x + (width / 2),
                        y + (height / 2),
                        index,
                        buffer,
                        rodType,
                        ImageUtils.isColorPresent(buffer, 8, 80, new Color(224, 76, 71))
                );
            } else {
                return new WearSlot(
                        x + (width / 2),
                        y + (height / 2),
                        index,
                        buffer);
            }
        } else if (
                ImageUtils.isColorPresent(buffer, 1, buffer.getHeight() - 1,
                        new Color(153, 57, 57)
                )) {
            return new WearSlot(
                    x + (width / 2),
                    y + (height / 2),
                    index,
                    buffer);
        } else {
            return new EmptySlot(
                    x + (width / 2),
                    y + (height / 2),
                    index,
                    buffer);
        }
    }
}
