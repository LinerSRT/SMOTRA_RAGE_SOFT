package com.liner.keygen.generator.utils.animator;

import static java.lang.Math.PI;

public class SineInterpolator extends Interpolator{
    @Override
    public double getFactor(double value) {
        return -Math.cos(value * (PI/2)) + 1;
    }
}
