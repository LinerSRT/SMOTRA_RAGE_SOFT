package com.liner.ragebot.server;


import com.google.gson.Gson;
import com.liner.ragebot.jna.HardwareInfo;
import com.liner.ragebot.server.models.LicenceKey;
import com.liner.ragebot.server.models.User;
import com.liner.ragebot.utils.Files;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unchecked")
public class Server {
    public interface LicenceCallback {
        void onReceive(LicenceKey licenceKey);

        void onFailed(String reason);
    }

    public interface UserCallback {
        void onReceive(User user);

        void onFailed(String reason);
    }

    public interface SmotraUserCallback {
        void onExists();

        void onFailed();
    }

    public interface RegisterCallback {
        void onRegistered();

        void onAlreadyRegistered();

        void onFailed();
    }

    public static void getLicence(String key, LicenceCallback callback) {
        Base64.Encoder encoder = Base64.getEncoder();
        sendRequest(
                "http://80.87.199.200:8282/key?action=" +
                        "get_key&" +
                        "key=" + encoder.encodeToString(key.getBytes()), response -> {
                    LicenceKey licenceKey = new Gson().fromJson(
                            response,
                            LicenceKey.class
                    );
                    if (licenceKey.getKey() != null) {
                        callback.onReceive(licenceKey);
                    } else {
                        callback.onFailed(response);
                    }
                });
    }

    public static void activateLicence(String key, LicenceCallback callback) {
        Base64.Encoder encoder = Base64.getEncoder();
        sendRequest(
                "http://80.87.199.200:8282/key?action=" +
                        "activate_key&" +
                        "key=" + encoder.encodeToString(key.getBytes()) + "&" +
                        "hardware=" + encoder.encodeToString(new Gson().toJson(HardwareInfo.getHardware()).getBytes()), response -> {
                    LicenceKey licenceKey = new Gson().fromJson(
                            response,
                            LicenceKey.class
                    );
                    if (licenceKey.getKey() != null) {
                        callback.onReceive(licenceKey);
                    } else {
                        callback.onFailed(response);
                    }
                });
    }

    public static void getUser(String username, UserCallback callback) {
        Base64.Encoder encoder = Base64.getEncoder();
        sendRequest(
                "http://80.87.199.200:8282/user?action=" +
                        "get_user&" +
                        "username=" + encoder.encodeToString(username.getBytes()), response -> {
                    User user = new Gson().fromJson(response, User.class);
                    if (user.getGameUsername() != null) {
                        callback.onReceive(user);
                    } else {
                        callback.onFailed(response);
                    }
                });
    }

    public static void getSmotraUser(String username, SmotraUserCallback callback) {
        sendRequest("https://api.smotra.games/rage/checklog.php?login=" + username, new ResponseCallback() {
            @Override
            public void onResponse(String response) {
                if (response.contains("Success")) {
                    callback.onExists();
                } else {
                    callback.onFailed();
                }
            }
        });
    }

    public static String getSmotraUsername() {
        File storageFile = new File(Advapi32Util.registryGetStringValue(
                WinReg.HKEY_CURRENT_USER,
                "Software\\\\RAGE-MP",
                "rage_path"
        ) + "\\client_resources\\9e7404f101260295ba163aa5c852bd14\\.storage");
        if (storageFile.exists()) {
            Pattern pattern = Pattern.compile("\"login\":\"([\\w]*)\"");
            Matcher matcher = pattern.matcher(Objects.requireNonNull(Files.readFile(storageFile)));
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }

    public static void setOnline(String username, boolean online) {
        Base64.Encoder encoder = Base64.getEncoder();
        sendRequest(
                "http://80.87.199.200:8282/user?action=" +
                        "set_online&" +
                        "username=" + encoder.encodeToString(username.getBytes()) + "&" +
                        "online=" + encoder.encodeToString(String.valueOf(online).getBytes())
                , new ResponseCallback() {
                    @Override
                    public void onResponse(String response) {

                    }
                });
    }

    public static void registerUser(String username, RegisterCallback callback) {
        if (username == null || username.isEmpty()) {
            callback.onFailed();
            return;
        }
        Base64.Encoder encoder = Base64.getEncoder();
        sendRequest(
                "http://80.87.199.200:8282/user?action=" +
                        "register_user&" +
                        "username=" + encoder.encodeToString(username.getBytes()) + "&" +
                        "hardware=" + encoder.encodeToString(new Gson().toJson(HardwareInfo.getHardware()).getBytes()), new ResponseCallback() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains("successfully")) {
                            callback.onRegistered();
                        } else if (response.contains("already")) {
                            callback.onAlreadyRegistered();
                        } else {
                            callback.onFailed();
                        }
                    }
                });
    }


    public interface ResponseCallback {
        void onResponse(String response);
    }

    public static void sendRequest(String url, ResponseCallback responseCallback) {
        new Thread(() -> {
            try {
                HttpURLConnection httpClient = (HttpURLConnection) new URL(url).openConnection();
                httpClient.setRequestMethod("GET");
                httpClient.setReadTimeout(60 * 1000);
                httpClient.setConnectTimeout(60 * 1000);
                httpClient.setRequestProperty("User-Agent", "Mozilla/5.0");
                try (BufferedReader in = new BufferedReader(new InputStreamReader((httpClient.getResponseCode() == 200) ? httpClient.getInputStream() : httpClient.getErrorStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    responseCallback.onResponse(response.toString());
                }
            } catch (IOException e) {
                responseCallback.onResponse("null");
            }
        }).start();
    }

}
