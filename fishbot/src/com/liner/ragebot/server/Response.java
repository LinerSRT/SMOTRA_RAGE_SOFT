package com.liner.ragebot.server;


import com.google.gson.internal.LinkedTreeMap;

@SuppressWarnings("rawtypes")
public class Response {
    public String key;
    public double activatedTime;
    public double expiredTime;
    public double durationTime;
    public boolean activated;
    public boolean expired;
    public boolean banned;
    public boolean isOwner;
    public boolean isExists;

    public Response() {
        isExists = false;
    }

    public Response(LinkedTreeMap linkedTreeMap){
        this.isExists = true;
        this.key = (String) linkedTreeMap.get("key");
        this.activatedTime = (double) linkedTreeMap.get("activatedTime");
        this.expiredTime = (double) linkedTreeMap.get("expiredTime");
        this.durationTime = (double) linkedTreeMap.get("durationTime");
        this.activated = (boolean) linkedTreeMap.get("activated");
        this.expired = (boolean) linkedTreeMap.get("expired");
        this.banned = (boolean) linkedTreeMap.get("banned");
        this.isOwner = (boolean) linkedTreeMap.get("isOwner");
    }

    @Override
    public String toString() {
        return "Response{" +
                "key='" + key + '\'' +
                ", activatedTime=" + activatedTime +
                ", expiredTime=" + expiredTime +
                ", durationTime=" + durationTime +
                ", activated=" + activated +
                ", expired=" + expired +
                ", banned=" + banned +
                ", isOwner=" + isOwner +
                '}';
    }
}