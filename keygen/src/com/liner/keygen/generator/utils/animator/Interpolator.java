package com.liner.keygen.generator.utils.animator;

public abstract class Interpolator {
    private double delta;
    public abstract double getFactor(double value);

    public double getDelta() {
        return delta;
    }

    public void setDelta(double delta) {
        this.delta = delta;
    }

    public double getValue(double value){
        return delta * getFactor(value);
    }
}
