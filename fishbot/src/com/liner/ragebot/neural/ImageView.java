package com.liner.ragebot.neural;


import com.liner.ragebot.utils.ImageUtils;
import com.liner.ragebot.utils.RandomString;
import com.liner.ragebot.utils.StringMetrics;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class ImageView extends JPanel {
    private BufferedImage bufferedImage;
    private JFrame frame;

    public ImageView(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
        frame = new JFrame("Display Image");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add("Center", this);
        frame.getContentPane().setSize(bufferedImage.getWidth(), bufferedImage.getHeight()*2);
        frame.setSize(bufferedImage.getWidth(), bufferedImage.getHeight()*2);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
        frame.getContentPane().removeAll();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add("Center", this);
        frame.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bufferedImage != null) {
            g.drawImage(bufferedImage, 0, 0, this);
        }
    }
}