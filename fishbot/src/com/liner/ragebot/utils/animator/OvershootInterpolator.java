package com.liner.ragebot.utils.animator;

public class OvershootInterpolator extends Interpolator{
    private static final float TENSION = 2.0f;

    @Override
    public double getFactor(double value) {
        value -= 1.0f;
        return value * value * ((TENSION + 1) * value + TENSION) + 1.0f;
    }
}
