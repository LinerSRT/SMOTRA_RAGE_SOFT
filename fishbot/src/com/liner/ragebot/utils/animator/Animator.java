package com.liner.ragebot.utils.animator;


import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Animator {
    private Timer timer;
    private int delay = 0;
    private int duration = 500;
    private Interpolator interpolator;
    private AnimationListener animationListener;
    private long time = -1;
    private long animationTime = -1;
    private List<AnimationObject> animationObjectList;

    public Animator() {
    }

    public Animator of(AnimationObject... animationObjects){
        animationObjectList = new ArrayList<>();
        animationObjectList.addAll(Arrays.asList(animationObjects));
        return this;
    }

    public double getAnimatedValueOf(String name){
        for(AnimationObject animationObject:animationObjectList)
            if(animationObject.getName().equals(name))
                return animationObject.getValue();
            return 0;
    }

    public Animator setAnimationListener(AnimationListener animationListener) {
        this.animationListener = animationListener;
        return this;
    }

    public Animator setStartDelay(int delay) {
        this.delay = delay;
        return this;
    }

    public Animator setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public Animator setInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
        return this;
    }
    public void start(){
        start(5);
    }

    public void start(int tick){
        timer = new Timer(tick, actionEvent -> {
            if(animationTime == -1){
                animationListener.onStart();
                time = System.currentTimeMillis();
            }
            animationTime = (int)(System.currentTimeMillis() - time);
            double factor = interpolator.getFactor(Math.min(1.0, (double) animationTime / duration));
            for(AnimationObject animationObject:animationObjectList){
                int from = animationObject.getValues()[0];
                int to = animationObject.getValues()[1];;
                double value = from + ((to * factor)-(from * factor));
                animationObject.setValue(value);
            }
            animationListener.onAnimate(this);
            if(animationTime >= duration){
                timer.stop();
                animationListener.onStop();
            }
        });
        timer.setInitialDelay(delay);
        timer.setCoalesce(true);
        timer.start();
    }

    public void stop(){
        timer.stop();
    }
}
