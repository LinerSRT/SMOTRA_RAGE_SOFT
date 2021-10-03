package com.liner.keygen.generator;

import com.liner.keygen.Core;
import com.liner.keygen.generator.models.LicenceKey;
import com.liner.keygen.generator.models.User;
import com.liner.keygen.generator.ui.*;
import com.liner.keygen.generator.utils.RandomString;
import com.liner.keygen.generator.utils.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class KeyGenerator {
    private JPanel panel;
    private JLabel loadingStatus;
    private CircleProgressBar loadingProgress;
    private LinerButton getKeyInfo;
    private LinerEditText keyField;
    private LinerEditText userField;
    private JLabel userNameText;
    private JLabel userKeysText;
    private JLabel userBanText;
    private JLabel userOnlineText;
    private JLabel userLastOnlineText;
    private JLabel userCPUIDText;
    private JLabel userMBIDText;
    private JLabel userHDDIDText;
    private JLabel userWindowsText;
    private LinerButton banUserButton;
    private LinerButton unbanUserButton;
    private LinerEditText banReasonField;
    private JPanel informationPanel;
    private JPanel userInfoPanel;
    private JPanel keyInfoPanel;
    private RoundedPanel statPanel;
    private JLabel statTotalUserText;
    private JLabel statText;
    private JLabel statTotalOnlineUserText;
    private JLabel statKeys;
    private JLabel statActiveKeys;
    private LinerButton updateStatButton;
    private JLabel keyText;
    private JLabel keyActive;
    private JLabel keyActivateTime;
    private JLabel keyDuration;
    private JLabel keyExpireTime;
    private JLabel keyUser;
    private JPanel userActionPanel;
    private JPanel keyActionPanel;
    private LinerEditText addHourField;
    private LinerEditText addDayField;
    private LinerButton keyAddTimeButton;
    private LinerButton generateKey;
    private LinerEditText generatedKeyText;
    private LinerButton sendKeyToServer;
    private LinerEditText generateDayFiled;
    private LinerEditText generateHourField;
    public static JFrame frame;
    private User user;
    private LicenceKey licenceKey;

    private String format(String string, int n, String delimiter) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : string.split("(?<=\\G.{" + n + "})"))
            stringBuilder.append(s).append(delimiter);
        String formatted = stringBuilder.toString();
        return formatted.substring(0, formatted.lastIndexOf(delimiter));
    }

    public KeyGenerator() {
        keyInfoPanel.setVisible(false);
        userInfoPanel.setVisible(false);
        keyActionPanel.setVisible(false);
        userActionPanel.setVisible(false);
        keyField.setIcon(Core.Icons.KEY);
        generatedKeyText.setIcon(Core.Icons.KEY);
        generateKey.setIcon(new ImageIcon(Core.Icons.KEY));
        sendKeyToServer.setIcon(new ImageIcon(Core.Icons.SEND));
        generateKey.setIcon(new ImageIcon(Core.Icons.SEND));
        keyField.setLimitChar(19);
        userField.setIcon(Core.Icons.USER);
        addHourField.setIcon(Core.Icons.HOUR);
        generateHourField.setIcon(Core.Icons.HOUR);
        addDayField.setIcon(Core.Icons.DAY);
        generateDayFiled.setIcon(Core.Icons.DAY);
        banReasonField.setIcon(Core.Icons.REASON);
        keyAddTimeButton.setIcon(new ImageIcon(Core.Icons.ADDTIME));
        keyExpireTime.setIcon(new ImageIcon(Core.Icons.HOUR));
        banUserButton.setIcon(new ImageIcon(Core.Icons.BAN));
        userLastOnlineText.setIcon(new ImageIcon(Core.Icons.ONLINE));
        userOnlineText.setIcon(new ImageIcon(Core.Icons.ONLINE));
        userBanText.setIcon(new ImageIcon(Core.Icons.BAN));
        userCPUIDText.setIcon(new ImageIcon(Core.Icons.GEAR));
        userKeysText.setIcon(new ImageIcon(Core.Icons.KEY));
        keyText.setIcon(new ImageIcon(Core.Icons.KEY));
        userHDDIDText.setIcon(new ImageIcon(Core.Icons.GEAR));
        userMBIDText.setIcon(new ImageIcon(Core.Icons.GEAR));
        keyActive.setIcon(new ImageIcon(Core.Icons.ACTIVE));
        keyDuration.setIcon(new ImageIcon(Core.Icons.DURATION));
        keyActivateTime.setIcon(new ImageIcon(Core.Icons.TIMEWARN));
        userWindowsText.setIcon(new ImageIcon(Core.Icons.WINDOWS));
        userNameText.setIcon(new ImageIcon(Core.Icons.USER));
        keyUser.setIcon(new ImageIcon(Core.Icons.USER));
        unbanUserButton.setIcon(new ImageIcon(Core.Icons.UNBAN));
        statText.setIcon(new ImageIcon(Core.Icons.STATISTICS));
        statTotalUserText.setIcon(new ImageIcon(Core.Icons.USERS));
        statTotalOnlineUserText.setIcon(new ImageIcon(Core.Icons.USERS_ONLINE));
        statKeys.setIcon(new ImageIcon(Core.Icons.KEY));
        statActiveKeys.setIcon(new ImageIcon(Core.Icons.KEY));
        getKeyInfo.setIcon(new ImageIcon(Core.Icons.INFO));
        updateStatButton.setIcon(new ImageIcon(Core.Icons.REFRESH));
        loadingProgress.setIndeterminate(true);
        showStatus("Загрузка...");
        updateStatistics();
        updateStatButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                updateStatistics();
            }
        });
        getKeyInfo.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (!userField.getText().isEmpty())
                    getUserInfo(userField.getText());
                else
                    userField.focus(Color.YELLOW);
                if (!keyField.getText().isEmpty())
                    getKeyInfo(keyField.getText());
                else
                    keyField.focus(Color.YELLOW);
            }
        });
        addDayField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!addDayField.getText().isEmpty()) {
                            float value = Float.parseFloat(addDayField.getText());
                            addHourField.setText("" + value / 24f);
                        } else {
                            addHourField.setText("");
                        }
                    }
                }, 100);
            }
        });
        addHourField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!addHourField.getText().isEmpty()) {
                            float value = Float.parseFloat(addHourField.getText());
                            addDayField.setText("" + value * 24f);
                        } else {
                            addDayField.setText("");
                        }
                    }
                }, 100);
            }
        });


        generateDayFiled.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!generateDayFiled.getText().isEmpty()) {
                            float value = Float.parseFloat(generateDayFiled.getText());
                            generateHourField.setText("" + value * 24f);
                        } else {
                            generateHourField.setText("");
                        }
                    }
                }, 100);
            }
        });
        generateHourField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!generateHourField.getText().isEmpty()) {
                            float value = Float.parseFloat(generateHourField.getText());
                            generateDayFiled.setText("" + value / 24f);
                        } else {
                            generateDayFiled.setText("");
                        }
                    }
                }, 100);
            }
        });

        keyAddTimeButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (licenceKey != null) {

                } else {
                    showStatus("Ошибка, попробуйте получить данные еще раз!", 2);
                }
            }
        });
        generateKey.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new Thread(() -> generatedKeyText.setText(format(new RandomString(16).nextString(), 4, "-").toUpperCase())).start();
            }
        });
        sendKeyToServer.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (generatedKeyText.getText().isEmpty()) {
                    generateKey.focus(Color.RED);
                } else {
                    if (generateHourField.getText().isEmpty()) {
                        generateHourField.focus(Color.RED);
                    } else {
                        if (generateDayFiled.getText().isEmpty()) {
                            generateDayFiled.focus(Color.RED);
                        } else {
                            String generateResult = Server.createNewKey(generatedKeyText.getText(), Float.parseFloat(generateHourField.getText()));
                            if (generateResult != null) {
                                showStatus("Ключ успешно сгенерирован! Результат скопирован в буфер обмена", 3);
                                generateResult = generateResult + Float.parseFloat(generateHourField.getText())+"ч";
                                StringSelection stringSelection = new StringSelection(generateResult);
                                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                                clipboard.setContents(stringSelection, null);
                            } else {
                                showStatus("Ошибка генерации ключа!", 2);
                            }
                        }
                    }
                }
            }
        });
    }

    public void getKeyInfo(String key) {
        new Thread(() -> {
            showStatus("Выполняется запрос серверу...");
            LicenceKey licenceKey = Server.getLicenceKey(key);
            if (licenceKey.getKey() == null) {
                this.licenceKey = null;
                showStatus("Ошибка запроса!", 2);
                keyField.focus(Color.RED);
                keyText.setText("Ключ:  ");
                keyActive.setText("Активирован:  ");
                keyActivateTime.setText("Активирован в:  ");
                keyDuration.setText("Время действия:  ");
                keyExpireTime.setText("Истекает:  ");
                keyUser.setText("Пользователь:  ");
                keyInfoPanel.setVisible(false);
                keyActionPanel.setVisible(false);
                frame.pack();
            } else {
                this.licenceKey = licenceKey;
                keyInfoPanel.setVisible(true);
                keyActionPanel.setVisible(true);
                frame.pack();
                keyText.setText("Ключ:  " + licenceKey.getKey());
                keyActive.setText("Активирован:  " + (licenceKey.isActivated ? "да" : "нет"));
                keyActivateTime.setText("Активирован в:  " + (licenceKey.isActivated ? LicenceKey.simpleDateFormat.format(new Date(licenceKey.getActivateTime())) : "не активирован"));
                keyDuration.setText("Время действия:  " + TimeUnit.MILLISECONDS.toHours(licenceKey.getDurationTime()) + "ч  (" + (Math.round(TimeUnit.MILLISECONDS.toHours(licenceKey.getDurationTime()) / 24f)) + "д)");
                keyExpireTime.setText("Истекает:  " + (licenceKey.isActivated ? LicenceKey.simpleDateFormat.format(new Date(licenceKey.getExpireTime())) : "не активирован"));
                keyUser.setText("Пользователь:  " + (licenceKey.isActivated ? Server.getUser(licenceKey.getHardwareInfo()).getGameUsername() : "не активирован"));
                showStatus("Запрос выполнен успешно", 3);
            }
        }).start();
    }

    public void getUserInfo(String username) {
        new Thread(() -> {
            showStatus("Выполняется запрос серверу...");
            User user = Server.getUser(username);
            if (user.getGameUsername() == null) {
                this.user = null;
                showStatus("Ошибка запроса!", 2);
                userField.focus(Color.RED);
                userNameText.setText("Имя пользователя:  ");
                userKeysText.setText("Ключей пользователя:  ");
                userBanText.setText("Забанен:  ");
                userOnlineText.setText("Онлайн:  ");
                userLastOnlineText.setVisible(!user.getOnline());
                userLastOnlineText.setText("Был онлайн в:  ");
                userCPUIDText.setText("CPUID:  ");
                userMBIDText.setText("MBID:  ");
                userHDDIDText.setText("HDDID:  ");
                userWindowsText.setText("Windows:  ");
                userInfoPanel.setVisible(false);
                userActionPanel.setVisible(false);
                frame.pack();
            } else {
                this.user = user;
                userInfoPanel.setVisible(true);
                userActionPanel.setVisible(true);
                frame.pack();
                userNameText.setText("Имя пользователя:  " + user.getGameUsername());
                userKeysText.setText("Ключей пользователя:  " + user.getLicenceKeys().size());
                userBanText.setText("Забанен:  " + (user.getBanned() ? "Да" : "Нет")+", "+user.getBanReason());
                userOnlineText.setText("Онлайн:  " + (user.getOnline() ? "Да" : "Нет"));
                userLastOnlineText.setText("Был онлайн в:  " + user.getLastOnlineTime());
                userCPUIDText.setText("CPUID:  " + user.getHardwareInfo().getCpuSerialNumber());
                userMBIDText.setText("MBID:  " + user.getHardwareInfo().getMotherSerialNumber());
                userHDDIDText.setText("HDDID:  " + user.getHardwareInfo().getDiskSerialNumber());
                userWindowsText.setText("Windows:  " + user.getHardwareInfo().getWindowsPCName());
                showStatus("Запрос выполнен успешно", 3);
                for (ActionListener actionListener : banUserButton.getActionListeners())
                    banUserButton.removeActionListener(actionListener);
                banUserButton.addActionListener(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        Server.setUserBan(user.getGameUsername(), banReasonField.getText(), true);
                        showStatus("Пользователь забанен успешно", 3);
                    }
                });
                for (ActionListener actionListener : unbanUserButton.getActionListeners())
                    unbanUserButton.removeActionListener(actionListener);
                unbanUserButton.addActionListener(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        Server.setUserBan(user.getGameUsername(), banReasonField.getText(), false);
                        showStatus("Пользователь разбанен успешно", 3);
                    }
                });
            }
        }).start();
    }

    private void updateStatistics() {
        new Thread(() -> {
            showStatus("Обновление данных...");
            statTotalUserText.setText("Всего пользователей: " + Server.getAllUsers());
            statTotalOnlineUserText.setText("Пользователей в сети: " + Server.getAllOnlineUsers());
            statKeys.setText("Всего ключей: " + Server.getAllKeys());
            statActiveKeys.setText("Активных ключей: " + Server.getActiveKeys());
            showStatus("Обновление завершено", 3);
        }).start();
    }


    private void showStatus(String text) {
        showStatus(text, 0);
    }

    public void showStatus(String text, int type) {
        loadingProgress.setInvisible(false);
        loadingStatus.setText(text);
        switch (type) {
            case 0:
                loadingStatus.setForeground(Color.WHITE);
                loadingProgress.setForegroundColor(new Color(177, 56, 238));
                break;
            case 1:
                loadingStatus.setForeground(Color.YELLOW);
                loadingProgress.setForegroundColor(Color.YELLOW);
                break;
            case 2:
                loadingStatus.setForeground(Color.RED);
                loadingProgress.setForegroundColor(Color.RED);
                break;
            case 3:
                loadingStatus.setForeground(Color.GREEN);
                loadingProgress.setForegroundColor(Color.GREEN);
                break;
            case 4:
                loadingProgress.setForegroundColor(new Color(47, 47, 50));
                loadingStatus.setText(" ");
                break;
        }
    }

    public static void main(String[] args) {
        frame = new JFrame("Fishbot Server Keygen");
        frame.setIconImage(Core.Icons.KEY);
        frame.setContentPane(new KeyGenerator().panel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
    }


}
