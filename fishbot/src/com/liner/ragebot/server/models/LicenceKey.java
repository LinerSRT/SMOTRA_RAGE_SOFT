package com.liner.ragebot.server.models;

import com.liner.ragebot.jna.HardwareInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class LicenceKey {
    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
    public HardwareInfo hardwareInfo;
    public String key;
    public Long activateTime;
    public Long durationTime;
    public Long expireTime;
    public Boolean isActivated;
    public Boolean isExpired;

    public HardwareInfo getHardwareInfo() {
        return hardwareInfo;
    }

    public void setHardwareInfo(HardwareInfo hardwareInfo) {
        this.hardwareInfo = hardwareInfo;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getActivateTime() {
        return activateTime;
    }

    public void setActivateTime(Long activateTime) {
        this.activateTime = activateTime;
    }

    public Long getDurationTime() {
        return durationTime;
    }

    public void setDurationTime(Long durationTime) {
        this.durationTime = durationTime;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public Boolean getActivated() {
        return isActivated;
    }

    public void setActivated(Boolean activated) {
        isActivated = activated;
    }

    public Boolean getExpired() {
        return isExpired;
    }

    public void setExpired(Boolean expired) {
        isExpired = expired;
    }


    @Override
    public String toString() {
        return "LicenceKey{" +
                "\n\thardwareInfo=" + hardwareInfo +
                "\n\tkey='" + key + '\'' +
                "\n\tactivateTime=" + simpleDateFormat.format(new Date(activateTime)) +
                "\n\tdurationTime=" + TimeUnit.MILLISECONDS.toHours(durationTime) +
                "\n\texpireTime=" + simpleDateFormat.format(new Date(expireTime)) +
                "\n\tisActivated=" + isActivated +
                "\n\tisExpired=" + isExpired +
                "\n}";
    }
}