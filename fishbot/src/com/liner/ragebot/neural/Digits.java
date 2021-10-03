package com.liner.ragebot.neural;


import com.liner.ragebot.utils.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@SuppressWarnings("unused")
public class Digits {
    private BufferedImage bufferedImage;
    private List<Digit> digitList;
    private int answer;


    public Digits(BufferedImage original, int answer) {
        this.answer = answer;
        this.digitList = new ArrayList<>();
        findDigits(original);
    }

    public Digits(BufferedImage original) {
        this(original, -1);
    }

    public Digits(File file) {
        try {
            this.answer = Integer.parseInt(file.getName().split("_")[1]);
            this.digitList = new ArrayList<>();
            bufferedImage = ImageIO.read(file);
            findDigits(bufferedImage);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public Digits(int number) {
        if (number > 9) {
            this.answer = number;
            this.digitList = new ArrayList<>();
            int first = Integer.parseInt(String.valueOf(String.valueOf(number).toCharArray()[0]));
            int second = Integer.parseInt(String.valueOf(String.valueOf(number).toCharArray()[1]));
            this.digitList.add(new Digit(generate(first), first));
            this.digitList.add(new Digit(generate(second), second));
        } else {
            this.answer = number;
            this.digitList = new ArrayList<>();
            this.digitList.add(new Digit(generate(number), number));
        }
    }

    public BufferedImage getImage() {
        int width = 0;
        int height = 0;
        for (Digit digit : digitList) {
            width += digit.getBufferedImage().getWidth();
            height = digit.getBufferedImage().getHeight();
        }
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = (Graphics2D) bufferedImage.getGraphics();
        for (int i = 0; i < digitList.size(); i++) {
            Digit digit = digitList.get(i);
            graphics2D.drawImage(digit.getBufferedImage(), digit.getBufferedImage().getWidth() * i, 0, null);
        }
        return this.bufferedImage == null ? bufferedImage : binarize(autoCrop(extractDigits(this.bufferedImage)));
    }

    private void findDigits(BufferedImage digitBuffer) {
        BufferedImage processed = binarize(autoCrop(extractDigits(digitBuffer)));
        int digitCount = String.valueOf(answer).toCharArray().length;
        if (digitCount > 1) {
            for (int i = 0; i < digitCount; i++) {
                int segmentWidth = processed.getWidth() / digitCount;
                int digitAnswer = answer == -1 ? -1 : Integer.parseInt(String.valueOf(String.valueOf(answer).toCharArray()[i]));
                digitList.add(
                        new Digit(
                                processed.getSubimage(i * segmentWidth, 0, segmentWidth, processed.getHeight()),
                                digitAnswer
                        )
                );
            }
        } else {
            int segmentWidth = processed.getWidth() / digitCount;
            int digitAnswer = answer == -1 ? -1 : answer;
            digitList.add(
                    new Digit(
                            processed.getSubimage(0, 0, segmentWidth, processed.getHeight()),
                            digitAnswer
                    )
            );
        }

    }

    public List<Digit> getDigitList() {
        return digitList;
    }

    public int getAnswer() {
        return answer;
    }

    public static BufferedImage extractDigits(BufferedImage bufferedImage) {


        return ImageProcessor.autoCrop(bufferedImage.getSubimage(bufferedImage.getWidth() - 40, 0, 40, 20));
    }

    public static BufferedImage autoCrop(BufferedImage digitBuffer) {
        int startX = 0;
        int endX = digitBuffer.getWidth();
        int startY = 0;
        int endY = digitBuffer.getHeight();
        Color background = new Color(184, 184, 184);
        Color background2 = new Color(69, 69, 69);
        for (int x = digitBuffer.getWidth() - 1; x > 0; x--) {
            int y = digitBuffer.getHeight() / 2;
            Color currentColor = new Color(digitBuffer.getRGB(x, y));
            if (!currentColor.equals(background) && !currentColor.equals(background2)) {
                endX = x;
                break;
            }
        }
        for (int y = digitBuffer.getHeight() - 1; y > 0; y--) {
            int x = digitBuffer.getWidth() / 2;
            Color currentColor = new Color(digitBuffer.getRGB(x, y));
            if (!currentColor.equals(background) && !currentColor.equals(background2)) {
                endY = y + 1;
                break;
            }
        }

        startX = Math.max(endX - 15, 0);
        startY = Math.max(endY - 10, 0);
        return digitBuffer.getSubimage(startX, startY, (endX - startX), (endY - startY));
    }

    public static BufferedImage binarize(BufferedImage digitBuffer) {
        for (int x = 0; x < digitBuffer.getWidth(); x++) {
            for (int y = 0; y < digitBuffer.getHeight(); y++) {
                if (new Color(digitBuffer.getRGB(x, y)).equals(new Color(69, 69, 69))) {
                    digitBuffer.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }
        return digitBuffer;
    }

    public static BufferedImage generate(int number) {
        int width = 15;
        int height = 10;
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = (Graphics2D) bufferedImage.getGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setColor(Color.BLACK);
        graphics2D.fillRect(0, 0, width, height);
        graphics2D.setFont(new Font("Gilroy-Light", Font.PLAIN, 14));
        graphics2D.setColor(Color.WHITE);
        String text = String.valueOf(number);
        FontMetrics metrics = graphics2D.getFontMetrics(graphics2D.getFont());
        Rectangle rectangle = new Rectangle(0, 0, width, height);
        graphics2D.drawString(
                text,
                rectangle.x + (rectangle.width - metrics.stringWidth(text)) / 2,
                rectangle.y + ((rectangle.height - metrics.getHeight()) / 2) + metrics.getAscent()
        );
        return bufferedImage;
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        File[] fileList = new File(System.getProperty("user.dir"), "screenshots").listFiles();
        final BufferedImage[] bufferedImage = {ImageIO.read(new File(System.getProperty("user.dir"), "screenshots/slot_2_dslgksg.png"))};
        ImageView imageView = new ImageView(ImageIO.read(Objects.requireNonNull(fileList)[new Random().nextInt(fileList.length)]));
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        bufferedImage[0] = ImageIO.read(Objects.requireNonNull(fileList)[new Random().nextInt(fileList.length)]);
                        BufferedImage buffer = bufferedImage[0].getSubimage(bufferedImage[0].getWidth() - 45, 0, 45, 25);
                        Color backgroundColor = new Color(69, 69, 69);

                        imageView.setBufferedImage(
                                ImageUtils.scale(autoCrop(buffer, backgroundColor), 8));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    public static BufferedImage autoCrop(BufferedImage source, Color color) {
        int width = getTrimmedWidth(source, color);
        int height = getTrimmedHeight(source, color);
        BufferedImage newImg = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics g = newImg.createGraphics();
        g.drawImage(source, 0, 0, null);
        return newImg;
    }

    private static int getTrimmedWidth(BufferedImage bufferedImage, Color color) {
        int height = bufferedImage.getHeight();
        int width = bufferedImage.getWidth();
        int trimmedWidth = 0;

        for (int i = 0; i < height; i++) {
            for (int j = width - 1; j >= 0; j--) {
                if (bufferedImage.getRGB(j, i) != color.getRGB() &&
                        j > trimmedWidth) {
                    trimmedWidth = j;
                    break;
                }
            }
        }

        return trimmedWidth;
    }

    private static int getTrimmedHeight(BufferedImage bufferedImage, Color color) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int trimmedHeight = 0;

        for (int i = 0; i < width; i++) {
            for (int j = height - 1; j >= 0; j--) {
                if (bufferedImage.getRGB(i, j) != color.getRGB() &&
                        j > trimmedHeight) {
                    trimmedHeight = j;
                    break;
                }
            }
        }

        return trimmedHeight;
    }


    @Override
    public String toString() {
        return "Digits{" +
                "answer=" + answer +
                '}';
    }
}
