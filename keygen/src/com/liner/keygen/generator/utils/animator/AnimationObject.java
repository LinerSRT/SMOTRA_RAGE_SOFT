package com.liner.keygen.generator.utils.animator;

public class AnimationObject {
    private String name;
    private double value;
    private int[] values;

    public AnimationObject(String name, int[] values) {
        this.name = name;
        this.values = values;
    }

    public double getValue() {
        return value;
    }

    public int[] getValues() {
        return values;
    }

    public String getName() {
        return name;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
