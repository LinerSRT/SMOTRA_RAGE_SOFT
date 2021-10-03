package com.liner.ragebot.game;

import com.liner.ragebot.Core;
import com.liner.ragebot.Settings;
import com.liner.ragebot.bot.BotContext;
import com.liner.ragebot.bot.BotStatusUpdater;
import com.liner.ragebot.game.captcha.CaptchaSolver;
import com.liner.ragebot.game.captcha.CaptchaThread;
import com.liner.ragebot.game.inventory.Inventory;
import com.liner.ragebot.game.phone.Phone;
import com.liner.ragebot.game.state.GameState;
import com.liner.ragebot.game.state.GameThread;
import com.liner.ragebot.jna.JNAUtils;
import com.liner.ragebot.messages.MessageConfig;
import com.liner.ragebot.messages.MessageForm;
import com.liner.ragebot.messages.MessagePosition;
import com.liner.ragebot.messages.MessageType;
import com.liner.ragebot.utils.ImageUtils;
import com.sun.jna.platform.win32.WinUser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.liner.ragebot.jna.JNAUtils.IUSER32;

public class Bot implements CaptchaThread.CaptchaCallback, GameThread.GameCallback {
    private final BotContext context;
    private boolean isRunning = false;
    private boolean isCaptcha = false;
    private boolean isFishing = false;
    private boolean isExecuting = false;
    //private boolean isInventoryChecking = false;
    //private boolean isStartedSuccess = false;

    private final Timer timer;
    private BotState botState;
    private int fishCatchedCount;
    private int nextInventoryCheckCount;
    private BotStatusUpdater botStatusUpdater;
    private final CaptchaThread captchaThread;
    private final GameThread gameThread;
    //private final Inventory inventory;
    private final Phone phone;

    public Bot(BotContext context, BotStatusUpdater botStatusUpdater) {
        this.botStatusUpdater = botStatusUpdater;
        this.fishCatchedCount = 0;
        this.botState = BotState.INITIALIZATION;
        this.context = context;
        timer = new Timer();
        captchaThread = new CaptchaThread(context.getSelectedInterface(), this);
        gameThread = new GameThread(context.getRageMultiplayer(), this);
        //inventory = new Inventory(context);
        phone = new Phone(context);
    }


    public void start() {
        if (!isRunning) {
            isCaptcha = false;
            isExecuting = false;
            isFishing = false;
            //isStartedSuccess = true;
            //isInventoryChecking = false;
            fishCatchedCount = 0;
            botState = BotState.SELECTING_INTERFACE;
            botState = BotState.SELECTING_WINDOW;
            captchaThread.setNetworkInterface(context.getSelectedInterface());
            captchaThread.start();
            gameThread.start();
            if (!IUSER32.GetForegroundWindow().equals(context.getRageMultiplayer().getHwnd())) {
                JNAUtils.showWindow(context.getRageMultiplayer().getHwnd());
            }
            isRunning = true;
            botState = BotState.STARTED;

            new Thread(() -> {
                MessageForm messageForm = new MessageForm(
                        new MessageConfig.Builder()
                                .setMessageText("Бот был успешно запущен")
                                .setMessageTitle("Запущено")
                                .setMessageIcon(Core.Icon.baitIcon)
                                .setMessageType(MessageType.FINISH)
                                .setMessagePosition(MessagePosition.TOP_RIGHT)
                                .build()
                );
                messageForm.show();
                messageForm.playSound(Core.Sound.notification);
                messageForm.closeAfter(2000);
            }).start();
        }
    }

    public void stop() {
        if (isRunning) {
            isRunning = false;
            if (captchaThread != null)
                captchaThread.stop();
            if (gameThread != null)
                gameThread.stop();
            timer.cancel();
            fishCatchedCount = 0;
            botState = BotState.STOPPED;
            new Thread(() -> {
                MessageForm messageForm = new MessageForm(
                        new MessageConfig.Builder()
                                .setMessageText("Бот был успешно остановлен")
                                .setMessageTitle("Остановлено")
                                .setMessageIcon(Core.Icon.baitIcon)
                                .setMessageType(MessageType.ERROR)
                                .setMessagePosition(MessagePosition.TOP_RIGHT)
                                .build()
                );
                messageForm.show();
                messageForm.playSound(Core.Sound.notification);
                messageForm.closeAfter(2000);
            }).start();
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void onCaptcha(String data) {
        isCaptcha = true;
        botState = BotState.CAPTCHA_START;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
        Rectangle windowDimension = context.getRageMultiplayer().updateLocation();
        BufferedImage colored = context.getRageMultiplayer().getBuffer();
        BufferedImage buffer = ImageUtils.grayScaleImage(colored);
        ImageSearch search = new ImageSearch(buffer);
        ImageBounds bounds = null;
        for (BufferedImage captcha : Core.Captcha.images) {
            bounds = search.find(captcha);
            if (bounds != null)
                break;
        }
        if (bounds != null) {
            int captchaX = (windowDimension.x + (bounds.x + bounds.width / 2));
            int captchaY = (windowDimension.y + (bounds.y + bounds.height / 2));
            context.getRageMultiplayer().leftClick(captchaX - 93, captchaY + 133);
            CaptchaSolver.inputCaptcha(context.getRageMultiplayer().getHwnd(), data);
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
            context.getRageMultiplayer().leftClick(captchaX - 93, captchaY + 209);
            botState = BotState.CAPTCHA_FINISH;
            try {
                Thread.sleep(300);
            } catch (InterruptedException ignored) {
            }
            isCaptcha = false;
        } else {
            ImageBounds imageBounds = getCaptchaButtonCenterCoordinates(colored);
            context.getRageMultiplayer().leftClick(imageBounds.getCenterX(), imageBounds.getCenterY() - 60);
            CaptchaSolver.inputCaptcha(context.getRageMultiplayer().getHwnd(), data);
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
            context.getRageMultiplayer().leftClick(imageBounds.getCenterX(), imageBounds.getCenterY());
            botState = BotState.CAPTCHA_FINISH;
            try {
                Thread.sleep(300);
            } catch (InterruptedException ignored) {
            }
            isCaptcha = false;
        }
    }


    @Override
    public void onChanged(GameThread gameThread, GameState gameState) {
        if (isCaptcha || isExecuting)
            return;
//        if (isInventoryChecking) {
//            isExecuting = true;
//            System.out.println("Start inventory check");
//            botState = BotState.PASSIVE_MODE_OFF;
//            System.out.println("Disabling passive mode");
//            if (context.getSettings().isDropBrokenRods()) {
//                setPassiveMode(false, new Phone.PassiveCallback() {
//                    @Override
//                    public void onFinish(boolean enabled) {
//                        System.out.println("Passive mode disabled");
//                        botState = BotState.INVENTORY_CHECK;
//                        try {
//                            Thread.sleep(500);
//                        } catch (InterruptedException ignored) {
//                        }
//                        checkInventory();
//                    }
//
//                    @Override
//                    public void onWait() {
//                        botState = BotState.PASSIVE_MODE_WAIT;
//                    }
//                });
//            } else {
//                checkInventory();
//            }
//
//        } else {
            switch (gameState) {
                case WAITING:
                    botState = BotState.WAITING;
                    if (!isFishing) {
                        execute(() -> {
                            context.getRageMultiplayer().pressKey(KeyEvent.VK_E);
//                            if (isStartedSuccess) {
//                                isStartedSuccess = false;
//                                new Timer().schedule(new TimerTask() {
//                                    @Override
//                                    public void run() {
//                                        context.getRageMultiplayer().leftClick(987, 290);
//                                        try {
//                                            Thread.sleep(500);
//                                        } catch (InterruptedException ignored) {
//                                        }
//                                        if (!isStartedSuccess)
//                                            isInventoryChecking = true;
//                                    }
//                                }, TimeUnit.SECONDS.toMillis(11));
//                            }
                        }, 500);
                    }
                    break;
                case SELECTING_BAIT:
                    //if (isStartedSuccess) {
                    //    isStartedSuccess = false;
                    //}
                    botState = BotState.SELECTING_BAIT;
                    execute(() -> {
                        context.getRageMultiplayer().leftClick((350 + (context.getSettings().getBaitIndex() * 150)), 400);
                    }, 200);
                    break;
                case THROWING_ROD:
                    botState = BotState.THROW_ROD;
                    isFishing = true;
                    execute(() -> {
                        context.getRageMultiplayer().leftClick(575, 420);
                    }, 0);
                    break;
                case WAITING_FISH:
                    botState = BotState.WAITING_FISH;
                    //if (!isStartedSuccess)
                    //    isStartedSuccess = true;
                    break;
                case PICKING_FISH:
                    botState = BotState.PICKING_FISH;
                    execute(() -> context.getRageMultiplayer().pressKey(KeyEvent.VK_E), 100);
                    break;
                case PICKING_QTE_E:
                    execute(() -> context.getRageMultiplayer().pressKey(KeyEvent.VK_E), 8);
                    break;
                case PICKING_QTE_Q:
                    execute(() -> context.getRageMultiplayer().pressKey(KeyEvent.VK_Q), 8);
                    break;
                case FINISH:
                    botState = BotState.FISHING_FINISH;
                    execute(() -> {
                        isCaptcha = false;
                        isExecuting = false;
                        isFishing = false;
                        //isStartedSuccess = true;
                        //isInventoryChecking = false;
                        fishCatchedCount++;
                        int period;
                        if (context.getSettings().getInventoryCheckPeriod() != 0) {
                            switch (context.getSettings().getInventoryCheckPeriod()) {
                                case 1:
                                    period = 5;
                                    break;
                                case 2:
                                    period = 10;
                                    break;
                                case 3:
                                    period = 20;
                                    break;
                                case 4:
                                    period = 50;
                                    break;
                                case 5:
                                    period = 100;
                                    break;
                                default:
                                    period = 1000;
                            }
                            switch (context.getSettings().getInventoryCheckPeriod()) {
                                case 1:
                                    nextInventoryCheckCount = (5 - (fishCatchedCount % 5));
                                    break;
                                case 2:
                                    nextInventoryCheckCount = (10 - (fishCatchedCount % 10));
                                    break;
                                case 3:
                                    nextInventoryCheckCount = (20 - (fishCatchedCount % 20));
                                    break;
                                case 4:
                                    nextInventoryCheckCount = (50 - (fishCatchedCount % 50));
                                    break;
                                case 5:
                                    nextInventoryCheckCount = (100 - (fishCatchedCount % 100));
                                    break;
                                default:
                                    nextInventoryCheckCount = (9999 - (fishCatchedCount % 9999));
                            }
                            //isInventoryChecking = fishCatchedCount % period == 0;
                        } else {
                            //isInventoryChecking = false;
                            nextInventoryCheckCount = (9999 - (fishCatchedCount % 9999));
                        }
                    }, TimeUnit.SECONDS.toMillis(7));
                    break;
                case FAIL:
                    botState = BotState.FISHING_FAIL;
                    execute(() -> {
                        isCaptcha = false;
                        isExecuting = false;
                        isFishing = false;
                        //isStartedSuccess = true;
                        //isInventoryChecking = false;
                        int period;
                        if (context.getSettings().getInventoryCheckPeriod() != 0) {
                            switch (context.getSettings().getInventoryCheckPeriod()) {
                                case 1:
                                    period = 5;
                                    break;
                                case 2:
                                    period = 10;
                                    break;
                                case 3:
                                    period = 20;
                                    break;
                                case 4:
                                    period = 50;
                                    break;
                                case 5:
                                    period = 100;
                                    break;
                                default:
                                    period = 1000;
                            }
                            switch (context.getSettings().getInventoryCheckPeriod()) {
                                case 1:
                                    nextInventoryCheckCount = (5 - (fishCatchedCount % 5));
                                    break;
                                case 2:
                                    nextInventoryCheckCount = (10 - (fishCatchedCount % 10));
                                    break;
                                case 3:
                                    nextInventoryCheckCount = (20 - (fishCatchedCount % 20));
                                    break;
                                case 4:
                                    nextInventoryCheckCount = (50 - (fishCatchedCount % 50));
                                    break;
                                case 5:
                                    nextInventoryCheckCount = (100 - (fishCatchedCount % 100));
                                    break;
                                default:
                                    nextInventoryCheckCount = (9999 - (fishCatchedCount % 9999));
                            }
                            //isInventoryChecking = fishCatchedCount % period == 0;
                        } else {
                            //isInventoryChecking = false;
                            nextInventoryCheckCount = (9999 - (fishCatchedCount % 9999));
                        }
                    }, TimeUnit.SECONDS.toMillis(7));
                    break;
            }
            botStatusUpdater.updateStatus();
        //}
    }

    @Override
    public Settings getSettings() {
        return context.getSettings();
    }

    private void setPassiveMode(boolean value, Phone.PassiveCallback passiveMode) {
        botState = BotState.PASSIVE_MODE_OFF;
        phone.setPassiveModeEnabled(value, passiveMode);
    }

    private void checkInventory() {
//        inventory.checkInventory(new Inventory.InventoryCallback() {
//            @Override
//            public void onFinish() {
//                System.out.println("Check inventory finished");
//                context.getRageMultiplayer().pressKey(KeyEvent.VK_I);
//                System.out.println("Enable passive mode");
//                if (context.getSettings().isDropBrokenRods()) {
//                    setPassiveMode(true, new Phone.PassiveCallback() {
//                        @Override
//                        public void onFinish(boolean enabled) {
//                            botState = BotState.WAITING;
//                            isCaptcha = false;
//                            isExecuting = false;
//                            isFishing = false;
//                            isStartedSuccess = true;
//                            isInventoryChecking = false;
//                        }
//
//                        @Override
//                        public void onWait() {
//                            botState = BotState.PASSIVE_MODE_WAIT;
//                        }
//                    });
//                } else {
//                    isCaptcha = false;
//                    isExecuting = false;
//                    isFishing = false;
//                    isStartedSuccess = true;
//                    isInventoryChecking = false;
//                }
//            }
//
//            @Override
//            public void onNoRods() {
//                System.out.println("onNoRods");
//                context.getRageMultiplayer().pressKey(KeyEvent.VK_I);
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                MessageForm messageForm = new MessageForm(
//                        new MessageConfig.Builder()
//                                .setMessageText("Бот был успешно остановлен")
//                                .setMessageTitle("В инвенаре не найдено удочек")
//                                .setMessageIcon(Core.Icon.baitIcon)
//                                .setMessageType(MessageType.ERROR)
//                                .setMessagePosition(MessagePosition.TOP_RIGHT)
//                                .build()
//                );
//                messageForm.show();
//                messageForm.playSound(Core.Sound.notification);
//                messageForm.closeAfter(2000);
//            }
//
//            @Override
//            public void onNoSelectedRods() {
//                System.out.println("onNoSelectedRods");
//                context.getRageMultiplayer().pressKey(KeyEvent.VK_I);
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                MessageForm messageForm = new MessageForm(
//                        new MessageConfig.Builder()
//                                .setMessageText("Бот был успешно остановлен")
//                                .setMessageTitle("В инвентаре не найдено выбранных удочек")
//                                .setMessageIcon(Core.Icon.baitIcon)
//                                .setMessageType(MessageType.ERROR)
//                                .setMessagePosition(MessagePosition.TOP_RIGHT)
//                                .build()
//                );
//                messageForm.show();
//                messageForm.playSound(Core.Sound.notification);
//                messageForm.closeAfter(2000);
//                if (context.getSettings().isStopBotIfNoRodsBaits()) {
//                    context.stopBot();
//                } else if (context.getSettings().isCloseGameIfNoRodsBaits()) {
//                    context.stopBot();
//                    IUSER32.PostMessage(
//                            context.getRageMultiplayer().getHwnd(),
//                            WinUser.WM_CLOSE,
//                            null,
//                            null
//                    );
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    context.getRageMultiplayer().pressKey(KeyEvent.VK_ENTER);
//                    System.exit(0);
//                } else if (context.getSettings().isSuicideIfNoRodsBaits()) {
//                    context.stopBot();
//                    phone.suicide();
//                }
//            }
//
//            @Override
//            public void onNoBaits() {
//                System.out.println("onNoBaits");
//                context.getRageMultiplayer().pressKey(KeyEvent.VK_I);
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                MessageForm messageForm = new MessageForm(
//                        new MessageConfig.Builder()
//                                .setMessageText("Бот был успешно остановлен")
//                                .setMessageTitle("В инвентаре не найдено наживок")
//                                .setMessageIcon(Core.Icon.baitIcon)
//                                .setMessageType(MessageType.ERROR)
//                                .setMessagePosition(MessagePosition.TOP_RIGHT)
//                                .build()
//                );
//                messageForm.show();
//                messageForm.playSound(Core.Sound.notification);
//                messageForm.closeAfter(2000);
//                if (context.getSettings().isStopBotIfNoRodsBaits()) {
//                    context.stopBot();
//                } else if (context.getSettings().isCloseGameIfNoRodsBaits()) {
//                    context.stopBot();
//                    IUSER32.PostMessage(
//                            context.getRageMultiplayer().getHwnd(),
//                            WinUser.WM_CLOSE,
//                            null,
//                            null
//                    );
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    context.getRageMultiplayer().pressKey(KeyEvent.VK_ENTER);
//                    System.exit(0);
//                } else if (context.getSettings().isSuicideIfNoRodsBaits()) {
//                    context.stopBot();
//                    phone.suicide();
//                }
//            }
//
//            @Override
//            public void onNoSelectedBaits() {
//                System.out.println("onNoSelectedBaits");
//                context.getRageMultiplayer().pressKey(KeyEvent.VK_I);
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                MessageForm messageForm = new MessageForm(
//                        new MessageConfig.Builder()
//                                .setMessageText("Бот был успешно остановлен")
//                                .setMessageTitle("В инвенатре не найдено выбранных наживок")
//                                .setMessageIcon(Core.Icon.baitIcon)
//                                .setMessageType(MessageType.ERROR)
//                                .setMessagePosition(MessagePosition.TOP_RIGHT)
//                                .build()
//                );
//                messageForm.show();
//                messageForm.playSound(Core.Sound.notification);
//                messageForm.closeAfter(2000);
//                if (context.getSettings().isStopBotIfNoRodsBaits()) {
//                    context.stopBot();
//                } else if (context.getSettings().isCloseGameIfNoRodsBaits()) {
//                    context.stopBot();
//                    IUSER32.PostMessage(
//                            context.getRageMultiplayer().getHwnd(),
//                            WinUser.WM_CLOSE,
//                            null,
//                            null
//                    );
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    context.getRageMultiplayer().pressKey(KeyEvent.VK_ENTER);
//                    System.exit(0);
//                } else if (context.getSettings().isSuicideIfNoRodsBaits()) {
//                    context.stopBot();
//                    phone.suicide();
//                }
//            }
//
//            @Override
//            public void onNoHaveEmptySlots() {
//                System.out.println("onNoHaveEmptySlots");
//                context.getRageMultiplayer().pressKey(KeyEvent.VK_I);
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                MessageForm messageForm = new MessageForm(
//                        new MessageConfig.Builder()
//                                .setMessageText("Бот был успешно остановлен")
//                                .setMessageTitle("е найдено пустых слотов в инвентаре")
//                                .setMessageIcon(Core.Icon.baitIcon)
//                                .setMessageType(MessageType.ERROR)
//                                .setMessagePosition(MessagePosition.TOP_RIGHT)
//                                .build()
//                );
//                messageForm.show();
//                messageForm.playSound(Core.Sound.notification);
//                messageForm.closeAfter(2000);
//                if (context.getSettings().isStopBotIfNoRodsBaits()) {
//                    context.stopBot();
//                } else if (context.getSettings().isCloseGameIfNoRodsBaits()) {
//                    context.stopBot();
//                    IUSER32.PostMessage(
//                            context.getRageMultiplayer().getHwnd(),
//                            WinUser.WM_CLOSE,
//                            null,
//                            null
//                    );
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    context.getRageMultiplayer().pressKey(KeyEvent.VK_ENTER);
//                    System.exit(0);
//                } else if (context.getSettings().isSuicideIfNoRodsBaits()) {
//                    context.stopBot();
//                    phone.suicide();
//                }
//            }
//
//            @Override
//            public void failedChooseRod() {
//                System.out.println("failedChooseRod");
//                context.getRageMultiplayer().pressKey(KeyEvent.VK_I);
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                MessageForm messageForm = new MessageForm(
//                        new MessageConfig.Builder()
//                                .setMessageText("Бот был успешно остановлен")
//                                .setMessageTitle("Бот не смог выбрать удочку для рыбалки")
//                                .setMessageIcon(Core.Icon.baitIcon)
//                                .setMessageType(MessageType.ERROR)
//                                .setMessagePosition(MessagePosition.TOP_RIGHT)
//                                .build()
//                );
//                messageForm.show();
//                messageForm.playSound(Core.Sound.notification);
//                messageForm.closeAfter(2000);
//                if (context.getSettings().isStopBotIfNoRodsBaits()) {
//                    context.stopBot();
//                } else if (context.getSettings().isCloseGameIfNoRodsBaits()) {
//                    context.stopBot();
//                    IUSER32.PostMessage(
//                            context.getRageMultiplayer().getHwnd(),
//                            WinUser.WM_CLOSE,
//                            null,
//                            null
//                    );
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    context.getRageMultiplayer().pressKey(KeyEvent.VK_ENTER);
//                    System.exit(0);
//                } else if (context.getSettings().isSuicideIfNoRodsBaits()) {
//                    context.stopBot();
//                    phone.suicide();
//                }
//            }
//        });
    }

    private void execute(Runnable runnable, long timeOut) {
        isExecuting = true;
        runnable.run();
        try {
            Thread.sleep(timeOut);
        } catch (InterruptedException ignored) {
        }
        isExecuting = false;
    }

    public BotState getBotState() {
        return botState;
    }

    public int getFishCatchedCount() {
        return fishCatchedCount;
    }

    public int getNextInventoryCheckCount() {
        return nextInventoryCheckCount;
    }


    public static ImageBounds getCaptchaButtonCenterCoordinates(BufferedImage buffer) {
        List<ImageBounds> coordinateList = new ArrayList<>();
        int bufferWidth = buffer.getWidth();
        int bufferHeight = buffer.getHeight();
        int buttonWidth = 226;
        int buttonHeight = 21;
        for (int y = 10; y < bufferHeight; y++) {
            if (y + buttonHeight >= bufferHeight)
                break;
            for (int x = 10; x < bufferWidth; x++) {
                if (x + buttonWidth >= bufferWidth)
                    break;
                int topLeft = buffer.getRGB(x, y);
                int topRight = buffer.getRGB(x + buttonWidth, y);
                int bottomRight = buffer.getRGB(x + buttonWidth, y + buttonHeight);
                int bottomLeft = buffer.getRGB(x, y + buttonHeight);
                if (
                        topLeft == topRight &&
                                topRight == bottomRight &&
                                bottomRight == bottomLeft && !ImageUtils.isWhite(new Color(topLeft))
                ) {
                    coordinateList.add(new ImageBounds(x, y, buttonWidth, buttonHeight));
                }
            }
        }
        coordinateList.sort(Comparator.comparingInt(ImageBounds::getY));
        return coordinateList.get(coordinateList.size() - 1);
    }
}
