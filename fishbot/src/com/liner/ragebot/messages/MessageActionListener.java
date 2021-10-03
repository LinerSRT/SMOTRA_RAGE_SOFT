package com.liner.ragebot.messages;

import java.awt.image.BufferedImage;

public interface MessageActionListener {
    String getName();
    BufferedImage getIcon();
    void onClicked(MessageForm message);
}