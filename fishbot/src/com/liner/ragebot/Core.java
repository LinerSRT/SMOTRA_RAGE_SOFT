package com.liner.ragebot;

import com.liner.ragebot.utils.Files;
import com.liner.ragebot.utils.ImageUtils;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;

@SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
public class Core {
    public static final int UPDATE = 9;
    public static final Color accentColor = new Color(255,143,11);
    public static final Color backgroundColor = new Color(36,37,39);
    public static final File resourceDirectory = new File(System.getProperty("user.dir"), "res");
    public static final File buttonsDirectory = new File(System.getProperty("user.dir"), "res/buttons");
    public static final File iconDirectory = new File(System.getProperty("user.dir"), "res/icon");
    public static final File invDirectory = new File(System.getProperty("user.dir"), "res/inv");
    public static final File soundDirectory = new File(System.getProperty("user.dir"), "res/audio");

    public static class Icon {
        public static BufferedImage applicationIcon = ImageUtils.resize(read(new File(iconDirectory, "application_icon.fbf2")), 20, 20);
        public static BufferedImage loginIcon = ImageUtils.resize(read(new File(iconDirectory, "login_icon.fbf2")), 20, 20);
        public static BufferedImage passwordIcon = ImageUtils.resize(read(new File(iconDirectory, "password_icon.fbf2")), 20, 20);
        public static BufferedImage userIcon = ImageUtils.resize(read(new File(iconDirectory, "user_icon.fbf2")), 20, 20);
        public static BufferedImage closeIcon = ImageUtils.resize(read(new File(iconDirectory, "close_icon.fbf2")), 20, 20);
        public static BufferedImage telegramIcon = ImageUtils.resize(read(new File(iconDirectory, "telegram_icon.fbf2")), 20, 20);
        public static BufferedImage discordIcon = ImageUtils.resize(read(new File(iconDirectory, "discord_icon.fbf2")), 20, 20);
        public static BufferedImage logoutIcon = ImageUtils.resize(read(new File(iconDirectory, "logout_icon.fbf2")), 20, 20);
        public static BufferedImage funpayIcon = ImageUtils.resize(read(new File(iconDirectory, "funpay_icon.fbf2")), 20, 20);
        public static BufferedImage checkSelectedIcon = ImageUtils.resize(read(new File(iconDirectory, "checkbox_selected _icon.fbf2")), 20, 20);
        public static BufferedImage checkUnselectedIcon = ImageUtils.resize(read(new File(iconDirectory, "checkbox_unselected_icon.fbf2")), 20, 20);
        public static BufferedImage keyIcon = ImageUtils.resize(read(new File(iconDirectory, "key_icon.fbf2")), 20, 20);
        public static BufferedImage downArrowIcon = ImageUtils.resize(read(new File(iconDirectory, "down_arrow_icon.fbf2")), 20, 20);
        public static BufferedImage fishIcon = ImageUtils.resize(read(new File(iconDirectory, "fish_icon.fbf2")), 20, 20);
        public static BufferedImage inventoryIcon = ImageUtils.resize(read(new File(iconDirectory, "inventory_icon.fbf2")), 20, 20);
        public static BufferedImage timeIcon = ImageUtils.resize(read(new File(iconDirectory, "time_icon.fbf2")), 20, 20);
        public static BufferedImage timeOutIcon = ImageUtils.resize(read(new File(iconDirectory, "timeout_icon.fbf2")), 20, 20);
        public static BufferedImage hackIcon = ImageUtils.resize(read(new File(iconDirectory, "hack_icon.fbf2")), 20, 20);
        public static BufferedImage hackCheckIcon = ImageUtils.resize(read(new File(iconDirectory, "check_hack_icon.fbf2")), 20, 20);
        public static BufferedImage executeIcon = ImageUtils.resize(read(new File(iconDirectory, "execute_icon.fbf2")), 20, 20);
        public static BufferedImage interfaceIcon = ImageUtils.resize(read(new File(iconDirectory, "interface_icon.fbf2")), 20, 20);
        public static BufferedImage rodIcon = ImageUtils.resize(read(new File(iconDirectory, "rod_icon.fbf2")), 20, 20);
        public static BufferedImage baitIcon = ImageUtils.resize(read(new File(iconDirectory, "bait_icon.fbf2")), 20, 20);
        public static BufferedImage startIcon = ImageUtils.resize(read(new File(iconDirectory, "start_icon.fbf2")), 20, 20);
        public static BufferedImage stopIcon = ImageUtils.resize(read(new File(iconDirectory, "stop_icon.fbf2")), 20, 20);
        public static BufferedImage doneIcon = ImageUtils.resize(read(new File(iconDirectory, "done_icon.fbf2")), 20, 20);
        public static BufferedImage cancelIcon = ImageUtils.resize(read(new File(iconDirectory, "cancel_icon.fbf2")), 20, 20);
        public static BufferedImage updateIcon = ImageUtils.resize(read(new File(iconDirectory, "update_icon.fbf2")), 20, 20);
    }


    public static class Sound{
        public static File notification = new File(soundDirectory, "notification.wav");
    }

    public static class Bait {
        public static BufferedImage[] baits = new BufferedImage[]{
                read(new File(invDirectory, "bait_0.fbf2")),
                read(new File(invDirectory, "bait_1.fbf2")),
                read(new File(invDirectory, "bait_2.fbf2")),
                read(new File(invDirectory, "bait_3.fbf2")),
                read(new File(invDirectory, "bait_4.fbf2"))
        };
        public static String[] baitNames = new String[]{
                "Хлебный мякиш",
                "Личинки насекомых",
                "Дождевые черви",
                "Личинки мотыля",
                "Мальки"
        };
    }

    public static class Rod{
        //public static BufferedImage stick = read(new File(invDirectory, "stick.fbf2"));
        //public static BufferedImage rod = read(new File(invDirectory, "rod.fbf2"));
        public static BufferedImage spin = read(new File(invDirectory, "spin.fbf2"));

        public static BufferedImage[] rods = new BufferedImage[]{
                read(new File(invDirectory, "rod_0.fbf2")),
                read(new File(invDirectory, "rod_1.fbf2")),
                read(new File(invDirectory, "rod_2.fbf2"))
        };
        public static String[] rodNames = new String[]{
            "Палка",
            "Удочка",
            "Спиннинг"
        };
    }

    public static class Captcha {
        public static BufferedImage[] images = new BufferedImage[]{
                read(new File(buttonsDirectory, "captcha_v1.fbf2")),
                read(new File(buttonsDirectory, "captcha_v1_0.fbf2")),
                read(new File(buttonsDirectory, "captcha_v1_1.fbf2")),
                read(new File(buttonsDirectory, "captcha_v1_2.fbf2")),
                read(new File(buttonsDirectory, "captcha_v1_3.fbf2"))
        };
    }

    public static class Other {
        public static BufferedImage server = read(new File(buttonsDirectory, "serv_1.fbf2"));
    }

    public static class Inventory {
        public static BufferedImage instrument_slot_empty = read(new File(invDirectory, "instrument_slot_empty.fbf2"));
        public static BufferedImage instrument = read(new File(invDirectory, "instrument.fbf2"));
    }

    public static String encode(BufferedImage bufferedImage) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", stream);
        return new String(Base64.getEncoder().encode(stream.toByteArray()));
    }

    private static BufferedImage decode(String string) throws IOException {
        return ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(string)));
    }

    public static BufferedImage read(File file) {
        try {
            if (file.getName().contains(".png")){
                return ImageIO.read(file);
            } else
            return decode(Files.readFile(file, Charset.defaultCharset()));
        } catch (IOException e) {
            e.printStackTrace();
            return new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        }
    }


    public static void encodeResourceDirectory(File resourceDirectory) {
        for (File file : resourceDirectory.listFiles()) {
            if (file.isDirectory()) {
                encodeResourceDirectory(file);
            } else {
                if (file.getName().contains("png")) {
                    try {
                        String name = file.getName().replace(".png", ".fbf2");
                        BufferedImage bufferedImage = ImageIO.read(file);
                        Files.writeFile(new File(file.getParentFile(), name), encode(bufferedImage));
                        file.delete();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void decodeResourceDirectory(File resourceDirectory) {
        for (File file : resourceDirectory.listFiles()) {
            if (file.isDirectory()) {
                decodeResourceDirectory(file);
            } else {
                if (file.getName().contains("fbf2")) {
                    try {
                        String name = file.getName().replace(".fbf2", ".png");
                        BufferedImage bufferedImage = read(new File(file.getAbsolutePath()));
                        ImageIO.write(bufferedImage, "png", new File(file.getAbsolutePath().replace(file.getName(), ""), name));
                        file.delete();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
