package com.liner.ragebot;

import com.liner.ragebot.jna.KeyboardHook;
import com.liner.ragebot.jna.RageMultiplayer;
import com.liner.ragebot.utils.ImageUtils;
import com.sun.jna.platform.win32.WinDef;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GeneratorBot implements KeyboardHook.KeyCallback {
    private static JFrame frame;
    private JPanel panel;
    private JTextField position;
    private JTextField color;
    private JPanel colorPanel;
    private JLabel preview;
    private JButton save;
    private JButton saveGR;
    private JButton updateView;
    private WinDef.HWND window;
    private BufferedImage bufferedImage;
    private float scale = 1.3f;
    private boolean isLocked = false;
    private RageMultiplayer rageMultiplayer;

    public GeneratorBot() {
        rageMultiplayer = new RageMultiplayer();
        updateView.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                bufferedImage = rageMultiplayer.getBuffer();
                preview.setIcon(new ImageIcon(ImageUtils.resize(bufferedImage, bufferedImage.getWidth() / scale, bufferedImage.getHeight() / scale)));
                frame.setSize((int) (bufferedImage.getWidth() / scale) + 20, (int) (bufferedImage.getHeight() / scale) + 150);

            }
        });
        save.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    ImageIO.write(
                            bufferedImage,
                            "png",
                            new File(
                                    System.getProperty("user.dir"),
                                    "gen_" + new SimpleDateFormat("HH_mm_ss").format(new Date()) + ".png"
                            )
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        saveGR.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    ImageIO.write(
                            ImageUtils.grayScaleImage(bufferedImage),
                            "png",
                            new File(
                                    System.getProperty("user.dir"),
                                    "gen_" + new SimpleDateFormat("HH_mm_ss").format(new Date()) + "(GR).png"
                            )
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        bufferedImage = rageMultiplayer.getBuffer();
        preview.setIcon(new ImageIcon(ImageUtils.resize(bufferedImage, bufferedImage.getWidth() / scale, bufferedImage.getHeight() / scale)));
        frame.setSize((int) (bufferedImage.getWidth() / scale) + 20, (int) (bufferedImage.getHeight() / scale) + 150);
    }

    public static void main(String[] args) {
        Core.encodeResourceDirectory(Core.resourceDirectory);
        frame = new JFrame("Generator");
        frame.setContentPane(new GeneratorBot().panel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {
        preview = new PreviewPanel();
    }


    @Override
    public void onKeyDown(int keyCode) {
        if(keyCode == KeyEvent.VK_Q){
            bufferedImage = rageMultiplayer.getBuffer();
            preview.setIcon(new ImageIcon(ImageUtils.resize(bufferedImage, bufferedImage.getWidth() / scale, bufferedImage.getHeight() / scale)));
            frame.setSize((int) (bufferedImage.getWidth() / scale) + 20, (int) (bufferedImage.getHeight() / scale) + 150);
        } else if(keyCode == KeyEvent.VK_E){
            isLocked = !isLocked;
            color.setEditable(isLocked);
            position.setEditable(isLocked);
        }
    }

    @Override
    public void onKeyUp(int keyCode) {

    }

    private class PreviewPanel extends JLabel implements MouseMotionListener {
        private int mouseX = 0;
        private int mouseY = 0;

        public PreviewPanel() {
            addMouseMotionListener(this);
        }

        @Override
        public void paint(Graphics graphics) {
            super.paint(graphics);
            int x = Math.round(mouseX * scale);
            int y = Math.round(mouseY * scale);
            if (x - 25 < 0 || x + 50 > bufferedImage.getWidth())
                return;
            if (y - 25 < 0 || y + 50 > bufferedImage.getHeight())
                return;
            Graphics2D graphics2D = (Graphics2D) graphics;
            graphics2D.setColor(Color.RED);
            BufferedImage bbb = ImageUtils.scale(bufferedImage.getSubimage(x - 25, y - 25, 50, 50), 2);
            Graphics2D ggg = (Graphics2D) bbb.getGraphics();
            ggg.setColor(Color.RED);
            ggg.setStroke(new BasicStroke(1));
            ggg.drawLine(bbb.getWidth() / 2, 0, bbb.getHeight() / 2, bbb.getHeight());
            ggg.drawLine(0, bbb.getHeight() / 2, bbb.getWidth(), bbb.getHeight() / 2);
            graphics2D.drawImage(bbb, mouseX - bbb.getHeight() / 2 + 50, mouseY - bbb.getHeight() / 2 + 50, null);
        }

        @Override
        public void mouseDragged(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseMoved(MouseEvent mouseEvent) {
            if (!isLocked) {
                mouseX = mouseEvent.getX();
                mouseY = mouseEvent.getY();
                position.setText(Math.round(mouseEvent.getX() * scale) + "," + Math.round(mouseEvent.getY() * scale));
                Color colorV = new Color(bufferedImage.getRGB(Math.round(mouseEvent.getX() * scale), Math.round(mouseEvent.getY() * scale)));
                color.setText("new Color(" + colorV.getRed() + ", " + colorV.getGreen() + ", " + colorV.getBlue() + ");");
                colorPanel.setBackground(colorV);
            }
            repaint();
        }
    }
}
