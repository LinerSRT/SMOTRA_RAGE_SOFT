package com.liner.keygen.generator.utils.animator;

public class BounceInterpolator extends Interpolator{
    @Override
    public double getFactor(double value) {
        if (value < (1/2.75)) {
            return 7.5625f*value*value;
        } else if (value < (2/2.75)) {
            return 7.5625f*(value-=(1.5f/2.75f))*value + .75f;
        } else if (value < (2.5/2.75)) {
            return 7.5625f*(value-=(2.25f/2.75f))*value + .9375f;
        } else {
            return 7.5625f*(value-=(2.625f/2.75f))*value + .984375f;
        }
    }
}
