package com.liner.ragebot.utils;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Other {
    public static int randomInt(int start, int end, int... exception) {
        int result = -1;
        Random rand = new Random();
        int range = end - start + 1;
        boolean equals = true;

        while(equals) {
            result = rand.nextInt(range);
            boolean differentOfAll = true;
            for(int i : exception) {
                if(result==i) {
                    differentOfAll = false;
                    break;
                }
            }
            if(differentOfAll) {
                equals = false;
            }
        }

        return result;
    }


    public static String toHumanTime(long time) {
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(time), TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)), TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
    }

    public static float getTimePercent(long startTime, long endTime){
        return ((Float.parseFloat(String.valueOf((System.currentTimeMillis()-startTime)))/Float.parseFloat(String.valueOf(endTime-startTime))) * 100f);
    }

    public static void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        g.setFont(font);
        g.drawString(text, x, y);
    }

    public static long getFixedTime(long time){
        return time - TimeUnit.HOURS.toMillis(7);
    }

    public static boolean isDark (Color color) {
        //return (color.getRed()*0.299 + color.getGreen()*0.587 + color.getBlue()*0.114) < (0.6*255);
        //return (color.getRed() + color.getGreen() + color.getBlue())/3 < (0.63*255);
        return (color.getRed()*0.2125 + color.getGreen()*0.7154 + color.getBlue()*0.0721) < (0.535*255);
        //return (color.getRed()*0.21 + color.getGreen()*0.72 + color.getBlue()*0.07) < (0.54*255);
    }

    public static Color darken(Color color) {
        int r = wrapU8B(color.getRed() - 30);
        int g = wrapU8B(color.getGreen() - 30);
        int b = wrapU8B(color.getBlue() - 30);
        return new Color(r, g, b, color.getAlpha());
    }


    public static Color brighten(Color color) {
        int r = wrapU8B(color.getRed() + 30);
        int g = wrapU8B(color.getGreen() + 30);
        int b = wrapU8B(color.getBlue() + 30);
        return new Color(r, g, b, color.getAlpha());
    }

    private static int wrapU8B(int i) {
        return Math.min(255, Math.max(0, i));
    }

    public static Color applyAlphaMask(Color color, int bitMask) {
        return new Color(color.getRGB() & 0x00FFFFFF | (bitMask & 0xFF000000), true);
    }

    public static float getDistance(Point point1, Point point2){
        return (float) Math.sqrt(Math.pow(point1.x - point2.x, 2) + Math.pow(point1.y - point2.y, 2));
    }
}
