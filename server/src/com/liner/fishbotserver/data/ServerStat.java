package com.liner.fishbotserver.data;

public class ServerStat {
    private String serverStatus;
    private String serviceUptime;
    private String serviceStarted;

    public ServerStat(String serverStatus, String serviceUptime, String serviceStarted) {
        this.serverStatus = serverStatus;
        this.serviceUptime = serviceUptime;
        this.serviceStarted = serviceStarted;
    }

    public String getServerStatus() {
        return serverStatus;
    }

    public void setServerStatus(String serverStatus) {
        this.serverStatus = serverStatus;
    }

    public String getServiceUptime() {
        return serviceUptime;
    }

    public void setServiceUptime(String serviceUptime) {
        this.serviceUptime = serviceUptime;
    }

    public String getServiceStarted() {
        return serviceStarted;
    }

    public void setServiceStarted(String serviceStarted) {
        this.serviceStarted = serviceStarted;
    }

    @Override
    public String toString() {
        return "ServerStat{" +
                "serverStatus='" + serverStatus + '\'' +
                ", serviceUptime='" + serviceUptime + '\'' +
                ", serviceStarted='" + serviceStarted + '\'' +
                '}';
    }
}
