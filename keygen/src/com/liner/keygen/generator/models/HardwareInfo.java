package com.liner.keygen.generator.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class HardwareInfo {
    private final String cpuSerialNumber;
    private final String motherSerialNumber;
    private final String diskSerialNumber;
    private final String macAddress;
    private final String windowsPCName;

    public HardwareInfo(){
        cpuSerialNumber = execCommand("wmic cpu get processorid")
                .replace("ProcessorId", "")
                .replace(" ", "");
        motherSerialNumber = execCommand("wmic baseboard get serialnumber")
                .replace("SerialNumber", "")
                .replace(" ", "");
        diskSerialNumber = execCommand("wmic diskdrive get serialnumber")
                .replace("SerialNumber", "")
                .replace(" ", "");
        macAddress = execCommand("wmic nic get macaddress")
                .replace("MACAddress", "")
                .replace(" ", "")
                .replace(":", "");
        windowsPCName = execCommand("wmic os get Caption")
                .replace("Caption", "")
                .replace(" ", "")
                .replace("�", "");
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
                diskSerialNumber.equals(hardwareInfo.diskSerialNumber) &&
                windowsPCName.equals(hardwareInfo.windowsPCName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpuSerialNumber, motherSerialNumber, diskSerialNumber, macAddress, windowsPCName);
    }

    private static String execCommand(String command) {
        try {
            StringBuilder result = new StringBuilder();
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            bufferedReader.close();
            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
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
