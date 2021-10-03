package com.liner.keygen;

import com.liner.keygen.generator.utils.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class Core {

    public static class Icons{
        public static BufferedImage ARROW_DOWN = ImageUtils.resize(load("arrow_down.png"), 16,16);
        public static BufferedImage KEY = ImageUtils.resize(load("key.png"), 16,16);
        public static BufferedImage USER = ImageUtils.resize(load("user.png"), 16,16);
        public static BufferedImage BAN = ImageUtils.resize(load("ban.png"), 16,16);
        public static BufferedImage UNBAN = ImageUtils.resize(load("unban.png"), 16,16);
        public static BufferedImage REASON = ImageUtils.resize(load("reason.png"), 16,16);
        public static BufferedImage USERS = ImageUtils.resize(load("users.png"), 16,16);
        public static BufferedImage STATISTICS = ImageUtils.resize(load("statistics.png"), 16,16);
        public static BufferedImage USERS_ONLINE = ImageUtils.resize(load("users_online.png"), 16,16);
        public static BufferedImage INFO = ImageUtils.resize(load("info.png"), 16,16);
        public static BufferedImage REFRESH = ImageUtils.resize(load("refresh.png"), 16,16);
        public static BufferedImage DAY = ImageUtils.resize(load("day.png"), 16,16);
        public static BufferedImage HOUR = ImageUtils.resize(load("hour.png"), 16,16);
        public static BufferedImage SEND = ImageUtils.resize(load("send.png"), 16,16);
        public static BufferedImage GENERATE = ImageUtils.resize(load("generate.png"), 16,16);
        public static BufferedImage ACTIVE = ImageUtils.resize(load("activate.png"), 16,16);
        public static BufferedImage ADDTIME = ImageUtils.resize(load("addtime.png"), 16,16);
        public static BufferedImage DURATION = ImageUtils.resize(load("duration.png"), 16,16);
        public static BufferedImage ONLINE = ImageUtils.resize(load("online.png"), 16,16);
        public static BufferedImage TIMEWARN = ImageUtils.resize(load("timewarn.png"), 16,16);
        public static BufferedImage GEAR = ImageUtils.resize(load("gear.png"), 16,16);
        public static BufferedImage WINDOWS = ImageUtils.resize(load("windows.png"), 16,16);




    }

    private static BufferedImage load(String name){
        try {
            return ImageIO.read(new File(System.getProperty("user.dir"), "res/"+name));
        } catch (IOException e) {
            e.printStackTrace();
            return new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        }
    }
}
