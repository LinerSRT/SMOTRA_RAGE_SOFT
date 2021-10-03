package com.liner.fishbotserver.data;

import com.liner.fishbotserver.ObjectManager;
import com.liner.fishbotserver.utilities.Time;

import java.io.File;
import java.util.Objects;

public class LicenceKey {
    public static final File licenceDirectory = new File(System.getProperty("user.dir"), "bot_licence");
    private HardwareInfo hardwareInfo;
    private final String key;
    private long activateTime;
    private long durationTime;
    private long expireTime;
    private boolean isActivated;
    private boolean isExpired;

    public LicenceKey(String key, long durationTime) {
        this.key = key;
        this.durationTime = durationTime;
        this.activateTime = 0;
        this.expireTime = 0;
        this.isActivated = false;
        this.isExpired = false;
        this.hardwareInfo = null;
    }

    public String getKey() {
        return key;
    }

    public long getDurationTime() {
        return durationTime;
    }

    public long getExpireTime() {
        return isActivated ? expireTime : Time.getFixedCurrentTime() + durationTime;
    }

    public long getActivateTime() {
        return isActivated ? activateTime : Time.getFixedCurrentTime();
    }

    public HardwareInfo getHardwareInfo() {
        return hardwareInfo;
    }

    public boolean activateKey(HardwareInfo hardwareInfo) {
        return activateKey(hardwareInfo, null);
    }

    public boolean activateKey(HardwareInfo hardwareInfo, PromoCode promoCode) {
        if (hardwareInfo == null || isActivated)
            return false;
        User user = User.loadByHardware(hardwareInfo);
        if (user == null)
            return false;
        this.hardwareInfo = hardwareInfo;
        this.isActivated = true;
        this.isExpired = false;
        this.activateTime = Time.getFixedCurrentTime();
        if (promoCode != null) {
            if(user.isPromoCodeValid(promoCode)){
                if(user.activatePromo(promoCode)){
                    this.durationTime += ((durationTime / 100L) * promoCode.getPercent());
                    user.save();
                }
            }
        }
        this.expireTime = activateTime + durationTime;
        user.getLicenceKeys().add(key);
        user.save();
        save();
        return true;
    }

    public boolean deactivateKey(HardwareInfo hardwareInfo) {
        if (hardwareInfo == null || !isActivated)
            return false;
        if (!this.hardwareInfo.equals(hardwareInfo))
            return false;
        this.isActivated = false;
        this.isExpired = true;
        return true;
    }

    public void addDuration(long durationTime) {
        this.durationTime += durationTime;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public boolean isExpired() {
        if (!isActivated)
            return false;
        return isExpired = Time.getFixedCurrentTime() > getExpireTime();
    }



    public void save(){
        new ObjectManager(licenceDirectory).save(key+".json", this);
    }

    public static LicenceKey load(String key) {
        String result = null;
        for (File file : Objects.requireNonNull(licenceDirectory.listFiles())) {
            if (file.getName().contains(key)) {
                result = file.getName();
                break;
            }
        }
        if (result == null) {
            return null;
        }
        return new ObjectManager(licenceDirectory).load(result, LicenceKey.class);
    }

    public static boolean exists(LicenceKey licenceKey) {
        return exists(licenceKey.getKey());
    }

    public static boolean delete(String key){
        return new File(licenceDirectory, key+".json").delete();
    }

    public static boolean exists(String licenceKey) {
        boolean result = false;
        for (File file : Objects.requireNonNull(licenceDirectory.listFiles())) {
            if (file.getName().contains(licenceKey)) {
                result = true;
                break;
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "LicenceKey{" +
                "\n\thardwareInfo=" + hardwareInfo +
                "\n\tkey='" + key + '\'' +
                "\n\tactivateTime=" + activateTime +
                "\n\tdurationTime=" + durationTime +
                "\n\texpireTime=" + expireTime +
                "\n\tisActivated=" + isActivated +
                "\n\tisExpired=" + isExpired +
                "\n}";
    }
}
