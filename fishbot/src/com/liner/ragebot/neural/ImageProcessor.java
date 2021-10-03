package com.liner.ragebot.neural;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ImageProcessor {
    public static BufferedImage clearImage(BufferedImage original, int resultWidth, int resultHeight, Color skipColor) {
        BufferedImage copy = copyImage(original);
        removeLines(copy, getDominantColors(getAccentColors(copy, skipColor)), 0.25f);
        correctNoise(copy, skipColor);
        try {
            return binarize(resize(autoCrop(copy), resultWidth, resultHeight), skipColor);
        }catch (IllegalArgumentException e){
            return resize(original, resultWidth, resultHeight);
        }
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

    private static List<Color> getAccentColors(BufferedImage bufferedImage, Color skipColor) {
        List<Color> colorList = new ArrayList<>();
        for (int x = 0; x < bufferedImage.getWidth() / 10; x++)
            colorList.addAll(getColumnColors(bufferedImage, x * 10, skipColor));
        for (int y = 0; y < bufferedImage.getHeight() / 10; y++)
            colorList.addAll(getRowsColors(bufferedImage, y * 10, skipColor));
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

    private static void removeLines(BufferedImage bufferedImage, List<Pair<Integer, Color>> dominantColors, float existPercent) {
        int totalColors = 0;
        for (Pair<Integer, Color> color : dominantColors)
            totalColors += color.getKey();
        for (Pair<Integer, Color> color : dominantColors)
            if ((float) color.getKey() / totalColors < existPercent) {
                for (int x = 0; x < bufferedImage.getWidth(); x++) {
                    for (int y = 0; y < bufferedImage.getHeight(); y++) {
                        if (new Color(bufferedImage.getRGB(x, y)).equals(color.getValue())) {
                            bufferedImage.setRGB(x, y, Color.WHITE.getRGB());
                        }
                    }
                }
            }
    }
    private static void correctNoise(BufferedImage bufferedImage, Color triggerColor){
        for (int x = 10; x < bufferedImage.getWidth() - 10; x++) {
            for (int y = 10; y < bufferedImage.getHeight() - 10; y++) {
                if (new Color(bufferedImage.getRGB(x, y)).equals(triggerColor)) {
                    Color upColor = new Color(bufferedImage.getRGB(x, y - 1));
                    Color downColor = new Color(bufferedImage.getRGB(x, y + 1));
                    if (upColor.equals(downColor))
                        bufferedImage.setRGB(x, y, upColor.getRGB());
                }
            }
        }
    }

    public static BufferedImage binarize(BufferedImage bufferedImage, Color skipColor){
        for (int x = 0; x < bufferedImage.getWidth(); x++) {
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                if (!new Color(bufferedImage.getRGB(x, y)).equals(skipColor))
                    bufferedImage.setRGB(x, y, Color.BLACK.getRGB());
                else
                    bufferedImage.setRGB(x,y,Color.WHITE.getRGB());
            }
        }
        return bufferedImage;
    }

    public static BufferedImage autoCrop(BufferedImage source) {
        int width = source.getWidth();
        int height = source.getHeight();
        int topY = Integer.MAX_VALUE, topX = Integer.MAX_VALUE;
        int bottomY = -1, bottomX = -1;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (!new Color(source.getRGB(x, y)).equals(new Color(source.getRGB(0, 0)))) {
                    if (x < topX) topX = x;
                    if (y < topY) topY = y;
                    if (x > bottomX) bottomX = x;
                    if (y > bottomY) bottomY = y;
                }
            }
        }
        BufferedImage destination = new BufferedImage((bottomX - topX),
                (bottomY - topY), BufferedImage.TYPE_INT_ARGB);
        destination.getGraphics().drawImage(source, 0, 0,
                destination.getWidth(), destination.getHeight(),
                topX, topY, bottomX, bottomY, null);
        return destination;
    }

    private static BufferedImage copyImage(BufferedImage bufferedImage) {
        BufferedImage copy = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < bufferedImage.getWidth(); x++) {
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                copy.setRGB(x, y, bufferedImage.getRGB(x, y) == 0 ? Color.WHITE.getRGB() : bufferedImage.getRGB(x, y));
            }
        }
        return copy;
    }

    private static BufferedImage resize(BufferedImage original, int width, int height) {
        Image tmp = original.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return bufferedImage;
    }
}
