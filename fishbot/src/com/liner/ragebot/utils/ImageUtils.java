package com.liner.ragebot.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ImageUtils {
    public static BufferedImage grayScaleImage(BufferedImage bufferedImage) {
        final int width = bufferedImage.getWidth();
        final int height = bufferedImage.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = bufferedImage.getRGB(x, y);
                int gray = (((rgb >> 16) & 0xff) + ((rgb >> 8) & 0xff) + (rgb & 0xff)) / 3;
                result.setRGB(x, y, (255 << 24) | (gray << 16) | (gray << 8) | gray);
            }
        }
        return result;
    }

    public static BufferedImage resize(BufferedImage bufferedImage, int width, int height) {
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = result.createGraphics();
        graphics2D.drawImage(bufferedImage.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
        graphics2D.dispose();
        return result;
    }

    public static BufferedImage resize(BufferedImage bufferedImage, float width, float height) {
        return resize(bufferedImage, (int) width, (int) height);
    }

    public static BufferedImage scale(BufferedImage bufferedImage, float scale) {
        return resize(bufferedImage, (int) (bufferedImage.getWidth() * scale), (int) (bufferedImage.getHeight() * scale));
    }

    public static boolean isColorPresent(BufferedImage bufferedImage, int x, int y, Color... colors) {
        return isColorPresent(bufferedImage, x, y, 25, colors);
    }

    public static boolean isColorPresent(BufferedImage bufferedImage, int x, int y, int colorShift, Color... colors) {
        boolean result = false;
        for (Color color : colors) {
            Color checkColor = new Color(bufferedImage.getRGB(x, y));
            if (color.getRed() >= (checkColor.getRed() - colorShift) && color.getRed() <= (checkColor.getRed() + colorShift)) {
                if (color.getGreen() >= (checkColor.getGreen() - colorShift) && color.getGreen() <= (checkColor.getGreen() + colorShift)) {
                    if (color.getBlue() >= (checkColor.getBlue() - colorShift) && color.getBlue() <= (checkColor.getBlue() + colorShift)) {
                        result = true;
                        break;
                    }
                }
            }
        }
        return result;
    }

    public static Color interpolateColors(int colorStart, int colorEnd, int percent) {
        return new Color(((Math.min(colorStart, colorEnd) * (100 - percent)) + (Math.max(colorStart, colorEnd) * percent)) / 100);
    }

    public static Color interpolateColors(Color colorStart, Color colorEnd, int percent) {
        return new Color(((Math.min(colorStart.getRGB(), colorEnd.getRGB()) * (100 - percent)) + (Math.max(colorStart.getRGB(), colorEnd.getRGB()) * percent)) / 100);
    }


    private static List<Color> getColumnColors(BufferedImage bufferedImage, int x, Color skipColor) {
        List<Color> colorList = new ArrayList<>();
        for (int y = 0; y < bufferedImage.getHeight(); y++) {
            Color color = new Color(bufferedImage.getRGB(x, y));
            if (!color.equals(skipColor))
                colorList.add(color);
        }
        return colorList;
    }

    private static List<Color> getRowsColors(BufferedImage bufferedImage, int y, Color skipColor) {
        List<Color> colorList = new ArrayList<>();
        for (int x = 0; x < bufferedImage.getWidth(); x++) {
            Color color = new Color(bufferedImage.getRGB(x, y));
            if (!color.equals(skipColor))
                colorList.add(color);
        }
        return colorList;
    }

    private static List<Color> getAccent(BufferedImage bufferedImage, Color skipColor) {
        List<Color> colorList = new ArrayList<>();
        for (int x = 0; x < bufferedImage.getWidth() / 10; x++)
            colorList.addAll(getColumnColors(bufferedImage, x * 10, skipColor));
        for (int y = 0; y < bufferedImage.getHeight() / 10; y++)
            colorList.addAll(getRowsColors(bufferedImage, y * 10, skipColor));
        return colorList;
    }

    public static List<Color> getAccentColors(BufferedImage bufferedImage, Color skipColor) {
        List<Pair<Integer, Color>> pairList = getDominantColors(getAccent(bufferedImage, skipColor));
        pairList.sort((p1, p2) -> Integer.compare(p2.getKey(), p1.getKey()));
        List<Color> colorList = new ArrayList<>();
        for (Pair<Integer, Color> pair : pairList)
            colorList.add(pair.getValue());
        return colorList;
    }

    private static List<Pair<Integer, Color>> getDominantColors(List<Color> colorList) {
        List<Pair<Integer, Color>> pairList = new ArrayList<>();
        for (Color color : colorList) {
            Pair<Integer, Color> pair = new Pair<>(0, color);
            if (!pair.containValue(pairList)) {
                pair.setKey(1);
                pairList.add(pair);
            } else {
                int index = pair.indexOfValue(pairList);
                Pair<Integer, Color> existPair = pairList.get(index);
                existPair.setKey(existPair.getKey() + 1);
                pairList.set(index, existPair);
            }
        }
        pairList.sort(Comparator.comparingInt(Pair::getKey));
        return pairList;
    }

    public static boolean isWhite(Color color) {
        return (0.2126 * color.getRed() + 0.7152 * color.getGreen() + 0.0722 * color.getBlue()) > 128;
    }
}

