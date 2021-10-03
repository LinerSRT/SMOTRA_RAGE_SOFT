package com.liner.ragebot.messages;

import com.liner.ragebot.Core;
import com.liner.ragebot.ui.CircleProgressBar;
import com.liner.ragebot.ui.DialogMouseListener;
import com.liner.ragebot.ui.LinerButton;
import com.liner.ragebot.ui.RoundedPanel;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("ALL")
public class MessageForm {
    private final JFrame frame;
    private RoundedPanel dragPanel;
    private JPanel panel;
    private LinerButton closeButton;
    private JLabel messageTitle;
    private CircleProgressBar messageProgress;
    private RoundedPanel roundedPanel2;
    private LinerButton messageCancelButton;
    private JLabel dialogMessage;
    private LinerButton messageOkButton;


    public MessageForm(MessageConfig messageConfig) {
        frame = new JFrame("");
        frame.setIconImage(Core.Icon.applicationIcon);
        frame.setContentPane(panel);
        frame.setUndecorated(true);
        frame.setAlwaysOnTop(true);
        frame.setBackground(new Color(0, 0, 0, 0));
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        DialogMouseListener dialogMouseListener = new DialogMouseListener(frame);
        dragPanel.addMouseListener(dialogMouseListener);
        dragPanel.addMouseMotionListener(dialogMouseListener);
        messageTitle.setIcon(new ImageIcon(messageConfig.getMessageIcon()));
        messageTitle.setText(messageConfig.getMessageTitle());
        closeButton.setIcon(Core.Icon.closeIcon);
        closeButton.setIconPosition(LinerButton.IconPosition.RIGHT);
        closeButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                close();
            }
        });
        dialogMessage.setText(String.format("<html><div style=\"width:%dpx;\">%s</div></html>", 100, messageConfig.getMessageText()));
        if (messageConfig.getMessageType() == MessageType.PROGRESS) {
            messageProgress.setIndeterminate(false);
            messageProgress.setValue(0);
        } else if (messageConfig.getMessageType() == MessageType.PROGRESS_INDETERMINATE) {
            messageProgress.setIndeterminate(true);
        } else {
            messageProgress.setVisible(false);
        }
        if (messageConfig.getCancelListener() != null) {
            messageCancelButton.setVisible(true);
            messageCancelButton.setText(messageConfig.getCancelListener().getName());
            messageCancelButton.setIcon(messageConfig.getCancelListener().getIcon());
            messageCancelButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    messageConfig.getCancelListener().onClicked(MessageForm.this);
                }
            });
        } else {
            messageCancelButton.setVisible(false);
        }

        if (messageConfig.getConfirmListener() != null) {
            messageOkButton.setVisible(true);
            messageOkButton.setText(messageConfig.getConfirmListener().getName());
            messageOkButton.setIcon(messageConfig.getConfirmListener().getIcon());
            messageOkButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    messageConfig.getConfirmListener().onClicked(MessageForm.this);
                }
            });
        } else {
            messageOkButton.setVisible(false);
        }
        messageProgress.setForegroundColor(messageConfig.getMessageType().getColor());
        dragPanel.setBottomColor(messageConfig.getMessageType().getColor());
        dragPanel.setHeaderColor(messageConfig.getMessageType().getColor());
        frame.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension dialogSize = frame.getSize();
        int finalX;
        int finalY;
        switch (messageConfig.getMessagePosition()) {
            case CENTER:
                finalX = (int) (screenSize.getWidth() / 2) - (int) (dialogSize.getWidth() / 2);
                finalY = (int) (screenSize.getHeight() / 2) - (int) (dialogSize.getHeight() / 2);
                break;
            case TOP_RIGHT:
                finalX = (int) (screenSize.getWidth()) - (int) (dialogSize.getWidth()) - 8;
                finalY = 8;
                break;
            case BOTTOM_LEFT:
                finalX = 8;
                finalY = (int) (screenSize.getHeight()) - (int) (dialogSize.getHeight()) - 64;
                break;
            case BOTTOM_RIGHT:
                finalX = (int) (screenSize.getWidth()) - (int) (dialogSize.getWidth()) - 64;
                finalY = (int) (screenSize.getHeight()) - (int) (dialogSize.getHeight()) - 64;
                break;
            case TOP_LEFT:
            default:
                finalX = 8;
                finalY = 8;
        }
        frame.setLocation(finalX, finalY);
    }


    public void setProgress(int progress) {
        if (messageProgress.isIndeterminate())
            messageProgress.setIndeterminate(false);
        messageProgress.setValue(progress, false);
    }

    public void show() {
        frame.setVisible(true);
        panel.setOpaque(false);
        panel.setBackground(new Color(0, 0, 0, 0));
    }

    public void close() {
        frame.setVisible(false);
        frame.dispose();
    }

    public void setAlwaysOnTop(boolean value) {
        frame.setAlwaysOnTop(value);
    }


    public void playSound(File soundFile) {
        new Thread(() -> {
            try {
                Clip clip = AudioSystem.getClip();
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                        new BufferedInputStream(new FileInputStream(soundFile))
                );
                clip.open(audioInputStream);
                clip.start();
            } catch (Exception ignored) {
            }
        }).start();
    }

    public void closeAfter(long millis) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                close();
            }
        }, millis);
    }
}
