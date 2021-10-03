package com.liner.ragebot.game.inventory;

import com.liner.ragebot.Core;
import com.liner.ragebot.game.ImageSearch;
import com.liner.ragebot.jna.RageMultiplayer;
import com.liner.ragebot.neural.ImageView;
import com.liner.ragebot.utils.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class InvTest {
    public static void main(String[] args) {
        Core.encodeResourceDirectory(Core.resourceDirectory);
        RageMultiplayer rageMultiplayer = new RageMultiplayer();
        BufferedImage bufferedImage = rageMultiplayer.getBuffer();
        Graphics2D graphics2D = (Graphics2D) bufferedImage.getGraphics();
        graphics2D.setColor(Color.CYAN);
        for (int index = 0; index < 30; index++) {
            int colID = (index % 5);
            int rowID = (index / 5);
            int slotStartX = (4 + (colID * 76)) + (6 * colID);
            int slotStartY = (86 + (rowID * 76) + (6 * rowID));
            BufferedImage buffer = bufferedImage.getSubimage(slotStartX, slotStartY, ((slotStartX+76) - slotStartX), ((slotStartY+76) - slotStartY));

            graphics2D.drawRect(slotStartX, slotStartY, 76,76);
        }
        new ImageView(ImageUtils.grayScaleImage(bufferedImage));
    }
}
