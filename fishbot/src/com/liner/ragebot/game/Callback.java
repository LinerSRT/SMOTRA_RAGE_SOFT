package com.liner.ragebot.game;

public interface Callback {
    void onFinish();
    void onFail(String reason);
}
