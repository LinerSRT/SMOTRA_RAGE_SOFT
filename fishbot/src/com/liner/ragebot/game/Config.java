package com.liner.ragebot.game;

import com.liner.ragebot.jna.JNAUtils;
import com.sun.jna.platform.win32.WinDef;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static com.liner.ragebot.utils.ImageUtils.isColorPresent;

public class Config {
    public static class HD {
        public static final List<Pixel> baitSelect = new ArrayList<Pixel>() {
            {
                add(new Pixel(750, 310, new Color(255, 255, 255)));
                add(new Pixel(820, 310, new Color(255, 255, 255)));
                add(new Pixel(985, 297, new Color(242, 48, 48)));
            }
        };
        public static final List<Pixel> throwRod = new ArrayList<Pixel>() {
            {
                add(new Pixel(977, 290, new Color(242, 48, 48)));
                add(new Pixel(537, 316, new Color(255, 255, 255)));
                add(new Pixel(694, 316, new Color(255, 255, 255)));
                add(new Pixel(500, 385,
                        new Color(109, 216, 134),
                        new Color(137, 223, 158))
                );
            }
        };
        public static final List<Pixel> waitFish = new ArrayList<Pixel>() {
            {
                add(new Pixel(1245, 534, new Color(186, 186, 186)));
                add(new Pixel(1245, 568, new Color(186, 186, 186)));
                add(new Pixel(1245, 605, new Color(186, 186, 186)));
                add(new Pixel(1245, 677, new Color(186, 186, 186)));
                add(new Pixel(1150, 559, new Color(69, 69, 69)));
            }
        };
        public static final List<Pixel> pickFish = new ArrayList<Pixel>() {
            {
                add(new Pixel(672, 159, new Color(114, 204, 114)));
                add(new Pixel(779, 159, new Color(114, 204, 114)));
                add(new Pixel(642, 160, new Color(240, 240, 240)));
            }
        };
        public static final List<Pixel> pickFishNoE = new ArrayList<Pixel>() {
            {
                add(new Pixel(934, 315, new Color(255, 255, 255)));
                add(new Pixel(934, 315, new Color(255, 255, 255)));
                add(new Pixel(810, 315, new Color(255, 255, 255)));
            }
        };
        public static final List<Pixel> pickFishNoEFix = new ArrayList<Pixel>() {
            {
                add(new Pixel(932, 315, new Color(255, 255, 255)));
                add(new Pixel(932, 315, new Color(255, 255, 255)));
                add(new Pixel(810, 315, new Color(255, 255, 255)));
            }
        };

        public static final List<Pixel> pickFishQTE = new ArrayList<Pixel>() {
            {
                add(new Pixel(934, 315, new Color(255, 255, 255)));
                add(new Pixel(934, 315, new Color(255, 255, 255)));
                add(new Pixel(810, 315, new Color(255, 255, 255)));
            }
        };
        public static final List<Pixel> pickFishQTEFix = new ArrayList<Pixel>() {
            {
                add(new Pixel(932, 315, new Color(255, 255, 255)));
                add(new Pixel(932, 315, new Color(255, 255, 255)));
                add(new Pixel(810, 315, new Color(255, 255, 255)));
            }
        };

        public static final List<Pixel> pickFishQTENoE = new ArrayList<Pixel>() {
            {
                add(new Pixel(850, 395, new Color(255, 255, 255)));
            }
        };

        public static final List<Pixel> pickFishQ = new ArrayList<Pixel>() {
            {
                add(
                        new Pixel(732, 395,
                        new Color(127, 177, 115),
                        new Color(126, 183, 130)));
            }
        };

        public static final List<Pixel> finishFish = new ArrayList<Pixel>() {
            {
                add(new Pixel(580, 183, new Color(240, 240, 240)));
                add(new Pixel(619, 183, new Color(114, 204, 114)));
                add(new Pixel(741, 174, new Color(114, 201, 114)));
                add(new Pixel(780, 162, new Color(240, 240, 240)));
            }
        };

        public static final List<Pixel> failFish = new ArrayList<Pixel>() {
            {
                add(new Pixel(593, 175, new Color(224, 50, 50)));
                add(new Pixel(638, 175, new Color(224, 50, 50)));
                add(new Pixel(717, 175, new Color(224, 50, 50)));
            }
        };

        public static final List<Pixel> inventory = new ArrayList<Pixel>() {
            {
                add(new Pixel(1100, 42, new Color(217, 219, 221)));
                add(new Pixel(1100, 675, new Color(67, 74, 84)));
                add(new Pixel(185, 130, new Color(184, 184, 184)));
                add(new Pixel(265, 130, new Color(184, 184, 184)));
                add(new Pixel(342, 130, new Color(184, 184, 184)));
            }
        };
    }

    public static boolean isCoordinatesPresent(BufferedImage bufferedImage, List<Pixel> pixelList) {
        boolean present = true;
        for (Pixel coordinate : pixelList) {
            if (!isColorPresent(bufferedImage, coordinate.x, coordinate.y, coordinate.color)) {
                present = false;
                break;
            }
        }
        return present;
    }
    public static boolean isCoordinatesPresent(BufferedImage bufferedImage, List<Pixel> pixelList, int colorShift) {
        boolean present = true;
        for (Pixel coordinate : pixelList) {
            if (!isColorPresent(bufferedImage, coordinate.x, coordinate.y, colorShift, coordinate.color)) {
                present = false;
                break;
            }
        }
        return present;
    }
}
