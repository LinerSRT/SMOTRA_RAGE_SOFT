package com.liner.ragebot.server.models;

import com.liner.ragebot.jna.HardwareInfo;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class User {
    private HardwareInfo hardwareInfo;
    private String gameUsername;
    private Boolean haveAdminRights;
    private Boolean havePremiumRights;
    private Boolean isBanned;
    private byte[] banReason;
    private Boolean isOnline;
    private String lastOnlineTime;
    private List<PromoCode> promoCodes = null;
    private List<String> licenceKeys = null;
    private List<Notification> notificationMessages = null;

    public HardwareInfo getHardwareInfo() {
        return hardwareInfo;
    }

    public String getGameUsername() {
        return gameUsername;
    }

    public Boolean getHaveAdminRights() {
        return haveAdminRights;
    }

    public Boolean getHavePremiumRights() {
        return havePremiumRights;
    }

    public Boolean getBanned() {
        return isBanned;
    }

    public String getBanReason() {
        return new String(banReason, StandardCharsets.UTF_8);
    }

    public Boolean getOnline() {
        return isOnline;
    }

    public String getLastOnlineTime() {
        return lastOnlineTime;
    }

    public List<PromoCode> getPromoCodes() {
        return promoCodes;
    }

    public List<String> getLicenceKeys() {
        return licenceKeys;
    }

    public List<Notification> getNotificationMessages() {
        return notificationMessages;
    }

    @Override
    public String toString() {
        return "User{" +
                "\n\thardwareInfo=" + hardwareInfo +
                "\n\tgameUsername='" + gameUsername + '\'' +
                "\n\thaveAdminRights=" + haveAdminRights +
                "\n\thavePremiumRights=" + havePremiumRights +
                "\n\tisBanned=" + isBanned +
                "\n\tbanReason='" + banReason + '\'' +
                "\n\tisOnline=" + isOnline +
                "\n\tlastOnlineTime='" + lastOnlineTime + '\'' +
                "\n\tpromoCodes=" + promoCodes +
                "\n\tlicenceKeys=" + licenceKeys +
                "\n\tnotificationMessages=" + notificationMessages +
                "\n}";
    }
}
