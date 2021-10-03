package com.liner.ragebot.auth;

import com.liner.ragebot.Core;
import com.liner.ragebot.Settings;
import com.liner.ragebot.bot.BotForm;
import com.liner.ragebot.jna.HardwareInfo;
import com.liner.ragebot.messages.*;
import com.liner.ragebot.server.Server;
import com.liner.ragebot.server.models.LicenceKey;
import com.liner.ragebot.server.models.User;
import com.liner.ragebot.server.updater.UpdateCheck;
import com.liner.ragebot.ui.CircleProgressBar;
import com.liner.ragebot.ui.DialogMouseListener;
import com.liner.ragebot.ui.LinerButton;
import com.liner.ragebot.ui.LinerEditText;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthForm implements CheckUserCallback, LoginUserCallback, CheckLicenceCallback {
    public static JFrame frame;
    private final Settings settings;
    private LinerEditText userName;
    private LinerButton discordButton;
    private LinerButton telegramButton;
    private JPanel dragPanel;
    private JPanel panel;
    private LinerButton closeButton;
    private LinerEditText userKey;
    private LinerButton loginButton;
    private JLabel appTitle;
    private CircleProgressBar statusProgress;
    private JLabel statusText;
    private LinerButton funpayButton;
    private JLabel authTitle;
    private JPanel statusPanel;
    private JPanel actionPanel;
    private JLabel banReason;
    private User user;
    private LicenceKey licenceKey;
    private boolean autoLogin;

    public AuthForm(boolean autoLogin) {
        this.autoLogin = autoLogin;
        createUI();
        showStatus("Инициализация", Color.WHITE, null);
        settings = Settings.load();
        closeButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (user != null) {
                    closeButton.setEnabled(false);
                    Server.setOnline(user.getGameUsername(), false);
                }
                System.exit(0);
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

        showStatus("Проверка обновлений", Color.WHITE, null);
        new Thread(() -> UpdateCheck.checkUpdates(new UpdateCheck.UpdateCallback() {
            @Override
            public void onForceUpdate(String version, String url, String changes) {
                new Thread(() -> {
                    MessageForm messageForm = new MessageForm(
                            new MessageConfig.Builder()
                                    .setMessageTitle("Новое обязательное обновление!")
                                    .setMessageText("Обнаружено обязательное обновление " + version + " \n Изменения: " + changes)
                                    .setMessageIcon(Core.Icon.updateIcon)
                                    .setCancelListener(new MessageActionListener() {
                                        @Override
                                        public String getName() {
                                            return "Выход";
                                        }

                                        @Override
                                        public BufferedImage getIcon() {
                                            return Core.Icon.stopIcon;
                                        }

                                        @Override
                                        public void onClicked(MessageForm message) {
                                            System.exit(0);
                                        }
                                    })
                                    .setConfirmListener(new MessageActionListener() {
                                        @Override
                                        public String getName() {
                                            return "Обновить";
                                        }

                                        @Override
                                        public BufferedImage getIcon() {
                                            return Core.Icon.updateIcon;
                                        }

                                        @Override
                                        public void onClicked(MessageForm message) {
                                            message.close();
                                            UpdateCheck.forceUpdate(url, version);
                                        }
                                    })
                                    .setMessageType(MessageType.ERROR)
                                    .setMessagePosition(MessagePosition.TOP_RIGHT)
                                    .build()
                    );
                    messageForm.setAlwaysOnTop(true);
                    messageForm.show();
                    messageForm.playSound(Core.Sound.notification);
                }).start();
            }

            @Override
            public void onUpdate(String version, String url, String changes) {
                new Thread(() -> {
                    MessageForm messageForm = new MessageForm(
                            new MessageConfig.Builder()
                                    .setMessageTitle("Новое обновление!")
                                    .setMessageText("Обнаружено обновление " + version + " \n Изменения: " + changes)
                                    .setMessageIcon(Core.Icon.updateIcon)
                                    .setCancelListener(new MessageActionListener() {
                                        @Override
                                        public String getName() {
                                            return "Пропустить";
                                        }

                                        @Override
                                        public BufferedImage getIcon() {
                                            return Core.Icon.startIcon;
                                        }

                                        @Override
                                        public void onClicked(MessageForm message) {
                                            message.close();
                                            showStatus("Проверка пользователя", Color.WHITE, null);
                                            checkUser(settings.getUserName(), AuthForm.this);
                                        }
                                    })
                                    .setConfirmListener(new MessageActionListener() {
                                        @Override
                                        public String getName() {
                                            return "Обновить";
                                        }

                                        @Override
                                        public BufferedImage getIcon() {
                                            return Core.Icon.updateIcon;
                                        }

                                        @Override
                                        public void onClicked(MessageForm message) {
                                            message.close();
                                            UpdateCheck.forceUpdate(url, version);
                                        }
                                    })
                                    .setMessageType(MessageType.ERROR)
                                    .setMessagePosition(MessagePosition.TOP_RIGHT)
                                    .build()
                    );
                    messageForm.show();
                    messageForm.playSound(Core.Sound.notification);
                }).start();
            }

            @Override
            public void onNoUpdate() {
                showStatus("Проверка пользователя", Color.WHITE, null);
                checkUser(settings.getUserName(), AuthForm.this);
            }
        })).start();
    }


    @Override
    public void onSuccess(User user) {
        this.user = user;
        Server.setOnline(user.getGameUsername(), true);
        loginButton.setEnabled(true);
        settings.setUserName(user.getGameUsername());
        hideStatus();
        if (settings.getLicenceKey() != null && !settings.getLicenceKey().isEmpty()) {
            userName.setText(user.getGameUsername());
            if (autoLogin) {
                activateLicence(settings.getLicenceKey(), AuthForm.this);
            } else {
                userKey.setText(settings.getLicenceKey());
                userName.setEditable(false);
                userName.setText(user.getGameUsername());
                userName.setEnabled(false);
                userKey.setEditable(true);
                userKey.setEnabled(true);
                userKey.requestFocus();
                userKey.toggleFocus();
                userKey.setCaretPosition(settings.getLicenceKey().length());
                loginButton.setText("Войти");
                for (ActionListener actionListener : loginButton.getActionListeners())
                    loginButton.removeActionListener(actionListener);
                loginButton.addActionListener(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        activateLicence(userKey.getText(), AuthForm.this);
                    }
                });
            }
        } else {
            userName.setEditable(false);
            userName.setText(user.getGameUsername());
            userName.setEnabled(false);
            userKey.setEditable(true);
            userKey.setEnabled(true);
            userKey.requestFocus();
            userKey.toggleFocus();
            loginButton.setText("Войти");
            for (ActionListener actionListener : loginButton.getActionListeners())
                loginButton.removeActionListener(actionListener);
            loginButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    userName.unfocus(null);
                    userKey.unfocus(null);
                    activateLicence(userKey.getText(), AuthForm.this);
                }
            });
        }

    }

    @Override
    public void onAlreadyRegistered() {
        loginButton.setEnabled(true);
        showStatus("Пользователь уже зарегистрирован!", Color.YELLOW, null, false);
        userName.setEditable(true);
        userName.requestFocus();
        userName.toggleFocus();
        userKey.setEditable(false);
        userKey.setEnabled(false);
        loginButton.setText("Авторизовать никнейм");
        for (ActionListener actionListener : loginButton.getActionListeners())
            loginButton.removeActionListener(actionListener);
        loginButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                checkUser(userName.getText(), AuthForm.this);
            }
        });
    }

    @Override
    public void onFailedLogin() {
        loginButton.setEnabled(true);
        showStatus("Произошла ошибка", Color.RED, null, false);
        if (settings.getUserName() != null && !settings.getUserName().isEmpty()) {
            userName.setText(settings.getUserName());
            userName.setCaretPosition(settings.getUserName().length());
        }
        userName.setEditable(true);
        userName.requestFocus();
        userName.toggleFocus();
        userKey.setEditable(false);
        userKey.setEnabled(false);
        loginButton.setText("Авторизовать никнейм");
        for (ActionListener actionListener : loginButton.getActionListeners())
            loginButton.removeActionListener(actionListener);
        loginButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                checkUser(userName.getText(), AuthForm.this);
            }
        });
    }

    @Override
    public void onUserBanned(User user) {
        authTitle.setForeground(Color.RED);
        authTitle.setText("Ваш аккаунт навсегда заблокирован");
        banReason.setVisible(true);
        banReason.setText("Причина: " + user.getBanReason());
        actionPanel.setVisible(false);
        statusPanel.setVisible(false);
        frame.pack();
    }

    @Override
    public void startRegister(String username) {
        loginButton.setEnabled(false);
        showStatus("Авторизация", Color.WHITE, null);
        loginUser(username, this);
    }

    @Override
    public void failedLoadUsername() {
        loginButton.setEnabled(true);
        showStatus("Не удалость проверить пользователя", Color.RED, null, false);
        userName.setEditable(true);
        userName.requestFocus();
        userName.toggleFocus();
        userKey.setEditable(false);
        userKey.setEnabled(false);
        loginButton.setText("Авторизовать никнейм");
        for (ActionListener actionListener : loginButton.getActionListeners())
            loginButton.removeActionListener(actionListener);
        loginButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                checkUser(userName.getText(), AuthForm.this);
            }
        });
    }

    @Override
    public void failedConfirmUsername() {
        loginButton.setEnabled(true);
        showStatus("Введен не верный никнейм", Color.RED, null, false);
        userName.setEditable(true);
        userName.requestFocus();
        userName.toggleFocus();
        userKey.setEditable(false);
        userKey.setEnabled(false);
        loginButton.setText("Попробовать еще");
    }

    @Override
    public void successLogin(User user, LicenceKey licenceKey) {
        this.licenceKey = licenceKey;
        this.user = user;
        settings.setLicenceKey(licenceKey.getKey());
        showStatus("Вход выполнен", Color.WHITE, null, false);
        userName.setEditable(false);
        userName.setEnabled(false);
        userName.setText(user.getGameUsername());
        userKey.setText(licenceKey.getKey());
        userKey.setEditable(false);
        userKey.setEnabled(false);
        loginButton.setEnabled(false);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Point point = frame.getLocation();
                BotForm.start(user, licenceKey, point.x, point.y);
                frame.dispose();
            }
        }, 1000);
    }

    @Override
    public void onKeyActivate(String licenceKey) {
        userKey.setText(licenceKey);
        showStatus("Активация ключа", Color.WHITE, null);
        Server.activateLicence(licenceKey, new Server.LicenceCallback() {
            @Override
            public void onReceive(LicenceKey licenceKey) {
                successLogin(user, licenceKey);
            }

            @Override
            public void onFailed(String reason) {
                onKeyActivateFailed();
            }
        });
    }

    @Override
    public void onKeyActivateFailed() {
        loginButton.setEnabled(true);
        showStatus("Ошибка активации ключа", Color.RED, null, false);
        userKey.setEditable(true);
        userKey.setEnabled(true);
        userKey.requestFocus();
        userKey.toggleFocus();
        for (ActionListener actionListener : loginButton.getActionListeners())
            loginButton.removeActionListener(actionListener);
        loginButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                activateLicence(userKey.getText(), AuthForm.this);
            }
        });
    }

    @Override
    public void onKeyExpired() {
        loginButton.setEnabled(true);
        showStatus("Срок действия ключа вышел", Color.YELLOW, null, false);
        userKey.setEditable(true);
        userKey.setEnabled(true);
        userKey.requestFocus();
        userKey.toggleFocus();
        for (ActionListener actionListener : loginButton.getActionListeners())
            loginButton.removeActionListener(actionListener);
        loginButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                activateLicence(userKey.getText(), AuthForm.this);
            }
        });
    }

    @Override
    public void onKeyHardwareMismatch() {
        loginButton.setEnabled(true);
        showStatus("Ключ активирован на другом ПК", Color.RED, null, false);
        userKey.setEditable(true);
        userKey.setEnabled(true);
        userKey.requestFocus();
        userKey.toggleFocus();
        for (ActionListener actionListener : loginButton.getActionListeners())
            loginButton.removeActionListener(actionListener);
        loginButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                activateLicence(userKey.getText(), AuthForm.this);
            }
        });

    }

    @Override
    public void onWrongKeyFormat() {
        loginButton.setEnabled(true);
        showStatus("Неправильный формат ключа", Color.RED, null, false);
        userKey.setEditable(true);
        userKey.setEnabled(true);
        userKey.requestFocus();
        userKey.toggleFocus();
        for (ActionListener actionListener : loginButton.getActionListeners())
            loginButton.removeActionListener(actionListener);
        loginButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                activateLicence(userKey.getText(), AuthForm.this);
            }
        });
    }

    @Override
    public void onFailedKeyInfoGather() {
        loginButton.setEnabled(true);
        showStatus("Ошибка получения информации о ключе", Color.RED, null, false);
        userKey.setEditable(true);
        userKey.setEnabled(true);
        userKey.requestFocus();
        userKey.toggleFocus();
        for (ActionListener actionListener : loginButton.getActionListeners())
            loginButton.removeActionListener(actionListener);
        loginButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                activateLicence(userKey.getText(), AuthForm.this);
            }
        });
    }

    private void activateLicence(String licenceKey, CheckLicenceCallback callback) {
        loginButton.setEnabled(false);
        Pattern pattern = Pattern.compile("([A-Z0-9]{4})-([A-Z0-9]{4})-([A-Z0-9]{4})-([A-Z0-9]{4})");
        Matcher matcher = pattern.matcher(licenceKey);
        if (matcher.find()) {
            showStatus("Получение информации о ключе", Color.WHITE, null);
            Server.getLicence(licenceKey, new Server.LicenceCallback() {
                @Override
                public void onReceive(LicenceKey licenceKey) {
                    HardwareInfo hardwareInfo = HardwareInfo.getHardware();
                    if (licenceKey.isActivated && hardwareInfo.equals(licenceKey.getHardwareInfo())) {
                        if (!licenceKey.isExpired) {
                            callback.successLogin(user, licenceKey);
                        } else {
                            callback.onKeyExpired();
                        }
                    } else if (licenceKey.isActivated && !hardwareInfo.equals(licenceKey.getHardwareInfo())) {
                        callback.onKeyHardwareMismatch();
                    } else if (!licenceKey.isActivated) {
                        callback.onKeyActivate(licenceKey.getKey());
                    }
                }

                @Override
                public void onFailed(String reason) {
                    callback.onFailedKeyInfoGather();
                }
            });
        } else {
            callback.onWrongKeyFormat();
        }
    }

    private void loginUser(String username, LoginUserCallback loginUserCallback) {
        loginButton.setEnabled(false);
        Server.registerUser(username, new Server.RegisterCallback() {
            @Override
            public void onRegistered() {
                showStatus("Получение информации", Color.WHITE, null);
                Server.getUser(username, new Server.UserCallback() {
                    @Override
                    public void onReceive(User user) {
                        loginUserCallback.onSuccess(user);
                    }

                    @Override
                    public void onFailed(String reason) {
                        loginUserCallback.onFailedLogin();
                    }
                });
            }

            @Override
            public void onAlreadyRegistered() {
                showStatus("Идентификация", Color.WHITE, null);
                Server.getUser(username, new Server.UserCallback() {
                    @Override
                    public void onReceive(User user) {
                        if (HardwareInfo.getHardware().equals(user.getHardwareInfo())) {
                            if (user.getBanned()) {
                                loginUserCallback.onUserBanned(user);
                            } else {
                                loginUserCallback.onSuccess(user);
                            }
                        } else {
                            loginUserCallback.onAlreadyRegistered();
                        }
                    }

                    @Override
                    public void onFailed(String reason) {
                        loginUserCallback.onFailedLogin();
                    }
                });
            }

            @Override
            public void onFailed() {
                loginUserCallback.onFailedLogin();
            }
        });
    }

    private void checkUser(String username, CheckUserCallback callback) {
        loginButton.setEnabled(false);
        if (username == null || username.isEmpty())
            username = Server.getSmotraUsername();
        if (username == null || username.isEmpty()) {
            callback.failedLoadUsername();
        } else {
            showStatus("Проверка никнейма", Color.WHITE, null);
            String finalUsername = username;
            Server.getSmotraUser(username, new Server.SmotraUserCallback() {
                @Override
                public void onExists() {
                    callback.startRegister(finalUsername);
                }

                @Override
                public void onFailed() {
                    callback.failedConfirmUsername();
                }
            });
        }
    }


    private void showStatus(String text, Color statusColor, Color progressColor, boolean showProgress) {
        statusText.setText((!showProgress) ? " \t" + text : text);
        statusText.setForeground(statusColor);
        statusProgress.setInvisible(!showProgress);
        statusProgress.setVisible(showProgress);
        statusProgress.setForegroundColor(progressColor == null ? Core.accentColor : progressColor);
    }

    private void showStatus(String text, Color statusColor, Color progressColor) {
        showStatus(text, statusColor, progressColor, true);
    }

    private void hideStatus() {
        statusText.setText(" ");
        statusProgress.setInvisible(true);
    }

    private void createUI() {
        DialogMouseListener dialogMouseListener = new DialogMouseListener(frame);
        dragPanel.addMouseListener(dialogMouseListener);
        dragPanel.addMouseMotionListener(dialogMouseListener);
        panel.setOpaque(false);
        panel.setBackground(new Color(0, 0, 0, 0));
        frame.setIconImage(Core.Icon.applicationIcon);
        closeButton.setIcon(Core.Icon.closeIcon);
        discordButton.setIcon(Core.Icon.discordIcon);
        funpayButton.setIcon(Core.Icon.funpayIcon);
        appTitle.setIcon(new ImageIcon(Core.Icon.applicationIcon));
        telegramButton.setIcon(Core.Icon.telegramIcon);
        userName.setIcon(Core.Icon.userIcon);
        userKey.setIcon(Core.Icon.passwordIcon);
        loginButton.setIcon(Core.Icon.loginIcon);
        closeButton.setIconPosition(LinerButton.IconPosition.RIGHT);
        userKey.setLimitChar(19);
    }

    public static void main(String[] args) {
        start(true, 0, 0);
    }

    public static void restart(int x, int y) {
        if (frame != null)
            frame.dispose();
        start(false, x, y);
    }

    public static void start(boolean autoLogin, int x, int y) {
        Core.encodeResourceDirectory(Core.resourceDirectory);
        frame = new JFrame("");
        frame.setIconImage(Core.Icon.applicationIcon);
        frame.setContentPane(new AuthForm(autoLogin).panel);
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
}
