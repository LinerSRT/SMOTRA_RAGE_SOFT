package com.liner.keygen.generator.utils.animator;

public class AnticipateInterpolator extends Interpolator{
    private final static float TENSION = 2f;
    @Override
    public double getFactor(double value) {
        return value * value * ((TENSION + 1) * value - TENSION);
    }
}
