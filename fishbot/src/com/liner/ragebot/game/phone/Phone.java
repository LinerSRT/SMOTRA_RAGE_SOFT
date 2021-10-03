package com.liner.ragebot.game.phone;

import com.liner.ragebot.bot.BotContext;
import com.liner.ragebot.utils.ImageUtils;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Phone {
    private final BotContext context;
    private boolean passiveModeEnabled;
    private boolean needWaitForPassive;
    private boolean executing = false;
    private int fallbackCount = 0;

    public Phone(BotContext context) {
        this.context = context;
        this.passiveModeEnabled = false;
        this.needWaitForPassive = false;
    }

    public void setPassiveModeEnabled(boolean enable, PassiveCallback passiveCallback) {
        if (executing || !context.isBotRunning())
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                executing = true;
                if (!context.getSettings().isDropBrokenRods()) {
                    passiveCallback.onFinish(enable);
                    return;
                }
                if (!isPhoneOpen()) {
                    fallbackCount++;
                    if(fallbackCount > 10){
                        context.stopBot();
                    }
                    context.getRageMultiplayer().pressKey(KeyEvent.VK_UP);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {
                    }
                    executing = false;
                    setPassiveModeEnabled(enable, passiveCallback);
                } else {
                    context.getRageMultiplayer().leftClick(1097, 325);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {
                    }
                    BufferedImage bufferedImage = context.getRageMultiplayer().getBuffer();
                    passiveModeEnabled = ImageUtils.isColorPresent(
                            bufferedImage,
                            1125, 640,
                            new Color(217, 56, 56)
                    );
                    if (enable && !passiveModeEnabled) {
                        if (needWaitForPassive) {
                            passiveCallback.onWait();
                            try {
                                Thread.sleep(TimeUnit.SECONDS.toMillis(30));
                            } catch (InterruptedException ignored) {
                            }
                            needWaitForPassive = false;
                        }
                        needWaitForPassive = true;
                        context.getRageMultiplayer().leftClick(1125, 653);
                        passiveModeEnabled = true;
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ignored) {
                        }
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                needWaitForPassive = false;
                            }
                        }, TimeUnit.SECONDS.toMillis(30));
                        executing = false;
                        fallbackCount = 0;
                        passiveCallback.onFinish(true);
                    } else if (!enable && passiveModeEnabled) {
                        if (needWaitForPassive) {
                            passiveCallback.onWait();
                            try {
                                Thread.sleep(TimeUnit.SECONDS.toMillis(30));
                            } catch (InterruptedException ignored) {
                            }
                            needWaitForPassive = false;
                        }
                        needWaitForPassive = true;
                        context.getRageMultiplayer().leftClick(1125, 653);
                        passiveModeEnabled = false;
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ignored) {
                        }
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                needWaitForPassive = false;
                            }
                        }, TimeUnit.SECONDS.toMillis(30));
                        executing = false;
                        fallbackCount = 0;
                        passiveCallback.onFinish(false);
                    } else {
                        context.getRageMultiplayer().pressKey(KeyEvent.VK_UP);
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException ignored) {
                        }
                        executing = false;
                        fallbackCount = 0;
                        passiveCallback.onFinish(passiveModeEnabled);
                    }
                }
            }
        }).start();
    }

    public void suicide() {
        if (!isPhoneOpen()) {
            context.getRageMultiplayer().pressKey(KeyEvent.VK_UP);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            suicide();
        } else {
            try {
                Thread.sleep(500);
                context.getRageMultiplayer().leftClick(1035, 324);
                Thread.sleep(100);
                context.getRageMultiplayer().leftClick(1100, 624);
                Thread.sleep(100);
            } catch (InterruptedException e) {
                context.getRageMultiplayer().pressKey(KeyEvent.VK_UP);
            }
        }
    }

    public interface PassiveCallback {
        void onFinish(boolean enabled);

        void onWait();
    }

    public boolean isPhoneOpen() {
        BufferedImage bufferedImage = context.getRageMultiplayer().getBuffer();
        return ImageUtils.isColorPresent(bufferedImage,
                1200, 285,
                new Color(0, 181, 68)
        ) && ImageUtils.isColorPresent(bufferedImage,
                1180, 285,
                new Color(37, 37, 37)
        ) && ImageUtils.isColorPresent(bufferedImage,
                1005, 285,
                new Color(108, 87, 106)
        );
    }


    public boolean isPassiveModeEnabled() {
        return passiveModeEnabled;
    }

}
