package com.liner.ragebot.auth;

import com.liner.ragebot.server.models.LicenceKey;
import com.liner.ragebot.server.models.User;

public interface CheckLicenceCallback {
    void successLogin(User user, LicenceKey licenceKey);
    void onKeyActivate(String licenceKey);
    void onKeyActivateFailed();
    void onKeyExpired();
    void onKeyHardwareMismatch();
    void onWrongKeyFormat();
    void onFailedKeyInfoGather();
}
