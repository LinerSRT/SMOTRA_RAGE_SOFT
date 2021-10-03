package com.liner.ragebot.game;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ImageSearch {
    private BufferedImage source;

    public ImageSearch(BufferedImage source) {
        this.source = source;
    }

    public void setSource(BufferedImage source) {
        this.source = source;
    }

    public boolean exists(BufferedImage target){
        return find(target) != null;
    }

    public boolean existsInArea(ImageBounds bounds, BufferedImage target){
        return findInArea(bounds, target) != null;
    }

    public ImageBounds findInArea(ImageBounds bounds, BufferedImage target){
        if(target.getWidth() > bounds.width || target.getHeight() > bounds.height)
            return null;
        int[][] sourceData = getPixelData(source.getSubimage(bounds.x, bounds.y, bounds.width, bounds.height));
        int[][] targetData = getPixelData(target);
        int firstElem = targetData[0][0];
        for (int x = 0; x < sourceData.length - targetData.length + 1; x++) {
            scan:
            for (int y = 0; y < sourceData[0].length - targetData[0].length; y++) {
                if (!isSimilarColor(new Color(sourceData[x][y]), new Color(firstElem))) continue;
                for (int ii = 0; ii < targetData.length; ii++)
                    for (int jj = 0; jj < targetData[0].length; jj++) {
                        Color sourceColor = new Color(sourceData[x + ii][y + jj]);
                        Color checkColor = new Color(targetData[ii][jj]);
                        if (!isSimilarColor(sourceColor, checkColor) && ((targetData[ii][jj] >> 24) & 0xff) != 0)
                            continue scan;
                    }
                return new ImageBounds(x, y, target.getWidth(), target.getHeight());
            }
        }
        return null;
    }

    public ImageBounds find(BufferedImage target, int startX, int startY, int endX, int endY) {
        int[][] sourceData = getPixelData(source.getSubimage(startX, startY, endX-startX, endY-startY));
        int[][] targetData = getPixelData(target);
        int firstElem = targetData[0][0];
        for (int x = 0; x < sourceData.length - targetData.length + 1; x++) {
            scan:
            for (int y = 0; y < sourceData[0].length - targetData[0].length; y++) {
                if (!isSimilarColor(new Color(sourceData[x][y]), new Color(firstElem))) continue;
                for (int ii = 0; ii < targetData.length; ii++)
                    for (int jj = 0; jj < targetData[0].length; jj++) {
                        Color sourceColor = new Color(sourceData[x + ii][y + jj]);
                        Color checkColor = new Color(targetData[ii][jj]);
                        if (!isSimilarColor(sourceColor, checkColor) && ((targetData[ii][jj] >> 24) & 0xff) != 0)
                            continue scan;
                    }
                return new ImageBounds(x, y, target.getWidth(), target.getHeight());
            }
        }
        return null;
    }

    public ImageBounds find(BufferedImage target) {
        int[][] sourceData = getPixelData(source);
        int[][] targetData = getPixelData(target);
        int firstElem = targetData[0][0];
        for (int x = 0; x < sourceData.length - targetData.length + 1; x++) {
            scan:
            for (int y = 0; y < sourceData[0].length - targetData[0].length; y++) {
                if (!isSimilarColor(new Color(sourceData[x][y]), new Color(firstElem))) continue;
                for (int ii = 0; ii < targetData.length; ii++)
                    for (int jj = 0; jj < targetData[0].length; jj++) {
                        Color sourceColor = new Color(sourceData[x + ii][y + jj]);
                        Color checkColor = new Color(targetData[ii][jj]);
                        if (!isSimilarColor(sourceColor, checkColor) && ((targetData[ii][jj] >> 24) & 0xff) != 0)
                            continue scan;
                    }
                return new ImageBounds(x, y, target.getWidth(), target.getHeight());
            }
        }
        return null;
    }

    public List<ImageBounds> findAll(BufferedImage target) {
        List<ImageBounds> boundsList = new ArrayList<>();
        int[][] sourceData = getPixelData(source);
        int[][] targetData = getPixelData(target);
        int firstElem = targetData[0][0];
        for (int x = 0; x < sourceData.length - targetData.length + 1; x++) {
            scan:
            for (int y = 0; y < sourceData[0].length - targetData[0].length; y++) {
                if (!isSimilarColor(new Color(sourceData[x][y]), new Color(firstElem))) continue;
                for (int ii = 0; ii < targetData.length; ii++)
                    for (int jj = 0; jj < targetData[0].length; jj++) {
                        Color sourceColor = new Color(sourceData[x + ii][y + jj]);
                        Color checkColor = new Color(targetData[ii][jj]);
                        if (!isSimilarColor(sourceColor, checkColor) && ((targetData[ii][jj] >> 24) & 0xff) != 0)
                            continue scan;
                    }
                boundsList.add(new ImageBounds(x, y, target.getWidth(), target.getHeight()));
            }
        }
        return boundsList;
    }

    private int[][] getPixelData(BufferedImage bufferedImage) {
        int[][] result = new int[bufferedImage.getWidth()][bufferedImage.getHeight()];
        for (int x = 0; x < bufferedImage.getWidth(); x++) {
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                result[x][y] = bufferedImage.getRGB(x, y);
            }
        }
        return result;
    }

    private boolean isSimilarColor(Color sourceColor, Color checkColor) {
        int colorShift = 25;
        if (checkColor.getAlpha() == 0)
            return true;
        if (sourceColor.getRed() >= (checkColor.getRed() - colorShift) && sourceColor.getRed() <= (checkColor.getRed() + colorShift)) {
            if (sourceColor.getGreen() >= (checkColor.getGreen() - colorShift) && sourceColor.getGreen() <= (checkColor.getGreen() + colorShift)) {
                return sourceColor.getBlue() >= (checkColor.getBlue() - colorShift) && sourceColor.getBlue() <= (checkColor.getBlue() + colorShift);
            }
        }
        return false;
    }

    public BufferedImage getSource() {
        return source;
    }
}
