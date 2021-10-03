package com.liner.fishbotserver.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class HardwareInfo {
    private String cpuSerialNumber;
    private String motherSerialNumber;
    private String diskSerialNumber;
    private String macAddress;
    private String windowsPCName;

    public HardwareInfo(){
    }

    public HardwareInfo(String cpuSerialNumber, String motherSerialNumber, String diskSerialNumber, String macAddress, String windowsPCName) {
        this.cpuSerialNumber = cpuSerialNumber;
        this.motherSerialNumber = motherSerialNumber;
        this.diskSerialNumber = diskSerialNumber;
        this.macAddress = macAddress;
        this.windowsPCName = windowsPCName;
    }

    public String getCpuSerialNumber() {
        return cpuSerialNumber;
    }

    public String getMotherSerialNumber() {
        return motherSerialNumber;
    }

    public String getDiskSerialNumber() {
        return diskSerialNumber;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public String getWindowsPCName() {
        return windowsPCName;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof HardwareInfo)) return false;
        HardwareInfo hardwareInfo = (HardwareInfo) object;
        return cpuSerialNumber.equals(hardwareInfo.cpuSerialNumber) &&
                motherSerialNumber.equals(hardwareInfo.motherSerialNumber) &&
                diskSerialNumber.equals(hardwareInfo.diskSerialNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpuSerialNumber, motherSerialNumber, diskSerialNumber, macAddress, windowsPCName);
    }



    @Override
    public String toString() {
        return "HardwareInfo{" +
                "\n\tcpuSerialNumber='" + cpuSerialNumber + '\'' +
                "\n\tmotherSerialNumber='" + motherSerialNumber + '\'' +
                "\n\tdiskSerialNumber='" + diskSerialNumber + '\'' +
                "\n\tmacAddress='" + macAddress + '\'' +
                "\n\twindowsPCName='" + windowsPCName + '\'' +
                "\n}";
    }
}
