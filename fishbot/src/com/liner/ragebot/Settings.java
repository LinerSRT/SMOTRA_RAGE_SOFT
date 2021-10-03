package com.liner.ragebot;

import com.liner.ragebot.utils.ObjectManager;

import java.io.File;

public class Settings {
    private String licenceKey = "";
    private String userName = "";
    private boolean registeredOnServer = false;
    private boolean uploadedToServer = false;
    private boolean autoLogin = false;
    private int rodIndex = 0;
    private int baitIndex = 0;
    private int fishingTime = 0;
    private int fishingTimeout = 0;
    private int inventoryCheckPeriod = 2;
    private boolean qteFix = false;
    private boolean dropBrokenRods = false;
    private boolean useAnyRods = false;
    private boolean useAntBaits = false;
    private boolean suicideIfNoRodsBaits = false;
    private boolean closeGameIfNoRodsBaits = false;
    private boolean stopBotIfNoRodsBaits = true;

    public Settings(String licenceKey) {
        this.licenceKey = licenceKey;
    }

    public String getLicenceKey() {
        return licenceKey;
    }

    public void setLicenceKey(String licenceKey) {
        this.licenceKey = licenceKey;
        save();
    }

    public void setUserName(String userName) {
        this.userName = userName;
        save();
    }

    public String getUserName() {
        return userName;
    }

    public boolean isRegisteredOnServer() {
        return registeredOnServer;
    }

    public void setRegisteredOnServer(boolean registeredOnServer) {
        this.registeredOnServer = registeredOnServer;
        save();
    }

    public boolean isUploadedToServer() {
        return uploadedToServer;
    }

    public void setUploadedToServer(boolean uploadedToServer) {
        this.uploadedToServer = uploadedToServer;
        save();
    }

    public boolean isAutoLogin() {
        return autoLogin;
    }

    public void setAutoLogin(boolean autoLogin) {
        this.autoLogin = autoLogin;
        save();
    }

    public int getRodIndex() {
        return rodIndex;
    }

    public void setRodIndex(int rodIndex) {
        this.rodIndex = rodIndex;
        save();
    }

    public int getBaitIndex() {
        return baitIndex;
    }

    public void setBaitIndex(int baitIndex) {
        this.baitIndex = baitIndex;
        save();
    }

    public int getFishingTime() {
        return fishingTime;
    }

    public void setFishingTime(int fishingTime) {
        this.fishingTime = fishingTime;
        save();
    }

    public int getFishingTimeout() {
        return fishingTimeout;
    }

    public void setFishingTimeout(int fishingTimeout) {
        this.fishingTimeout = fishingTimeout;
        save();
    }

    public int getInventoryCheckPeriod() {
        return inventoryCheckPeriod;
    }

    public void setInventoryCheckPeriod(int inventoryCheckPeriod) {
        this.inventoryCheckPeriod = inventoryCheckPeriod;
        save();
    }

    public boolean isDropBrokenRods() {
        return dropBrokenRods;
    }

    public void setDropBrokenRods(boolean dropBrokenRods) {
        this.dropBrokenRods = dropBrokenRods;
        save();
    }

    public boolean isUseAnyRods() {
        return useAnyRods;
    }

    public void setUseAnyRods(boolean useAnyRods) {
        this.useAnyRods = useAnyRods;
        save();
    }

    public boolean isUseAntBaits() {
        return useAntBaits;
    }

    public void setUseAntBaits(boolean useAntBaits) {
        this.useAntBaits = useAntBaits;
        save();
    }

    public boolean isSuicideIfNoRodsBaits() {
        return suicideIfNoRodsBaits;
    }

    public void setSuicideIfNoRodsBaits(boolean suicideIfNoRodsBaits) {
        this.suicideIfNoRodsBaits = suicideIfNoRodsBaits;
        save();
    }

    public boolean isCloseGameIfNoRodsBaits() {
        return closeGameIfNoRodsBaits;
    }

    public void setCloseGameIfNoRodsBaits(boolean closeGameIfNoRodsBaits) {
        this.closeGameIfNoRodsBaits = closeGameIfNoRodsBaits;
        save();
    }

    public boolean isStopBotIfNoRodsBaits() {
        return stopBotIfNoRodsBaits;
    }

    public void setStopBotIfNoRodsBaits(boolean stopBotIfNoRodsBaits) {
        this.stopBotIfNoRodsBaits = stopBotIfNoRodsBaits;
        save();
    }

    public void setQteFix(boolean qteFix) {
        this.qteFix = qteFix;
        save();
    }

    public boolean isQteFix() {
        return qteFix;
    }

    public void save() {
        new ObjectManager().save("settings.fbs", this);
    }

    public static Settings load() {
        if (!new File(System.getProperty("user.dir"), "settings.fbs").exists()) {
            Settings settings = new Settings(null);
            settings.save();
            return settings;
        }
        return new ObjectManager().load("settings.fbs", Settings.class);
    }

    @Override
    public String toString() {
        return "Settings{" +
                "\n\tlicenceKey='" + licenceKey + '\'' +
                "\n\tautoLogin=" + autoLogin +
                "\n\trodIndex=" + rodIndex +
                "\n\tbaitIndex=" + baitIndex +
                "\n\tfishingTime=" + fishingTime +
                "\n\tfishingTimeout=" + fishingTimeout +
                "\n\tinventoryCheckPeriod=" + inventoryCheckPeriod +
                "\n\tdropBrokenRods=" + dropBrokenRods +
                "\n\tuseAnyRods=" + useAnyRods +
                "\n\tuseAntBaits=" + useAntBaits +
                "\n\tsuicideIfNoRodsBaits=" + suicideIfNoRodsBaits +
                "\n\tcloseGameIfNoRodsBaits=" + closeGameIfNoRodsBaits +
                "\n\tstopBotIfNoRodsBaits=" + stopBotIfNoRodsBaits +
                "\n}";
    }
}
