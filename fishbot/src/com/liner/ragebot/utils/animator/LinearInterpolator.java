package com.liner.ragebot.utils.animator;

public class LinearInterpolator extends Interpolator{
    @Override
    public double getFactor(double value) {
        return value;
    }
}
