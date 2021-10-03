package com.liner.ragebot.bot;

import com.liner.ragebot.server.models.LicenceKey;
import com.liner.ragebot.server.models.User;

public interface ServerCheckerCallback {
    void onSuccess(User user, LicenceKey licenceKey);
    void onNewUserMessage(User user);
    void onUserBanned(User user);
    void onKeyExpired(LicenceKey licenceKey);
    void onKeyHardwareMismatch(LicenceKey licenceKey);
    void onKeyNotActivated(LicenceKey licenceKey);
    void onError();
}
