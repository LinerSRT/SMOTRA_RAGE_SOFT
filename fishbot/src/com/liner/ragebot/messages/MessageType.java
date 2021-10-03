package com.liner.ragebot.messages;

import java.awt.*;

public enum MessageType {
    PROGRESS(new Color(44, 97, 232)),
    PROGRESS_INDETERMINATE(new Color(44, 97, 232)),
    INFO(new Color(44, 97, 232)),
    WARNING(new Color(232, 207, 44)),
    ERROR(new Color(232, 44, 44)),
    FINISH(new Color(78, 232, 44));
    private Color color;
    MessageType(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
