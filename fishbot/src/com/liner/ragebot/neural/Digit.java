package com.liner.ragebot.neural;

import java.awt.image.BufferedImage;

public class Digit {
    private final BufferedImage bufferedImage;
    private final int answer;
    private double[] input;

    public Digit(BufferedImage bufferedImage, int answer) {
        this.bufferedImage = bufferedImage;
        this.answer = answer;
        this.input = new double[bufferedImage.getWidth() * bufferedImage.getHeight()];
        for (int x = 0; x < bufferedImage.getWidth(); x++) {
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                input[x + y * x] = (bufferedImage.getRGB(x, y) & 0xff) / 255.0;
            }
        }
    }

    public Digit(BufferedImage bufferedImage) {
        this(bufferedImage, -1);
    }

    public int getAnswer() {
        return answer;
    }

    public double[] getInput() {
        return input;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }
}
