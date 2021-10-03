package com.liner.fishbotserver;

import com.google.gson.Gson;
import com.liner.fishbotserver.data.*;
import com.liner.fishbotserver.funpay.FunPayCommentParser;
import com.liner.fishbotserver.response.BaseResponse;
import com.liner.fishbotserver.server.KeyHandler;
import com.liner.fishbotserver.server.SiteHandler;
import com.liner.fishbotserver.server.UserHandler;
import com.liner.fishbotserver.utilities.AES;
import com.liner.fishbotserver.utilities.Files;
import com.liner.fishbotserver.utilities.OS;
import com.liner.fishbotserver.utilities.Time;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.sun.net.httpserver.*;

import javax.net.ssl.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unchecked")
public class Main {
    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
    public static int PORT = 8282;
    public static int PORT2 = 8181;
    private static final String GET = "GET";
    private static final String ACTION = "action";
    private static final String SECRET_CODE = "linerapikey";
    private static List<KeyAddCounter> keyAddCounterList;
    public static TelegramBot bot;

    public static void main(String[] args) throws IOException {
        Files.ensureDirectory(User.usersDirectory);
        Files.ensureDirectory(LicenceKey.licenceDirectory);
        bot = new TelegramBot("1844280883:AAEuvocTJpzvyur37IbKyKhJwHp01cvW6YE");
        keyAddCounterList = (List<KeyAddCounter>) new ObjectManager(new File(System.getProperty("user.dir"))).loadList("dnfjY7dnsd.sh", KeyAddCounter.class);
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        HttpsServer httpsServer = HttpsServer.create(new InetSocketAddress(PORT2), 0);
        try {
            disableCertificateValidation(httpsServer);
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | CertificateException | KeyManagementException e) {
            e.printStackTrace();
        }
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        ThreadPoolExecutor executor2 = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        new UserHandler(server, (exchange, method, command, queryValues) -> {
            if (method.equals(GET)) {
                if (queryValues.containsKey(ACTION)) {
                    switch (queryValues.get(ACTION)) {
                        case "register_user": {
                            if (
                                    queryValues.containsKey("username") &&
                                            queryValues.containsKey("hardware")
                            ) {
                                Base64.Decoder decoder = Base64.getDecoder();
                                String userName = new String(decoder.decode(queryValues.get("username").getBytes()));
                                String hardware = new String(decoder.decode(queryValues.get("hardware").getBytes()));
                                HardwareInfo hardwareInfo = new Gson().fromJson(hardware, HardwareInfo.class);
                                User user = new User(userName, hardwareInfo);
                                if (!User.exists(user)) {
                                    user.save();
                                    new BaseResponse<>(200,
                                            "User " + userName + ", successfully registered on server"
                                    ).send(exchange);
                                } else {
                                    new BaseResponse<>(400,
                                            "User already registered"
                                    ).send(exchange);
                                }
                            } else {
                                new BaseResponse<>(400,
                                        "Cannot register user, wrong action"
                                ).send(exchange);
                            }
                        }
                        break;
                        case "ban_user": {
                            if (
                                    queryValues.containsKey("username") &&
                                            queryValues.containsKey("reason") &&
                                            queryValues.containsKey("apikey")
                            ) {
                                Base64.Decoder decoder = Base64.getDecoder();
                                String userName = new String(decoder.decode(queryValues.get("username").getBytes()));
                                byte[] banReason = decoder.decode(queryValues.get("reason").getBytes());
                                String apiKey = new String(decoder.decode(queryValues.get("apikey").getBytes()));
                                if (SECRET_CODE.equals(AES.decrypt(apiKey))) {
                                    if (User.exists(userName)) {
                                        User user = User.load(userName);
                                        if (user == null) {
                                            new BaseResponse<>(400,
                                                    "Cannot load user"
                                            ).send(exchange);
                                            return;
                                        }
                                        user.setBanned(true, banReason);
                                        user.save();
                                        new BaseResponse<>(200,
                                                "User " + userName + " banned successfully!"
                                        ).send(exchange);
                                    } else {
                                        new BaseResponse<>(400,
                                                "User not exists"
                                        ).send(exchange);
                                    }
                                } else {
                                    new BaseResponse<>(400,
                                            "Wrong API key!"
                                    ).send(exchange);
                                }
                            } else {
                                new BaseResponse<>(400,
                                        "No valid action"
                                ).send(exchange);
                            }
                        }
                        break;
                        case "unban_user": {
                            if (
                                    queryValues.containsKey("username") &&
                                            queryValues.containsKey("apikey")
                            ) {
                                Base64.Decoder decoder = Base64.getDecoder();
                                String userName = new String(decoder.decode(queryValues.get("username").getBytes()));
                                String apiKey = new String(decoder.decode(queryValues.get("apikey").getBytes()));
                                if (SECRET_CODE.equals(AES.decrypt(apiKey))) {
                                    if (User.exists(userName)) {
                                        User user = User.load(userName);
                                        if (user == null) {
                                            new BaseResponse<>(400,
                                                    "Cannot load user"
                                            ).send(exchange);
                                            return;
                                        }
                                        user.setBanned(false, "".getBytes());
                                        user.save();
                                        new BaseResponse<>(200,
                                                "User " + userName + " unbanned successfully!"
                                        ).send(exchange);
                                    } else {
                                        new BaseResponse<>(400,
                                                "User not exists"
                                        ).send(exchange);
                                    }
                                } else {
                                    new BaseResponse<>(400,
                                            "Wrong API key!"
                                    ).send(exchange);
                                }
                            } else {
                                new BaseResponse<>(400,
                                        "No valid action"
                                ).send(exchange);
                            }
                        }
                        break;
                        case "get_user": {
                            if (
                                    queryValues.containsKey("username")
                            ) {
                                Base64.Decoder decoder = Base64.getDecoder();
                                String userName = new String(decoder.decode(queryValues.get("username").getBytes()));
                                if (User.exists(userName)) {
                                    User user = User.load(userName);
                                    if (user == null) {
                                        new BaseResponse<>(400,
                                                "Cannot load user"
                                        ).send(exchange);
                                        return;
                                    }
                                    send(exchange, new Gson().toJson(user));
                                } else {
                                    new BaseResponse<>(400,
                                            "User not exists"
                                    ).send(exchange);
                                }
                            } else {
                                new BaseResponse<>(400,
                                        "No valid action"
                                ).send(exchange);
                            }
                        }
                        break;
                        case "get_user_by_hw": {
                            if (
                                    queryValues.containsKey("hardware")
                            ) {
                                Base64.Decoder decoder = Base64.getDecoder();
                                String hardware = new String(decoder.decode(queryValues.get("hardware").getBytes()));
                                HardwareInfo hardwareInfo = new Gson().fromJson(hardware, HardwareInfo.class);
                                User user = User.loadByHardware(hardwareInfo);
                                if (user == null) {
                                    new BaseResponse<>(400,
                                            "Cannot find user"
                                    ).send(exchange);
                                    return;
                                }
                                send(exchange, new Gson().toJson(user));
                            } else {
                                new BaseResponse<>(400,
                                        "No valid action"
                                ).send(exchange);
                            }
                        }
                        break;
                        case "get_all_users": {
                            if (
                                    queryValues.containsKey("apikey")
                            ) {
                                Base64.Decoder decoder = Base64.getDecoder();
                                String apiKey = new String(decoder.decode(queryValues.get("apikey").getBytes()));
                                if (SECRET_CODE.equals(AES.decrypt(apiKey))) {
                                    StringBuilder stringBuilder = new StringBuilder();
                                    int count = 0;
                                    for (File file : Objects.requireNonNull(User.usersDirectory.listFiles())) {
                                        stringBuilder.append(file.getName().replace(".json", "")).append("|");
                                        count++;
                                    }
                                    String result = count + "," + stringBuilder.toString();
                                    if (result.contains("|"))
                                        result = result.substring(0, result.lastIndexOf("|"));
                                    if (stringBuilder.toString().isEmpty()) {
                                        result = result.substring(0, result.lastIndexOf(","));
                                    }
                                    new BaseResponse<>(200,
                                            result
                                    ).send(exchange);
                                } else {
                                    new BaseResponse<>(400,
                                            "Wrong API key!"
                                    ).send(exchange);
                                }
                            } else {
                                new BaseResponse<>(400,
                                        "No valid action"
                                ).send(exchange);
                            }
                        }
                        break;
                        case "get_users_online": {
                            if (
                                    queryValues.containsKey("apikey")
                            ) {
                                Base64.Decoder decoder = Base64.getDecoder();
                                String apiKey = new String(decoder.decode(queryValues.get("apikey").getBytes()));
                                if (SECRET_CODE.equals(AES.decrypt(apiKey))) {
                                    StringBuilder stringBuilder = new StringBuilder();
                                    int count = 0;
                                    for (File file : Objects.requireNonNull(User.usersDirectory.listFiles())) {
                                        User user = User.load(file.getName().replace(".json", ""));
                                        if (user != null && user.isOnline()) {
                                            stringBuilder.append(user.getUsername()).append("|");
                                            count++;
                                        }
                                    }
                                    String result = count + "," + stringBuilder.toString();
                                    if (result.contains("|"))
                                        result = result.substring(0, result.lastIndexOf("|"));
                                    if (stringBuilder.toString().isEmpty())
                                        result = result.substring(0, result.lastIndexOf(","));
                                    new BaseResponse<>(200,
                                            result
                                    ).send(exchange);
                                } else {
                                    new BaseResponse<>(400,
                                            "Wrong API key!"
                                    ).send(exchange);
                                }
                            } else {
                                new BaseResponse<>(400,
                                        "No valid action"
                                ).send(exchange);
                            }
                        }
                        break;
                        case "set_online": {
                            if (
                                    queryValues.containsKey("username") &&
                                            queryValues.containsKey("online")
                            ) {
                                Base64.Decoder decoder = Base64.getDecoder();
                                String userName = new String(decoder.decode(queryValues.get("username").getBytes()));
                                String online = new String(decoder.decode(queryValues.get("online").getBytes()));
                                if (User.exists(userName)) {
                                    User user = User.load(userName);
                                    if (user == null) {
                                        new BaseResponse<>(400,
                                                "Cannot load user"
                                        ).send(exchange);
                                        return;
                                    }
                                    user.setOnline((online.equals("1") || online.equals("true")));
                                    user.setLastOnlineTime(simpleDateFormat.format(new Date(Time.getFixedCurrentTime())));
                                    user.save();
                                    new BaseResponse<>(200,
                                            "User: " + userName + ", online: " + user.isOnline() + ", lastOnline: " + user.getLastOnlineTime()
                                    ).send(exchange);
                                } else {
                                    new BaseResponse<>(400,
                                            "User not exists"
                                    ).send(exchange);
                                }
                            } else {
                                new BaseResponse<>(400,
                                        "No valid action"
                                ).send(exchange);
                            }
                        }
                        break;
                        case "send_notification": {
                            if (
                                    queryValues.containsKey("username") &&
                                            queryValues.containsKey("notification")
                            ) {
                                Base64.Decoder decoder = Base64.getDecoder();
                                String userName = new String(decoder.decode(queryValues.get("username").getBytes()));
                                String notificationString = new String(decoder.decode(queryValues.get("notification").getBytes()));
                                Notification notification = new Gson().fromJson(notificationString, Notification.class);
                                if (User.exists(userName)) {
                                    User user = User.load(userName);
                                    if (user == null) {
                                        new BaseResponse<>(400,
                                                "Cannot load user"
                                        ).send(exchange);
                                        return;
                                    }
                                    if (user.addNotification(notification)) {
                                        user.save();
                                        new BaseResponse<>(200,
                                                "Created notification: " + notification.getId() + " for: " + userName
                                        ).send(exchange);
                                    } else {
                                        new BaseResponse<>(400,
                                                "Notification already exists"
                                        ).send(exchange);
                                    }
                                } else {
                                    new BaseResponse<>(400,
                                            "User not exists"
                                    ).send(exchange);
                                }
                            } else {
                                new BaseResponse<>(400,
                                        "No valid action"
                                ).send(exchange);
                            }
                        }
                        break;
                        case "read_notification": {
                            if (
                                    queryValues.containsKey("username") &&
                                            queryValues.containsKey("notification_id")
                            ) {
                                Base64.Decoder decoder = Base64.getDecoder();
                                String userName = new String(decoder.decode(queryValues.get("username").getBytes()));
                                String notificationId = new String(decoder.decode(queryValues.get("notification_id").getBytes()));
                                if (User.exists(userName)) {
                                    User user = User.load(userName);
                                    if (user == null) {
                                        new BaseResponse<>(400,
                                                "Cannot load user"
                                        ).send(exchange);
                                        return;
                                    }
                                    if (user.setNotificationRead(true, notificationId)) {
                                        user.save();
                                        new BaseResponse<>(200,
                                                "Notification: " + notificationId + " has been seen"
                                        ).send(exchange);
                                    } else {
                                        new BaseResponse<>(400,
                                                "Notification:" + notificationId + " not exists"
                                        ).send(exchange);
                                    }
                                } else {
                                    new BaseResponse<>(400,
                                            "User not exists"
                                    ).send(exchange);
                                }
                            } else {
                                new BaseResponse<>(400,
                                        "No valid action"
                                ).send(exchange);
                            }
                        }
                        break;
                        case "remove_notification": {
                            if (
                                    queryValues.containsKey("username") &&
                                            queryValues.containsKey("notification_id")
                            ) {
                                Base64.Decoder decoder = Base64.getDecoder();
                                String userName = new String(decoder.decode(queryValues.get("username").getBytes()));
                                String notificationId = new String(decoder.decode(queryValues.get("notification_id").getBytes()));
                                if (User.exists(userName)) {
                                    User user = User.load(userName);
                                    if (user == null) {
                                        new BaseResponse<>(400,
                                                "Cannot load user"
                                        ).send(exchange);
                                        return;
                                    }
                                    user.removeNotification(notificationId);
                                    user.save();
                                    new BaseResponse<>(200,
                                            "Notification: " + notificationId + " has been removed successfully"
                                    ).send(exchange);
                                } else {
                                    new BaseResponse<>(400,
                                            "User not exists"
                                    ).send(exchange);
                                }
                            } else {
                                new BaseResponse<>(400,
                                        "No valid action"
                                ).send(exchange);
                            }
                        }
                        break;
                        case "get_notifications": {
                            if (
                                    queryValues.containsKey("username")
                            ) {
                                Base64.Decoder decoder = Base64.getDecoder();
                                String userName = new String(decoder.decode(queryValues.get("username").getBytes()));
                                if (User.exists(userName)) {
                                    User user = User.load(userName);
                                    if (user == null) {
                                        new BaseResponse<>(400,
                                                "Cannot load user"
                                        ).send(exchange);
                                        return;
                                    }
                                    send(exchange, new Gson().toJson(user.getNotificationMessages()));
                                } else {
                                    new BaseResponse<>(400,
                                            "User not exists"
                                    ).send(exchange);
                                }
                            } else {
                                new BaseResponse<>(400,
                                        "No valid action"
                                ).send(exchange);
                            }
                        }
                        break;
                        case "create_promo": {
                            if (
                                    queryValues.containsKey("username") &&
                                            queryValues.containsKey("promo") &&
                                            queryValues.containsKey("apikey")
                            ) {
                                Base64.Decoder decoder = Base64.getDecoder();
                                String userName = new String(decoder.decode(queryValues.get("username").getBytes()));
                                String promoString = new String(decoder.decode(queryValues.get("promo").getBytes()));
                                String apiKey = new String(decoder.decode(queryValues.get("apikey").getBytes()));
                                PromoCode promoCode = new Gson().fromJson(promoString, PromoCode.class);
                                if (SECRET_CODE.equals(AES.decrypt(apiKey))) {
                                    if (User.exists(userName)) {
                                        User user = User.load(userName);
                                        if (user == null) {
                                            new BaseResponse<>(400,
                                                    "Cannot load user"
                                            ).send(exchange);
                                            return;
                                        }
                                        user.addPromoCode(promoCode);
                                        user.save();
                                        new BaseResponse<>(200,
                                                "Created new promo: " + promoCode.getPromoCode() + " for: " + userName
                                        ).send(exchange);
                                    } else {
                                        new BaseResponse<>(400,
                                                "User not exists"
                                        ).send(exchange);
                                    }
                                } else {
                                    new BaseResponse<>(400,
                                            "Wrong API key!"
                                    ).send(exchange);
                                }
                            } else {
                                new BaseResponse<>(400,
                                        "No valid action"
                                ).send(exchange);
                            }
                        }
                        break;
                        case "create_comment": {
                            if (
                                    queryValues.containsKey("username") &&
                                            queryValues.containsKey("comment")
                            ) {
                                Base64.Decoder decoder = Base64.getDecoder();
                                String userName = new String(decoder.decode(queryValues.get("username").getBytes()));
                                Comment comment = new Gson().fromJson(
                                        new String(decoder.decode(queryValues.get("comment").getBytes())),
                                        Comment.class
                                );
                                if (User.exists(userName)) {
                                    User user = User.load(userName);
                                    if (user == null) {
                                        new BaseResponse<>(400,
                                                "Cannot load user"
                                        ).send(exchange);
                                        return;
                                    }
                                    if (user.getComment() == null) {
                                        user.setComment(comment);
                                        user.save();
                                        new BaseResponse<>(200,
                                                "Created new comment: " + comment.getComment() + ", with rating: " + comment.getRating()
                                        ).send(exchange);
                                    } else {
                                        new BaseResponse<>(400,
                                                "User already have comment"
                                        ).send(exchange);
                                    }
                                } else {
                                    new BaseResponse<>(400,
                                            "User not exists"
                                    ).send(exchange);
                                }
                            } else {
                                new BaseResponse<>(400,
                                        "No valid action"
                                ).send(exchange);
                            }
                        }
                        break;
                    }
                } else {
                    new BaseResponse<>(400,
                            "No valid action"
                    ).send(exchange);
                }
            } else {
                new BaseResponse<>(400,
                        "No valid action"
                ).send(exchange);
            }
        });
        new KeyHandler(server, (exchange, method, command, queryValues) -> {
            if (method.equals(GET)) {
                if (queryValues.containsKey(ACTION)) {
                    switch (queryValues.get(ACTION)) {
                        case "create_key": {
                            if (
                                    queryValues.containsKey("key") &&
                                            queryValues.containsKey("duration") &&
                                            queryValues.containsKey("apikey")
                            ) {
                                Base64.Decoder decoder = Base64.getDecoder();
                                String key = new String(decoder.decode(queryValues.get("key").getBytes()));
                                String duration = new String(decoder.decode(queryValues.get("duration").getBytes()));
                                String apiKey = new String(decoder.decode(queryValues.get("apikey").getBytes()));
                                if (SECRET_CODE.equals(AES.decrypt(apiKey))) {
                                    Pattern pattern = Pattern.compile("([A-Z0-9]{4})-([A-Z0-9]{4})-([A-Z0-9]{4})-([A-Z0-9]{4})");
                                    Matcher matcher = pattern.matcher(key);
                                    if (matcher.find()) {
                                        if (!LicenceKey.exists(key)) {
                                            LicenceKey licenceKey = new LicenceKey(
                                                    key,
                                                    Long.parseLong(duration)
                                            );
                                            KeyAddCounter keyAddCounter = new KeyAddCounter(
                                                    key,
                                                    simpleDateFormat.format(new Date(Time.getFixedCurrentTime())),
                                                    licenceKey.getDurationTime()
                                            );
                                            bot.execute(
                                                    new SendMessage(
                                                            750117845,
                                                            "Сгенерирован новый ключ: "+key+"\n" +
                                                                    "Дата: " + keyAddCounter.getAddDate() +"\n"+
                                                                    "Кол-во дней: " + keyAddCounter.getDaysTime() +"\n"+
                                                                    ""
                                                    ).disableNotification(true)
                                            );
                                            keyAddCounterList.add(keyAddCounter);
                                            new ObjectManager(new File(System.getProperty("user.dir"))).save("dnfjY7dnsd.sh", keyAddCounterList);
                                            licenceKey.save();
                                            new BaseResponse<>(200,
                                                    "Key: " + key + " successfully generated, expire: " + simpleDateFormat.format(new Date(licenceKey.getExpireTime()))
                                            ).send(exchange);
                                        } else {
                                            new BaseResponse<>(400,
                                                    "Key already exists"
                                            ).send(exchange);
                                        }
                                    } else {
                                        new BaseResponse<>(400,
                                                "Wrong key format, skipping"
                                        ).send(exchange);
                                    }
                                } else {
                                    new BaseResponse<>(400,
                                            "Wrong API key!"
                                    ).send(exchange);
                                }
                            } else {
                                new BaseResponse<>(400,
                                        "No valid action"
                                ).send(exchange);
                            }
                        }
                        break;
                        case "delete_key": {
                            if (
                                    queryValues.containsKey("key") &&
                                            queryValues.containsKey("apikey")
                            ) {
                                Base64.Decoder decoder = Base64.getDecoder();
                                String key = new String(decoder.decode(queryValues.get("key").getBytes()));
                                String apiKey = new String(decoder.decode(queryValues.get("apikey").getBytes()));
                                if (SECRET_CODE.equals(AES.decrypt(apiKey))) {
                                    Pattern pattern = Pattern.compile("([A-Z0-9]{4})-([A-Z0-9]{4})-([A-Z0-9]{4})-([A-Z0-9]{4})");
                                    Matcher matcher = pattern.matcher(key);
                                    if (matcher.find()) {
                                        if (LicenceKey.exists(key)) {
                                            if (LicenceKey.delete(key)) {
                                                new BaseResponse<>(200,
                                                        "Key successfully deleted"
                                                ).send(exchange);
                                            } else {
                                                new BaseResponse<>(400,
                                                        "Key delete error"
                                                ).send(exchange);
                                            }
                                        } else {
                                            new BaseResponse<>(400,
                                                    "Key not exists"
                                            ).send(exchange);
                                        }
                                    } else {
                                        new BaseResponse<>(400,
                                                "Wrong key format, skipping"
                                        ).send(exchange);
                                    }
                                } else {
                                    new BaseResponse<>(400,
                                            "Wrong API key!"
                                    ).send(exchange);
                                }
                            } else {
                                new BaseResponse<>(400,
                                        "No valid action"
                                ).send(exchange);
                            }
                        }
                        break;
                        case "modify_key_duration": {
                            if (
                                    queryValues.containsKey("key") &&
                                            queryValues.containsKey("duration") &&
                                            queryValues.containsKey("apikey")
                            ) {
                                Base64.Decoder decoder = Base64.getDecoder();
                                String key = new String(decoder.decode(queryValues.get("key").getBytes()));
                                String duration = new String(decoder.decode(queryValues.get("duration").getBytes()));
                                String apiKey = new String(decoder.decode(queryValues.get("apikey").getBytes()));
                                if (SECRET_CODE.equals(AES.decrypt(apiKey))) {
                                    Pattern pattern = Pattern.compile("([A-Z0-9]{4})-([A-Z0-9]{4})-([A-Z0-9]{4})-([A-Z0-9]{4})");
                                    Matcher matcher = pattern.matcher(key);
                                    if (matcher.find()) {
                                        if (LicenceKey.exists(key)) {
                                            LicenceKey licenceKey = LicenceKey.load(key);
                                            if (licenceKey == null) {
                                                new BaseResponse<>(400,
                                                        "Key not exists"
                                                ).send(exchange);
                                                return;
                                            }
                                            licenceKey.addDuration(Long.parseLong(duration));
                                            licenceKey.save();
                                            new BaseResponse<>(200,
                                                    "Key: " + key + " successfully updated, new expire: " + simpleDateFormat.format(new Date(licenceKey.getExpireTime()))
                                            ).send(exchange);
                                        } else {
                                            new BaseResponse<>(400,
                                                    "Key not exists"
                                            ).send(exchange);
                                        }
                                    } else {
                                        new BaseResponse<>(400,
                                                "Wrong key format, skipping"
                                        ).send(exchange);
                                    }
                                } else {
                                    new BaseResponse<>(400,
                                            "Wrong API key!"
                                    ).send(exchange);
                                }
                            } else {
                                new BaseResponse<>(400,
                                        "No valid action"
                                ).send(exchange);
                            }
                        }
                        break;
                        case "activate_key": {
                            if (
                                    queryValues.containsKey("key") &&
                                            queryValues.containsKey("hardware")
                            ) {
                                Base64.Decoder decoder = Base64.getDecoder();
                                String key = new String(decoder.decode(queryValues.get("key").getBytes()));
                                String hardware = new String(decoder.decode(queryValues.get("hardware").getBytes()));
                                HardwareInfo hardwareInfo = new Gson().fromJson(hardware, HardwareInfo.class);
                                if (LicenceKey.exists(key)) {
                                    LicenceKey licenceKey = LicenceKey.load(key);
                                    if (licenceKey == null) {
                                        new BaseResponse<>(400,
                                                "Key not exists"
                                        ).send(exchange);
                                        return;
                                    }
                                    PromoCode promoCode = null;
                                    if (queryValues.containsKey("promo")) {
                                        promoCode = new Gson().fromJson(
                                                new String(decoder.decode(queryValues.get("promo").getBytes())), PromoCode.class
                                        );
                                    }
                                    if (licenceKey.isActivated()) {
                                        new BaseResponse<>(400,
                                                "Key already activated!"
                                        ).send(exchange);
                                        return;
                                    }
                                    if (licenceKey.activateKey(hardwareInfo, promoCode)) {
                                        send(exchange, new Gson().toJson(licenceKey));
                                    } else {
                                        new BaseResponse<>(400,
                                                "Key is not activated"
                                        ).send(exchange);
                                    }
                                } else {
                                    new BaseResponse<>(400,
                                            "Key not exists"
                                    ).send(exchange);
                                }

                            } else {
                                new BaseResponse<>(400,
                                        "No valid action"
                                ).send(exchange);
                            }
                        }
                        break;
                        case "get_key": {
                            if (
                                    queryValues.containsKey("key")
                            ) {
                                Base64.Decoder decoder = Base64.getDecoder();
                                String key = new String(decoder.decode(queryValues.get("key").getBytes()));
                                if (LicenceKey.exists(key)) {
                                    LicenceKey licenceKey = LicenceKey.load(key);
                                    if (licenceKey == null) {
                                        new BaseResponse<>(400,
                                                "Key not exists"
                                        ).send(exchange);
                                        return;
                                    }
                                    licenceKey.isExpired();
                                    send(exchange, new Gson().toJson(licenceKey));
                                } else {
                                    new BaseResponse<>(400,
                                            "Key not exists"
                                    ).send(exchange);
                                }

                            } else {
                                new BaseResponse<>(400,
                                        "No valid action"
                                ).send(exchange);
                            }
                        }
                        case "get_active_keys": {
                            if (
                                    queryValues.containsKey("apikey")
                            ) {
                                Base64.Decoder decoder = Base64.getDecoder();
                                String apiKey = new String(decoder.decode(queryValues.get("apikey").getBytes()));
                                if (SECRET_CODE.equals(AES.decrypt(apiKey))) {
                                    StringBuilder stringBuilder = new StringBuilder();
                                    int count = 0;
                                    for (File file : Objects.requireNonNull(LicenceKey.licenceDirectory.listFiles())) {
                                        LicenceKey licenceKey = LicenceKey.load(file.getName().replace(".json", ""));
                                        if (licenceKey != null && !licenceKey.isExpired()) {
                                            stringBuilder.append(licenceKey.getKey()).append("|");
                                            count++;
                                        }
                                    }
                                    String result = count + "," + stringBuilder.toString();
                                    if (result.contains("|"))
                                        result = result.substring(0, result.lastIndexOf("|"));
                                    if (stringBuilder.toString().isEmpty())
                                        result = result.substring(0, result.lastIndexOf(","));
                                    new BaseResponse<>(200,
                                            result
                                    ).send(exchange);
                                } else {
                                    new BaseResponse<>(400,
                                            "Wrong API key!"
                                    ).send(exchange);
                                }
                            }
                        }
                        break;
                        case "get_all_keys": {
                            if (
                                    queryValues.containsKey("apikey")
                            ) {
                                Base64.Decoder decoder = Base64.getDecoder();
                                String apiKey = new String(decoder.decode(queryValues.get("apikey").getBytes()));
                                if (SECRET_CODE.equals(AES.decrypt(apiKey))) {
                                    StringBuilder stringBuilder = new StringBuilder();
                                    int count = 0;
                                    for (File file : Objects.requireNonNull(LicenceKey.licenceDirectory.listFiles())) {
                                        LicenceKey licenceKey = LicenceKey.load(file.getName().replace(".json", ""));
                                        if (licenceKey != null) {
                                            stringBuilder.append(licenceKey.getKey()).append("|");
                                            count++;
                                        }
                                    }
                                    String result = count + "," + stringBuilder.toString();
                                    if (result.contains("|"))
                                        result = result.substring(0, result.lastIndexOf("|"));
                                    if (stringBuilder.toString().isEmpty())
                                        result = result.substring(0, result.lastIndexOf(","));
                                    new BaseResponse<>(200,
                                            result
                                    ).send(exchange);
                                } else {
                                    new BaseResponse<>(400,
                                            "Wrong API key!"
                                    ).send(exchange);
                                }
                            } else {
                                new BaseResponse<>(400,
                                        "No valid action"
                                ).send(exchange);
                            }
                        }
                        break;
                    }
                } else {
                    new BaseResponse<>(400,
                            "No valid action"
                    ).send(exchange);
                }
            } else {
                new BaseResponse<>(400,
                        "No valid action"
                ).send(exchange);
            }
        });
        new SiteHandler(httpsServer, (exchange, method, command, queryValues) -> {
            if (method.equals(GET)) {
                if (queryValues.containsKey(ACTION)) {
                    switch (queryValues.get(ACTION)) {
                        case "get_funpay_comments":
                            FunPayCommentParser.getComments(funPayCommentList -> send(exchange, new Gson().toJson(funPayCommentList)));
                            break;
                        case "get_all_users": {
                            new BaseResponse<>(200,
                                    Objects.requireNonNull(User.usersDirectory.listFiles()).length
                            ).send(exchange);
                        }
                        break;
                        case "get_users_online": {
                            int count = 0;
                            for (File file : Objects.requireNonNull(User.usersDirectory.listFiles())) {
                                User user = User.load(file.getName().replace(".json", ""));
                                if (user != null && user.isOnline()) {
                                    count++;
                                }
                            }
                            new BaseResponse<>(200,
                                    count
                            ).send(exchange);
                        }
                        break;
                        case "get_all_keys": {
                            new BaseResponse<>(200,
                                    Objects.requireNonNull(LicenceKey.licenceDirectory.listFiles()).length
                            ).send(exchange);
                        }
                        break;
                        case "get_all_active_keys": {
                            int count = 0;
                            for (File file : Objects.requireNonNull(LicenceKey.licenceDirectory.listFiles())) {
                                LicenceKey licenceKey = LicenceKey.load(file.getName().replace(".json", ""));
                                if (licenceKey != null && !licenceKey.isExpired()) {
                                    count++;
                                }
                            }
                            new BaseResponse<>(200,
                                    count
                            ).send(exchange);
                        }
                        break;
                        case "get_all_key_time": {
                            long count = 0;
                            for (File file : Objects.requireNonNull(LicenceKey.licenceDirectory.listFiles())) {
                                LicenceKey licenceKey = LicenceKey.load(file.getName().replace(".json", ""));
                                if (licenceKey != null) {
                                    count += licenceKey.getDurationTime();
                                }
                            }
                            new BaseResponse<>(200,
                                    count
                            ).send(exchange);
                        }
                        break;
                        case "server_stat": {
                            String serverStatus = OS.execWaitCommand("./stat.sh");
                            String status = OS.execCommand("systemctl status fishbot.service");
                            Pattern pattern = Pattern.compile("Active: ([a-zA-Z]*) \\(([a-zA-Z]*)\\) since ([a-zA-Z\\w\\W]*); ([a-zA-Z\\W\\w]*)ago");
                            Matcher matcher = pattern.matcher(status);
                            ServerStat serverStat = new ServerStat(serverStatus, "", "");
                            if(matcher.find()){
                                serverStat.setServiceStarted(matcher.group(3));
                                serverStat.setServiceUptime(matcher.group(4));
                            }
                            send(exchange, new Gson().toJson(serverStat));
                        }
                        break;
                    }
                } else {
                    new BaseResponse<>(400,
                            "No valid action"
                    ).send(exchange);
                }
            } else {
                new BaseResponse<>(400,
                        "No valid action"
                ).send(exchange);
            }
        });
        server.setExecutor(executor);
        server.start();
        httpsServer.setExecutor(executor2);
        httpsServer.start();
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Setting offline for users...");
                for (File file : Objects.requireNonNull(User.usersDirectory.listFiles())) {
                    User user = new ObjectManager(User.usersDirectory).load(file.getName(), User.class);
                    if (user.isOnline())
                        user.setLastOnlineTime(simpleDateFormat.format(new Date(Time.getFixedCurrentTime())));
                    user.setOnline(false);
                    System.out.println("Set offline for: " + user.getUsername() + ", last online: " + user.getLastOnlineTime());
                    user.save();
                }
            }
        }, 0, TimeUnit.MINUTES.toMillis(5));
        System.out.println("API Key: " + AES.encrypt(SECRET_CODE));
        System.out.println("API Key (Encoded): " + Base64.getEncoder().encodeToString(AES.encrypt(SECRET_CODE).getBytes()));
    }

    public static void send(HttpExchange exchange, String content) {
        try {
            Headers headers = exchange.getResponseHeaders();
            headers.add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, content.getBytes().length);
            OutputStream stream = exchange.getResponseBody();
            stream.write(content.getBytes());
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void disableCertificateValidation(HttpsServer server) throws KeyStoreException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException, KeyManagementException {
//        System.setProperty("javax.net.ssl.trustStoreType", "jks");
//        System.setProperty("javax.net.ssl.keyStore", "fishbot.p12");
//        System.setProperty("javax.net.ssl.trustStore", "fishbotserver.keystore");
//        System.setProperty("javax.net.debug", "ssl");
//        System.setProperty("javax.net.ssl.keyStorePassword", "");
//        System.setProperty("javax.net.ssl.trustStorePassword", "123456qaZ");
//        SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(
                new FileInputStream(
                        new File(System.getProperty("user.dir"), "fishbot.p12")
                ),
                "".toCharArray()
        );
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, "".toCharArray());
        SSLContext ctx = SSLContext.getInstance("SSL");
        ctx.init(keyManagerFactory.getKeyManagers(), new TrustManager[]{}, new SecureRandom());
        server.setHttpsConfigurator(new HttpsConfigurator(ctx));
    }
}
