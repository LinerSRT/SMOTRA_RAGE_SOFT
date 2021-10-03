package com.liner.ragebot.auth;

public interface CheckUserCallback {
    void startRegister(String username);
    void failedLoadUsername();
    void failedConfirmUsername();
}
