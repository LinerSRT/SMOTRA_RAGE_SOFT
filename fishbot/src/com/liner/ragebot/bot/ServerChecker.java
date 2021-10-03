package com.liner.ragebot.bot;

import com.liner.ragebot.jna.HardwareInfo;
import com.liner.ragebot.server.Server;
import com.liner.ragebot.server.models.LicenceKey;
import com.liner.ragebot.server.models.Notification;
import com.liner.ragebot.server.models.User;
import com.liner.ragebot.utils.Worker;

import java.util.concurrent.TimeUnit;

public class ServerChecker extends Worker {
    private BotContext botContext;
    private ServerCheckerCallback callback;

    public ServerChecker(BotContext botContext, ServerCheckerCallback callback) {
        this.botContext = botContext;
        this.callback = callback;
    }

    @Override
    public void execute() {
        Server.getUser(botContext.getSettings().getUserName(), new Server.UserCallback() {
            @Override
            public void onReceive(User user) {
                if (user.getBanned()) {
                    callback.onUserBanned(user);
                } else {
                    Server.getLicence(botContext.getSettings().getLicenceKey(), new Server.LicenceCallback() {
                        @Override
                        public void onReceive(LicenceKey licenceKey) {
                            HardwareInfo hardwareInfo = HardwareInfo.getHardware();
                            if (licenceKey.isActivated && hardwareInfo.equals(licenceKey.getHardwareInfo())) {
                                if (!licenceKey.isExpired) {
                                    for (Notification notification : user.getNotificationMessages()) {
                                        if (!notification.isRead()) {
                                            callback.onNewUserMessage(user);
                                            return;
                                        }
                                    }
                                    callback.onSuccess(user, licenceKey);
                                } else {
                                    callback.onKeyExpired(licenceKey);
                                }
                            } else if (licenceKey.isActivated && !hardwareInfo.equals(licenceKey.getHardwareInfo())) {
                                callback.onKeyHardwareMismatch(licenceKey);
                            } else if (!licenceKey.isActivated) {
                                callback.onKeyNotActivated(licenceKey);
                            }
                        }

                        @Override
                        public void onFailed(String reason) {
                            callback.onError();
                        }
                    });
                }
            }

            @Override
            public void onFailed(String reason) {
                callback.onError();
            }
        });
    }

    @Override
    public long delay() {
        return TimeUnit.SECONDS.toMillis(30);
    }
}
