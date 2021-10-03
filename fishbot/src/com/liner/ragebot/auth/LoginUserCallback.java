package com.liner.ragebot.auth;

import com.liner.ragebot.server.models.User;

public interface LoginUserCallback {
    void onSuccess(User user);
    void onAlreadyRegistered();
    void onFailedLogin();
    void onUserBanned(User user);
}
