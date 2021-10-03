package com.liner.ragebot.utils;

public abstract class Check {
    private Check nextCheck;
    private boolean isFinished;

    public Check setNext(Check nextCheck) {
        this.nextCheck = nextCheck;
        return nextCheck;
    }

    protected boolean next(){
        isFinished = false;
        if (nextCheck == null) {
            isFinished = true;
            return true;
        }
        return nextCheck.check();
    }

    public Check getNextCheck() {
        return nextCheck;
    }

    public abstract boolean check();

    public void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isFinished() {
        return isFinished;
    }
}
