package com.liner.ragebot.game;

import java.awt.*;
import java.util.Arrays;

public class Pixel {
    public int x;
    public int y;
    public Color[] color;

    public Pixel(int x, int y, Color... colors) {
        this.x = x;
        this.y = y;
        this.color = colors;
    }
}
