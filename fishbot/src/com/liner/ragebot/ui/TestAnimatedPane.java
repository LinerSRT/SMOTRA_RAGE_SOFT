package com.liner.ragebot.ui;

import com.liner.ragebot.game.ImageBounds;
import com.liner.ragebot.neural.ImageView;
import com.liner.ragebot.utils.ImageUtils;
import com.liner.ragebot.utils.Pair;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TestAnimatedPane {

    public static void main(String[] args) throws IOException {
        BufferedImage buffer = ImageIO.read(new File(System.getProperty("user.dir"), "c4.png"));
        Graphics2D graphics2D = (Graphics2D) buffer.getGraphics();

        new ImageView(buffer);
    }

    public static ImageBounds getCaptchaButtonCenterCoordinates(BufferedImage buffer) {
        List<ImageBounds> coordinateList = new ArrayList<>();
        int bufferWidth = buffer.getWidth();
        int bufferHeight = buffer.getHeight();
        int buttonWidth = 226;
        int buttonHeight = 21;
        for (int y = 10; y < bufferHeight; y++) {
            if (y + buttonHeight >= bufferHeight)
                break;
            for (int x = 10; x < bufferWidth; x++) {
                if (x + buttonWidth >= bufferWidth)
                    break;
                int topLeft = buffer.getRGB(x, y);
                int topRight = buffer.getRGB(x + buttonWidth, y);
                int bottomRight = buffer.getRGB(x + buttonWidth, y + buttonHeight);
                int bottomLeft = buffer.getRGB(x, y + buttonHeight);
                if (
                        topLeft == topRight &&
                                topRight == bottomRight &&
                                bottomRight == bottomLeft && !ImageUtils.isWhite(new Color(topLeft))
                ) {
                    coordinateList.add(new ImageBounds(x, y, buttonWidth, buttonHeight));
                }
            }
        }
        coordinateList.sort(Comparator.comparingInt(ImageBounds::getY));
        return coordinateList.get(coordinateList.size()-1);
    }

}