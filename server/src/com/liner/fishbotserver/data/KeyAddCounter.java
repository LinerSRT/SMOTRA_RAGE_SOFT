package com.liner.fishbotserver.data;

import java.util.concurrent.TimeUnit;

public class KeyAddCounter {
    private String key;
    private String addDate;
    private long durationTime;
    private int daysTime;

    public KeyAddCounter(String key, String addDate, long durationTime) {
        this.key = key;
        this.addDate = addDate;
        this.durationTime = durationTime;
        this.daysTime = Math.toIntExact(TimeUnit.MILLISECONDS.toDays(durationTime));
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAddDate() {
        return addDate;
    }

    public void setAddDate(String addDate) {
        this.addDate = addDate;
    }

    public long getDurationTime() {
        return durationTime;
    }

    public void setDurationTime(long durationTime) {
        this.durationTime = durationTime;
    }

    public int getDaysTime() {
        return daysTime;
    }

    public void setDaysTime(int daysTime) {
        this.daysTime = daysTime;
    }

    @Override
    public String toString() {
        return "KeyAddCounter{" +
                "key='" + key + '\'' +
                ", addDate='" + addDate + '\'' +
                ", durationTime=" + durationTime +
                ", daysTime=" + daysTime +
                '}';
    }
}
