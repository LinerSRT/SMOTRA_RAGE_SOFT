package com.liner.fishbotserver.utilities;

import java.util.concurrent.TimeUnit;

public class Time {
    public static long getFixedCurrentTime(){
        return System.currentTimeMillis()+ TimeUnit.HOURS.toMillis(7);
    }
}
