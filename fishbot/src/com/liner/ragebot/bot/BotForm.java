package com.liner.ragebot.bot;

import com.liner.ragebot.Core;
import com.liner.ragebot.Settings;
import com.liner.ragebot.auth.AuthForm;
import com.liner.ragebot.checkers.InterfaceCheck;
import com.liner.ragebot.game.Bot;
import com.liner.ragebot.game.SelectorItem;
import com.liner.ragebot.jna.KeyboardHook;
import com.liner.ragebot.jna.RageMultiplayer;
import com.liner.ragebot.messages.MessageConfig;
import com.liner.ragebot.messages.MessageForm;
import com.liner.ragebot.messages.MessagePosition;
import com.liner.ragebot.messages.MessageType;
import com.liner.ragebot.server.models.LicenceKey;
import com.liner.ragebot.server.models.User;
import com.liner.ragebot.ui.CircleProgressBar;
import com.liner.ragebot.ui.DialogMouseListener;
import com.liner.ragebot.ui.LinerButton;
import com.liner.ragebot.ui.LinerComboBox;
import com.liner.ragebot.utils.Other;
import com.liner.ragebot.utils.Worker;
import org.pcap4j.core.PcapNetworkInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Timer;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"unchecked", "rawtypes"})
public class BotForm implements BotContext, KeyboardHook.KeyCallback, ServerCheckerCallback, BotStatusUpdater {
    private static JFrame frame;
    private final Settings settings;
    private LinerButton discordButton;
    private LinerButton telegramButton;
    private JPanel dragPanel;
    private JPanel panel;
    private LinerButton closeButton;
    private JLabel appTitle;
    private CircleProgressBar statusProgress;
    private JLabel statusText;
    private LinerButton logOutButton;
    private LinerButton funpayButton;
    private JLabel keyDuration;
    private JLabel fishCatched;
    private JLabel nextInventoryCheckCount;
    private JLabel fishingTime;
    private JProgressBar fishingTimeProgress;
    private JLabel fishingTimeOutTime;
    private JProgressBar fishingTimeOutProgress;
    private JLabel gameHookText;
    private LinerButton gameHook;
    private JCheckBox useAnyRods;
    private JCheckBox useAnyBaits;
    private JCheckBox autoCloseGame;
    private JCheckBox autoStopBot;
    private JCheckBox autoSuicide;
    private JCheckBox dropBrokenRods;
    private JCheckBox qteFix;
    private JComboBox chooseBait;
    private JLabel baitText;
    private JComboBox interfaceSelect;
    private JLabel interfaceText;
    private JComboBox chooseRod;
    private JLabel rodText;
    private JComboBox fishingTimeSelector;
    private JLabel fishingTimeText;
    private JComboBox fishingTimeoutSelector;
    private JLabel fishingTimeoutText;
    private JComboBox checkInventorySelector;
    private JLabel checkInventoryText;
    private JPanel botStatusPanel;
    private JPanel botSettingsPanel;
    private LinerButton checkHook;
    private LinerButton startBot;
    private LinerButton stopBot;
    private JLabel initMessage;
    private JPanel botControlPanel;
    private User user;
    private LicenceKey licenceKey;


    public Bot bot;
    private final ServerChecker serverChecker;
    private RageMultiplayer rageMultiplayer;
    public PcapNetworkInterface pcapNetworkInterface;
    private List<PcapNetworkInterface> networkInterfaceList;
    private static final File wpCapFile = new File("C:\\Windows\\System32\\wpcap.dll");
    public long startBotTime = 0;
    public long fishingMinutes = 0;
    public long fishingTimeoutMinutes = 0;
    public Worker fishingTimeWorker;
    public Worker fishingTimeoutWorker;

    public BotForm(User user, LicenceKey licenceKey) {
        KeyboardHook.getInstance().subscribe(this);
        this.user = user;
        this.licenceKey = licenceKey;
        if (user == null || licenceKey == null)
            System.exit(0);
        settings = Settings.load();
        createUI();
        initAction();
        serverChecker = new ServerChecker(this, this);
        keyDuration.setText(
                licenceKey.getKey() + "  до: " +
                        new SimpleDateFormat("HH:mm dd.MM").format(
                                new Date(Other.getFixedTime(licenceKey.getExpireTime()))
                        )
        );
        botStatusPanel.setVisible(false);
        botSettingsPanel.setVisible(false);
        botControlPanel.setVisible(false);
        showStatus("Инициализация", Color.WHITE, null, true);
        if (!wpCapFile.exists()) {
            botControlPanel.setVisible(false);
            showStatus("Ошибка установки", Color.RED, Color.RED, true);
            initMessage.setVisible(true);
            initMessage.setText("У вас не установлен WinPcap, выход через 10с!");
            initMessage.setForeground(Color.YELLOW);
            frame.pack();
            new Timer().scheduleAtFixedRate(new TimerTask() {
                int count = 10;

                @Override
                public void run() {
                    initMessage.setText("У вас не установлен WinPcap, выход через " + count + "с!");
                    if (count <= 0) {
                        System.exit(0);
                    }
                    count--;
                }
            }, 0, TimeUnit.SECONDS.toMillis(1));
        }
        showStatus("Определение интерфейсов", Color.WHITE, null, true);
        detectInterface(new InterfaceDetectCallback() {
            @Override
            public void onDetected(List<PcapNetworkInterface> networkInterfaces) {
                showStatus("Ожидание действий", Color.WHITE, null, true);
                networkInterfaceList = networkInterfaces;
                for (int i = 0; i < networkInterfaces.size(); i++) {
                    PcapNetworkInterface networkInterface = networkInterfaces.get(i);
                    interfaceSelect.addItem(new SelectorItem(networkInterface.getDescription(), i));
                }
                botSettingsPanel.setVisible(true);
                botControlPanel.setVisible(true);
                frame.pack();
                LinerComboBox.setListener(interfaceSelect, index -> {
                    if (index != -1) {
                        if (networkInterfaceList == null)
                            networkInterfaceList = new ArrayList<>();
                        pcapNetworkInterface = networkInterfaceList.get(index);
                    } else {
                        interfaceSelect.firePopupMenuCanceled();
                    }
                });
                serverChecker.start();
            }

            @Override
            public void onDetected(PcapNetworkInterface networkInterface) {
                if (networkInterface.getDescription().contains("Windscribe") || networkInterface.getDescription().contains("wind")) {
                    onWindscribeDetected();
                    return;
                }
                pcapNetworkInterface = networkInterface;
                interfaceSelect.setSelectedIndex(networkInterfaceList.indexOf(networkInterface));
            }

            @Override
            public void onWindscribeDetected() {
                botControlPanel.setVisible(false);
                showStatus("Обнаружен несовместимый VPN", Color.YELLOW, Color.YELLOW, true);
                initMessage.setVisible(true);
                initMessage.setText("У вас обнаружен VPN Windscribe! Выход через 10с!");
                initMessage.setForeground(Color.YELLOW);
                frame.pack();
                new Timer().scheduleAtFixedRate(new TimerTask() {
                    int count = 10;

                    @Override
                    public void run() {
                        initMessage.setText("У вас обнаружен VPN Windscribe! Выход через " + count + "с!");
                        if (count <= 0) {
                            System.exit(0);
                        }
                        count--;
                    }
                }, 0, TimeUnit.SECONDS.toMillis(1));
            }

            @Override
            public void onError() {
                botControlPanel.setVisible(false);
                showStatus("Ошибка определения интерфейсов", Color.RED, Color.RED, true);
                initMessage.setVisible(true);
                initMessage.setText("Произошла непредвиденная ошибка! Выход через 10с!");
                initMessage.setForeground(Color.RED);
                frame.pack();
                new Timer().scheduleAtFixedRate(new TimerTask() {
                    int count = 10;

                    @Override
                    public void run() {
                        initMessage.setText("Произошла непредвиденная ошибка! Выход через " + count + "с!");
                        if (count <= 0) {
                            System.exit(0);
                        }
                        count--;
                    }
                }, 0, TimeUnit.SECONDS.toMillis(1));
            }
        });
    }

    private void detectInterface(InterfaceDetectCallback callback) {
        new Thread(() -> {
            rageMultiplayer = new RageMultiplayer();
            new InterfaceCheck(new InterfaceCheck.Callback() {
                @Override
                public void onInterfaceDetected(PcapNetworkInterface networkInterface) {
                    callback.onDetected(networkInterface);
                }

                @Override
                public void onInterfaceCollected(List<PcapNetworkInterface> networkInterfaces) {
                    callback.onDetected(networkInterfaces);
                }

                @Override
                public void onFailed() {
                    callback.onError();
                }
            }).check();
        }).start();
    }


    @Override
    public void onSuccess(User user, LicenceKey licenceKey) {
        this.user = user;
        this.licenceKey = licenceKey;
        keyDuration.setText(
                licenceKey.getKey() + "  до: " +
                        new SimpleDateFormat("HH:mm dd.MM").format(
                                new Date(Other.getFixedTime(licenceKey.getExpireTime()))
                        )
        );
    }

    @Override
    public void onNewUserMessage(User user) {

    }

    @Override
    public void onUserBanned(User user) {
        serverChecker.stop();
        stopBot();
        Point point = frame.getLocation();
        AuthForm.start(true, point.x, point.y);
        frame.dispose();
    }

    @Override
    public void onKeyExpired(LicenceKey licenceKey) {
        serverChecker.stop();
        stopBot();
        Point point = frame.getLocation();
        AuthForm.start(true, point.x, point.y);
        frame.dispose();
    }

    @Override
    public void onKeyHardwareMismatch(LicenceKey licenceKey) {
        serverChecker.stop();
        stopBot();
        Point point = frame.getLocation();
        AuthForm.start(true, point.x, point.y);
        frame.dispose();

    }

    @Override
    public void onKeyNotActivated(LicenceKey licenceKey) {
        serverChecker.stop();
        stopBot();
        Point point = frame.getLocation();
        AuthForm.start(true, point.x, point.y);
        frame.dispose();
    }

    @Override
    public void onError() {
        serverChecker.stop();
        stopBot();
        Point point = frame.getLocation();
        AuthForm.start(true, point.x, point.y);
        frame.dispose();
    }

    @Override
    public void startBot() {
        showStatus("Запуск софта", Color.WHITE, null, true);
        startBot.setEnabled(false);
        stopBot.setEnabled(true);
        if (!rageMultiplayer.isLaunched()) {
            rageMultiplayer = new RageMultiplayer();
        }
        if (!rageMultiplayer.isLaunched()) {
            botControlPanel.setVisible(false);
            showStatus("Игра не запущена", Color.RED, Color.RED, true);
            initMessage.setVisible(true);
            initMessage.setText("Окно с игрой не может быть найдено, выход через 10с!");
            botSettingsPanel.setVisible(false);
            initMessage.setForeground(Color.YELLOW);
            frame.pack();
            new Timer().scheduleAtFixedRate(new TimerTask() {
                int count = 10;

                @Override
                public void run() {
                    initMessage.setText("Окно с игрой не может быть найдено, выход через " + count + "с!");
                    if (count <= 0) {
                        System.exit(0);
                    }
                    count--;
                }
            }, 0, TimeUnit.SECONDS.toMillis(1));
        } else {
            bot = new Bot(this, this);
            startBotTime = System.currentTimeMillis();
            switch (getSettings().getFishingTime()) {
                case 1:
                    fishingMinutes = TimeUnit.MINUTES.toMillis(15);
                    break;
                case 2:
                    fishingMinutes = TimeUnit.MINUTES.toMillis(30);
                    break;
                case 3:
                    fishingMinutes = TimeUnit.MINUTES.toMillis(45);
                    break;
                case 4:
                    fishingMinutes = TimeUnit.MINUTES.toMillis(60);
                    break;
                case 5:
                    fishingMinutes = TimeUnit.MINUTES.toMillis(120);
                    break;
                default:
                    fishingMinutes = TimeUnit.DAYS.toMillis(14);
            }
            switch (getSettings().getFishingTimeout()) {
                case 1:
                    fishingTimeoutMinutes = TimeUnit.MINUTES.toMillis(5);
                    break;
                case 2:
                    fishingTimeoutMinutes = TimeUnit.MINUTES.toMillis(10);
                    break;
                case 3:
                    fishingTimeoutMinutes = TimeUnit.MINUTES.toMillis(15);
                    break;
                case 4:
                    fishingTimeoutMinutes = TimeUnit.MINUTES.toMillis(30);
                    break;
                default:
                    fishingMinutes = TimeUnit.DAYS.toMillis(14);
            }
            fishingTimeWorker = new Worker() {
                @Override
                public void execute() {
                    float fishingPercent = Other.getTimePercent(startBotTime, startBotTime + fishingMinutes);
                    fishingTime.setText(Other.toHumanTime(System.currentTimeMillis() - startBotTime));
                    fishingTimeProgress.setValue((int) fishingPercent);
                    if (fishingPercent >= 100) {
                        fishingTimeProgress.setValue(0);
                        showStatus("Переход в режим ожидания", Color.WHITE, null, true);
                        if (bot != null && bot.isRunning()) {
                            bot.stop();
                        }
                        if (fishingTimeWorker != null && fishingTimeWorker.isRunning()) {
                            fishingTimeWorker.stop();
                        }
                        if (fishingTimeoutWorker != null && fishingTimeoutWorker.isRunning()) {
                            fishingTimeoutWorker.stop();
                        }
                        if (frame.isVisible())
                            frame.pack();
                        showStatus("Ожидание", Color.WHITE, null, true);
                        startBotTime = System.currentTimeMillis();
                        fishingTimeoutWorker = new Worker() {
                            @Override
                            public void execute() {
                                float fishingTimeoutPercent = Other.getTimePercent(startBotTime, startBotTime + fishingTimeoutMinutes);
                                fishingTimeOutTime.setText(Other.toHumanTime(System.currentTimeMillis() - startBotTime));
                                fishingTimeOutProgress.setValue((int) fishingTimeoutPercent);
                                if (fishingTimeoutPercent >= 100) {
                                    fishingTimeOutProgress.setValue(0);
                                    startBot();
                                    stop();
                                }
                            }

                            @Override
                            public long delay() {
                                return 1000;
                            }
                        };
                        fishingTimeoutWorker.start();
                    }
                }

                @Override
                public long delay() {
                    return 1000;
                }
            };
            botStatusPanel.setVisible(true);
            botSettingsPanel.setVisible(false);
            frame.pack();
            bot.start();
            fishingTimeWorker.start();
        }
    }

    @Override
    public void stopBot() {
        startBot.setEnabled(true);
        stopBot.setEnabled(false);
        showStatus("Остановка софта", Color.WHITE, null, true);
        if (bot != null && bot.isRunning()) {
            bot.stop();
        }
        if (fishingTimeWorker != null && fishingTimeWorker.isRunning()) {
            fishingTimeWorker.stop();
        }
        if (fishingTimeoutWorker != null && fishingTimeoutWorker.isRunning()) {
            fishingTimeoutWorker.stop();
        }
        botStatusPanel.setVisible(false);
        botSettingsPanel.setVisible(true);
        if (frame.isVisible())
            frame.pack();
        showStatus("Ожидание действий", Color.WHITE, null, true);
    }


    @Override
    public BotForm getContext() {
        return this;
    }

    @Override
    public RageMultiplayer getRageMultiplayer() {
        return rageMultiplayer;
    }

    @Override
    public Settings getSettings() {
        return settings;
    }

    @Override
    public boolean isBotRunning() {
        return bot != null && bot.isRunning();
    }

    @Override
    public PcapNetworkInterface getSelectedInterface() {
        return pcapNetworkInterface;
    }

    @Override
    public void updateUI() {
        fillUI();
    }

    @Override
    public void onKeyDown(int keyCode) {
        if (keyCode == KeyEvent.VK_F7) {
            startBot();
        } else if (keyCode == KeyEvent.VK_F8) {
            stopBot();
        }
    }

    @Override
    public void onKeyUp(int keyCode) {

    }


    private void initAction() {
        LinerComboBox.setListener(checkInventorySelector, settings::setInventoryCheckPeriod);
        LinerComboBox.setListener(fishingTimeoutSelector, settings::setFishingTimeout);
        LinerComboBox.setListener(fishingTimeSelector, settings::setFishingTime);
        LinerComboBox.setListener(chooseBait, settings::setBaitIndex);
        LinerComboBox.setListener(chooseRod, settings::setRodIndex);
        qteFix.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                settings.setQteFix(qteFix.isSelected());
            }
        });
        dropBrokenRods.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                settings.setDropBrokenRods(dropBrokenRods.isSelected());
            }
        });
        useAnyRods.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                settings.setUseAnyRods(useAnyRods.isSelected());
            }
        });
        useAnyBaits.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                settings.setUseAntBaits(useAnyBaits.isSelected());
            }
        });
        autoSuicide.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                settings.setSuicideIfNoRodsBaits(autoSuicide.isSelected());
                autoStopBot.setSelected(false);
                settings.setStopBotIfNoRodsBaits(false);
                autoCloseGame.setSelected(false);
                settings.setCloseGameIfNoRodsBaits(false);
            }
        });
        autoStopBot.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                settings.setStopBotIfNoRodsBaits(autoStopBot.isSelected());
                autoSuicide.setSelected(false);
                settings.setSuicideIfNoRodsBaits(false);
                autoCloseGame.setSelected(false);
                settings.setCloseGameIfNoRodsBaits(false);
            }
        });
        autoCloseGame.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                settings.setCloseGameIfNoRodsBaits(autoCloseGame.isSelected());
                autoSuicide.setSelected(false);
                settings.setSuicideIfNoRodsBaits(false);
                autoStopBot.setSelected(false);
                settings.setStopBotIfNoRodsBaits(false);
            }
        });
        closeButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.exit(0);
            }
        });
        logOutButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Point point = frame.getLocation();
                AuthForm.restart(point.x, point.y);
                frame.dispose();
            }
        });
        discordButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    Desktop.getDesktop().browse(new URL("https://discord.gg/YQv4EMVwEc").toURI());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        funpayButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    Desktop.getDesktop().browse(new URL("https://funpay.ru/users/3426436/").toURI());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        telegramButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    Desktop.getDesktop().browse(new URL("https://t.me/SmotraFish").toURI());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        startBot.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                startBot();
            }
        });
        stopBot.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                stopBot();
            }
        });
        gameHook.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                gameHook.setEnabled(false);
                if (!rageMultiplayer.isLaunched()) {
                    botControlPanel.setVisible(false);
                    showStatus("Игра не запущена", Color.RED, Color.RED, true);
                    initMessage.setVisible(true);
                    initMessage.setText("Окно с игрой не может быть найдено, выход через 10с!");
                    botSettingsPanel.setVisible(false);
                    initMessage.setForeground(Color.YELLOW);
                    frame.pack();
                    new Timer().scheduleAtFixedRate(new TimerTask() {
                        int count = 10;

                        @Override
                        public void run() {
                            initMessage.setText("Окно с игрой не может быть найдено, выход через " + count + "с!");
                            if (count <= 0) {
                                System.exit(0);
                            }
                            count--;
                        }
                    }, 0, TimeUnit.SECONDS.toMillis(1));
                } else {
                    MessageForm messageForm = new MessageForm(new MessageConfig.Builder()
                            .setMessageTitle("Подождите")
                            .setMessageText("Подождите пока завершится, процесс обхода будет завершен")
                            .setMessageType(MessageType.PROGRESS_INDETERMINATE)
                            .setMessagePosition(MessagePosition.TOP_RIGHT).build()
                    );
                    messageForm.show();
                    messageForm.playSound(Core.Sound.notification);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            rageMultiplayer.fishingPosHook();
                            rageMultiplayer.releaseResource();
                            messageForm.close();
                            gameHook.setEnabled(true);
                        }
                    }).start();
                }
            }
        });
        checkHook.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                checkHook.setEnabled(false);
                if (!rageMultiplayer.isLaunched()) {
                    botControlPanel.setVisible(false);
                    showStatus("Игра не запущена", Color.RED, Color.RED, true);
                    initMessage.setVisible(true);
                    initMessage.setText("Окно с игрой не может быть найдено, выход через 10с!");
                    botSettingsPanel.setVisible(false);
                    initMessage.setForeground(Color.YELLOW);
                    frame.pack();
                    new Timer().scheduleAtFixedRate(new TimerTask() {
                        int count = 10;

                        @Override
                        public void run() {
                            initMessage.setText("Окно с игрой не может быть найдено, выход через " + count + "с!");
                            if (count <= 0) {
                                System.exit(0);
                            }
                            count--;
                        }
                    }, 0, TimeUnit.SECONDS.toMillis(1));
                } else {
                    MessageForm messageForm = new MessageForm(new MessageConfig.Builder()
                            .setMessageTitle("Подождите, идет проверка")
                            .setMessageText("Подождите когда проверка завершится")
                            .setMessageType(MessageType.PROGRESS_INDETERMINATE)
                            .setMessagePosition(MessagePosition.TOP_RIGHT).build()
                    );
                    messageForm.show();
                    messageForm.playSound(Core.Sound.notification);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if(rageMultiplayer.isFishingHocked()){
                                gameHookText.setText("Обход позиции: Выполнено");
                            } else {
                                gameHookText.setText("Обход позиции: Не выполнено");
                            }
                            rageMultiplayer.releaseResource();
                            frame.pack();
                            messageForm.close();
                            checkHook.setEnabled(true);
                        }
                    }).start();
                }
            }
        });
    }

    private void fillUI() {
        fishingTimeSelector.removeAllItems();
        fishingTimeSelector.addItem(new SelectorItem("Бесконечно", 0));
        fishingTimeSelector.addItem(new SelectorItem("15 минут", 1));
        fishingTimeSelector.addItem(new SelectorItem("30 минут", 2));
        fishingTimeSelector.addItem(new SelectorItem("45 минут", 3));
        fishingTimeSelector.addItem(new SelectorItem("1 час", 4));
        fishingTimeSelector.addItem(new SelectorItem("2 часа", 5));
        fishingTimeoutSelector.removeAllItems();
        fishingTimeoutSelector.addItem(new SelectorItem("Без тайм-аута", 0));
        fishingTimeoutSelector.addItem(new SelectorItem("5 минут", 1));
        fishingTimeoutSelector.addItem(new SelectorItem("10 минут", 2));
        fishingTimeoutSelector.addItem(new SelectorItem("15 минут", 3));
        fishingTimeoutSelector.addItem(new SelectorItem("30 минут", 4));
        checkInventorySelector.removeAllItems();
        checkInventorySelector.addItem(new SelectorItem("Не проверять", 0));
        checkInventorySelector.addItem(new SelectorItem("Каждые 5 пойманных рыб", 1));
        checkInventorySelector.addItem(new SelectorItem("Каждые 10 пойманных рыб", 2));
        checkInventorySelector.addItem(new SelectorItem("Каждые 20 пойманных рыб", 3));
        checkInventorySelector.addItem(new SelectorItem("Каждые 50 пойманных рыб", 4));
        checkInventorySelector.addItem(new SelectorItem("Каждые 100 пойманных рыб", 5));
        checkInventorySelector.setSelectedIndex(settings.getInventoryCheckPeriod());
        chooseBait.removeAllItems();
        chooseBait.addItem(new SelectorItem(Core.Bait.baitNames[0], 0));
        chooseBait.addItem(new SelectorItem(Core.Bait.baitNames[1], 1));
        chooseBait.addItem(new SelectorItem(Core.Bait.baitNames[2], 2));
        chooseBait.addItem(new SelectorItem(Core.Bait.baitNames[3], 3));
        chooseBait.addItem(new SelectorItem(Core.Bait.baitNames[4], 4));
        chooseRod.removeAllItems();
        chooseRod.addItem(new SelectorItem(Core.Rod.rodNames[0], 0));
        chooseRod.addItem(new SelectorItem(Core.Rod.rodNames[1], 1));
        chooseRod.addItem(new SelectorItem(Core.Rod.rodNames[2], 2));
        chooseRod.setSelectedIndex(settings.getRodIndex());
        chooseBait.setSelectedIndex(settings.getBaitIndex());
        fishingTimeSelector.setSelectedIndex(settings.getFishingTime());
        fishingTimeoutSelector.setSelectedIndex(settings.getFishingTimeout());
        qteFix.setSelected(settings.isQteFix());
        dropBrokenRods.setSelected(settings.isDropBrokenRods());
        useAnyRods.setSelected(settings.isUseAnyRods());
        useAnyBaits.setSelected(settings.isUseAntBaits());
        autoSuicide.setSelected(settings.isSuicideIfNoRodsBaits());
        autoStopBot.setSelected(settings.isStopBotIfNoRodsBaits());
        autoCloseGame.setSelected(settings.isCloseGameIfNoRodsBaits());
    }

    private void createUI() {
        DialogMouseListener dialogMouseListener = new DialogMouseListener(frame);
        dragPanel.addMouseListener(dialogMouseListener);
        dragPanel.addMouseMotionListener(dialogMouseListener);
        panel.setOpaque(false);
        panel.setBackground(new Color(0, 0, 0, 0));
        appTitle.setIcon(new ImageIcon(Core.Icon.applicationIcon));
        closeButton.setIcon(Core.Icon.closeIcon);
        logOutButton.setIcon(Core.Icon.logoutIcon);
        discordButton.setIcon(Core.Icon.discordIcon);
        funpayButton.setIcon(Core.Icon.funpayIcon);
        telegramButton.setIcon(Core.Icon.telegramIcon);
        startBot.setIcon(Core.Icon.startIcon);
        stopBot.setIcon(Core.Icon.stopIcon);
        autoCloseGame.setIcon(new ImageIcon(Core.Icon.checkUnselectedIcon));
        autoCloseGame.setSelectedIcon(new ImageIcon(Core.Icon.checkSelectedIcon));
        autoStopBot.setIcon(new ImageIcon(Core.Icon.checkUnselectedIcon));
        autoStopBot.setSelectedIcon(new ImageIcon(Core.Icon.checkSelectedIcon));
        autoSuicide.setIcon(new ImageIcon(Core.Icon.checkUnselectedIcon));
        autoSuicide.setSelectedIcon(new ImageIcon(Core.Icon.checkSelectedIcon));
        useAnyBaits.setIcon(new ImageIcon(Core.Icon.checkUnselectedIcon));
        useAnyBaits.setSelectedIcon(new ImageIcon(Core.Icon.checkSelectedIcon));
        useAnyRods.setIcon(new ImageIcon(Core.Icon.checkUnselectedIcon));
        useAnyRods.setSelectedIcon(new ImageIcon(Core.Icon.checkSelectedIcon));
        dropBrokenRods.setIcon(new ImageIcon(Core.Icon.checkUnselectedIcon));
        dropBrokenRods.setSelectedIcon(new ImageIcon(Core.Icon.checkSelectedIcon));
        fishCatched.setIcon(new ImageIcon(Core.Icon.fishIcon));
        nextInventoryCheckCount.setIcon(new ImageIcon(Core.Icon.inventoryIcon));
        checkInventoryText.setIcon(new ImageIcon(Core.Icon.inventoryIcon));
        fishingTime.setIcon(new ImageIcon(Core.Icon.timeIcon));
        fishingTimeOutTime.setIcon(new ImageIcon(Core.Icon.timeOutIcon));
        gameHookText.setIcon(new ImageIcon(Core.Icon.hackIcon));
        gameHook.setIcon(new ImageIcon(Core.Icon.executeIcon));
        checkHook.setIcon(new ImageIcon(Core.Icon.hackCheckIcon));
        interfaceText.setIcon(new ImageIcon(Core.Icon.interfaceIcon));
        rodText.setIcon(new ImageIcon(Core.Icon.rodIcon));
        baitText.setIcon(new ImageIcon(Core.Icon.baitIcon));
        fishingTimeText.setIcon(new ImageIcon(Core.Icon.timeIcon));
        fishingTimeoutText.setIcon(new ImageIcon(Core.Icon.timeOutIcon));
        qteFix.setIcon(new ImageIcon(Core.Icon.checkUnselectedIcon));
        qteFix.setSelectedIcon(new ImageIcon(Core.Icon.checkSelectedIcon));
        closeButton.setIconPosition(LinerButton.IconPosition.RIGHT);
        LinerComboBox.init(fishingTimeSelector);
        LinerComboBox.init(fishingTimeoutSelector);
        LinerComboBox.init(checkInventorySelector);
        LinerComboBox.init(chooseBait);
        LinerComboBox.init(chooseRod);
        LinerComboBox.init(interfaceSelect);
        fillUI();
    }


    public static void start(User user, LicenceKey licenceKey, int x, int y) {
        frame = new JFrame("");
        frame.setIconImage(Core.Icon.applicationIcon);
        frame.setContentPane(new BotForm(user, licenceKey).panel);
        frame.setUndecorated(true);
        frame.setBackground(new Color(0, 0, 0, 0));
        frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        frame.setResizable(false);
        frame.validate();
        frame.repaint();
        frame.pack();
        frame.setVisible(true);
        frame.setLocation(x, y);
    }

    private void showStatus(String text, Color statusColor, Color progressColor, boolean needCheckSize) {
        statusText.setText(text);
        statusText.setForeground(statusColor);
        statusProgress.setForegroundColor(progressColor == null ? Core.accentColor : progressColor);
        if (frame.isVisible() && needCheckSize)
            frame.pack();
    }


    private void hideStatus() {
        statusText.setText(" ");
        statusProgress.setInvisible(true);
    }

    @Override
    public void updateStatus() {
        showStatus(bot.getBotState().getDescription(), Color.WHITE, null, false);
        fishCatched.setText("Рыбы поймано: " + bot.getFishCatchedCount());
        if (bot.getNextInventoryCheckCount() > 100) {
            nextInventoryCheckCount.setText("До следующей проверки инвентаря: (бесконечно) ∞");
        } else {
            nextInventoryCheckCount.setText("До следующей проверки инвентаря: " + bot.getNextInventoryCheckCount() + " рыб");
        }
        frame.pack();
    }
}
