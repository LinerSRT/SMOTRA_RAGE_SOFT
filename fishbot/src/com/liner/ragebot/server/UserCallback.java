package com.liner.ragebot.server;

import com.liner.ragebot.server.models.User;

public interface UserCallback {

    void onFinished(User user);

    void onRetry(String reason);

    void onError(String reason);
}
