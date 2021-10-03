package com.liner.fishbotserver.data;

import com.liner.fishbotserver.ObjectManager;
import com.liner.fishbotserver.utilities.Time;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class User {
    public static final File usersDirectory = new File(System.getProperty("user.dir"), "bot_users");
    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
    private final HardwareInfo hardwareInfo;
    private String gameUsername;
    private boolean haveAdminRights;
    private boolean havePremiumRights;
    private boolean isBanned;
    private byte[] banReason;
    private boolean isOnline;
    private String lastOnlineTime;
    private final List<PromoCode> promoCodes;
    private final List<String> licenceKeys;
    private final List<Notification> notificationMessages;
    private Comment comment;

    public User(String gameUsername, HardwareInfo hardwareInfo) {
        this.hardwareInfo = hardwareInfo;
        this.gameUsername = gameUsername;
        this.haveAdminRights = false;
        this.havePremiumRights = false;
        this.isBanned = false;
        this.banReason = new byte[]{};
        this.isOnline = false;
        this.lastOnlineTime = simpleDateFormat.format(new Date(Time.getFixedCurrentTime()));
        this.promoCodes = new ArrayList<>();
        this.licenceKeys = new ArrayList<>();
        this.notificationMessages = new ArrayList<>();
        this.comment = null;
    }


    public HardwareInfo getHardwareInfo() {
        return hardwareInfo;
    }

    public List<Notification> getNotificationMessages() {
        return notificationMessages;
    }

    public List<PromoCode> getPromoCodes() {
        return promoCodes;
    }

    public List<String> getLicenceKeys() {
        return licenceKeys;
    }

    public String getUsername() {
        return gameUsername;
    }

    public void setGameUsername(String gameUsername) {
        this.gameUsername = gameUsername;
    }

    public boolean isHaveAdminRights() {
        return haveAdminRights;
    }

    public void setHaveAdminRights(boolean haveAdminRights) {
        this.haveAdminRights = haveAdminRights;
    }

    public boolean isHavePremiumRights() {
        return havePremiumRights;
    }

    public void setHavePremiumRights(boolean havePremiumRights) {
        this.havePremiumRights = havePremiumRights;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned, byte[] banReason) {
        this.isBanned = banned;
        this.banReason = banReason;
    }

    public byte[] getBanReason() {
        return banReason;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getLastOnlineTime() {
        return lastOnlineTime;
    }

    public void setLastOnlineTime(String lastOnlineTime) {
        this.lastOnlineTime = lastOnlineTime;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public Comment getComment() {
        return comment;
    }

    public boolean addNotification(Notification notification) {
        for (Notification notification1 : notificationMessages)
            if (notification1.getId().equals(notification.getId()))
                return false;
        notificationMessages.add(notification);
        return true;
    }

    public void removeNotification(String notificationID) {
        notificationMessages.removeIf(notification -> notification.getId().equals(notificationID));
    }

    public boolean setNotificationRead(boolean value, String notificationID) {
        for (Notification notification : notificationMessages) {
            if (notification.getId().equals(notificationID)) {
                notification.setRead(value);
                return true;
            }
        }
        return false;
    }

    public String getNotificationIds() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Notification notification : notificationMessages) {
            stringBuilder.append(notification.getId()).append("|");
        }
        return stringBuilder.toString();
    }

    public Notification getNotification(String notificationID) {
        for (Notification notification : notificationMessages) {
            if (notification.getId().equals(notificationID)) {
                return notification;
            }
        }
        return null;
    }

    public void addPromoCode(PromoCode promoCode) {
        if (!promoCodes.contains(promoCode))
            promoCodes.add(promoCode);
    }

    public void removePromoCode(PromoCode promoCode){
        promoCodes.remove(promoCode);
    }

    public boolean isPromoCodeValid(PromoCode promo){
        boolean contain = false;
        boolean activated = false;
        for(PromoCode promoCode:promoCodes){
            if(promoCode.getPromoCode().equals(promo.getPromoCode())){
                contain = true;
                activated = promoCode.isActivated();
            }
        }
        return contain && !activated;
    }

    public boolean activatePromo(PromoCode promo){
        if(isPromoCodeValid(promo)){
            for(PromoCode promoCode:promoCodes){
                if(promoCode.getPromoCode().equals(promo.getPromoCode())){
                    promoCode.setActivated(true);
                    return true;
                }
            }
        }
        return false;
    }


    public void save() {
        new ObjectManager(usersDirectory).save(gameUsername + ".json", this);
    }

    public static User loadByHardware(HardwareInfo hardwareInfo) {
        for (File file : Objects.requireNonNull(usersDirectory.listFiles())) {
            User user = new ObjectManager(usersDirectory).load(file.getName(), User.class);
            if (user.getHardwareInfo().equals(hardwareInfo)) {
                return user;
            }
        }
        return null;
    }

    public static User load(String gameUsername) {
        String result = null;
        for (File file : Objects.requireNonNull(usersDirectory.listFiles())) {
            if (file.getName().contains(gameUsername)) {
                result = file.getName();
                break;
            }
        }
        if (result == null) {
            return null;
        }
        return new ObjectManager(usersDirectory).load(result, User.class);
    }

    public static boolean exists(User user) {
        return exists(user.getUsername());
    }

    public static boolean exists(String username) {
        boolean result = false;
        for (File file : Objects.requireNonNull(usersDirectory.listFiles())) {
            if (file.getName().contains(username)) {
                result = true;
                break;
            }
        }
        return result;
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
