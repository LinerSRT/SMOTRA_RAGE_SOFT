package com.liner.keygen.generator.utils;

import java.awt.*;
import java.awt.image.BufferedImage;

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
        graphics2D.drawImage(bufferedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
        graphics2D.dispose();
        return result;
    }

    public static BufferedImage resize(BufferedImage bufferedImage, float width, float height) {
        return resize(bufferedImage, (int)width, (int)height);
    }

    public static BufferedImage scale(BufferedImage bufferedImage, float scale) {
        return resize(bufferedImage, (int)(bufferedImage.getWidth()*scale), (int)(bufferedImage.getHeight()*scale));
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

    public static Color interpolateColors(int colorStart, int colorEnd, int percent){
        return new Color(((Math.min(colorStart, colorEnd)*(100-percent)) + (Math.max(colorStart, colorEnd)*percent)) / 100);
    }
    public static Color interpolateColors(Color colorStart, Color colorEnd, int percent){
        return new Color(((Math.min(colorStart.getRGB(), colorEnd.getRGB())*(100-percent)) + (Math.max(colorStart.getRGB(), colorEnd.getRGB())*percent)) / 100);
    }
}

