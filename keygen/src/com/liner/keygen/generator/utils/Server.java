package com.liner.keygen.generator.utils;

import com.google.gson.Gson;
import com.liner.keygen.generator.models.HardwareInfo;
import com.liner.keygen.generator.models.LicenceKey;
import com.liner.keygen.generator.models.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {
    public static String API_KEY = "1ZujFgHwDBr+sF4sIenVpw==";
    public static String API_KEY_BASE64 = Base64.getEncoder().encodeToString(API_KEY.getBytes());

    public static String createNewKey(String key, float hours){
        Base64.Encoder encoder = Base64.getEncoder();
        String response = getResponse("http://80.87.199.200:8282/key?action=" +
                "create_key&" +
                "key=" + encoder.encodeToString(key.getBytes())+"&"+
                "duration=" + encoder.encodeToString(String.valueOf(TimeUnit.HOURS.toMillis(Math.round(hours))).getBytes())+"&"+
                "apikey=" + API_KEY_BASE64);

        Pattern pattern = Pattern.compile("Key:([\\W\\w]*) successfully generated, expire: ([0-9.: ]*)");
        Matcher matcher = pattern.matcher(response);
        if(matcher.find()){
            return "Ваш ключ: \""+matcher.group(1)+"\"\nСрок действия ключа: ";
        } else {
            return null;
        }
    }

    public static void setUserBan(String userName, String reason, boolean banned) {
        Base64.Encoder encoder = Base64.getEncoder();
        getResponse("http://80.87.199.200:8282/user?action=" +
                (banned ? "ban_user" : "unban_user") + "&" +
                "username=" + encoder.encodeToString(userName.getBytes()) + "&" +
                "reason=" + encoder.encodeToString(reason.getBytes(StandardCharsets.UTF_8)) + "&" +
                "apikey=" + API_KEY_BASE64);

    }

    public static User getUser(String username) {
        Base64.Encoder encoder = Base64.getEncoder();
        return getResponse(
                "http://80.87.199.200:8282/user?action=" +
                        "get_user&" +
                        "username=" + encoder.encodeToString(username.getBytes()), User.class);
    }

    public static User getUser(HardwareInfo hardwareInfo) {
        Base64.Encoder encoder = Base64.getEncoder();
        return getResponse(
                "http://80.87.199.200:8282/user?action=" +
                        "get_user_by_hw&" +
                        "hardware=" + encoder.encodeToString(new Gson().toJson(hardwareInfo).getBytes()), User.class);
    }

    public static LicenceKey getLicenceKey(String key) {
        Base64.Encoder encoder = Base64.getEncoder();
        return getResponse(
                "http://80.87.199.200:8282/key?action=" +
                        "get_key&" +
                        "key=" + encoder.encodeToString(key.getBytes()), LicenceKey.class);
    }

    public static int getAllUsers() {
        String response = getResponse("http://80.87.199.200:8282/user?action=" +
                "get_all_users&" +
                "apikey=" + API_KEY_BASE64);
        Pattern pattern = Pattern.compile("body\\\":\\\"([0-9]*),([\\w\\W]*)\\}");
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        } else {
            return 0;
        }
    }

    public static int getAllOnlineUsers() {
        String response = getResponse("http://80.87.199.200:8282/user?action=" +
                "get_users_online&" +
                "apikey=" + API_KEY_BASE64);
        Pattern pattern = Pattern.compile("body\\\":\\\"([0-9]*),([\\w\\W]*)\\}");
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        } else {
            return 0;
        }
    }

    public static int getActiveKeys() {
        String response = getResponse("http://80.87.199.200:8282/key?action=" +
                "get_active_keys&" +
                "apikey=" + API_KEY_BASE64);
        Pattern pattern = Pattern.compile("body\\\":\\\"([0-9]*),([\\w\\W]*)\\}");
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        } else {
            return 0;
        }
    }

    public static int getAllKeys() {
        String response = getResponse("http://80.87.199.200:8282/key?action=" +
                "get_all_keys&" +
                "apikey=" + API_KEY_BASE64);
        Pattern pattern = Pattern.compile("body\\\":\\\"([0-9]*),([\\w\\W]*)\\}");
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        } else {
            return 0;
        }
    }


    public static <T> T getResponse(String url, Class<T> tClass) {
        return new Gson().fromJson(getResponse(url), tClass);
    }

    public static String getResponse(String url) {
        try {
            HttpURLConnection httpClient = (HttpURLConnection) new URL(url).openConnection();
            httpClient.setRequestMethod("GET");
            httpClient.setReadTimeout(30 * 1000);
            httpClient.setConnectTimeout(30 * 1000);
            httpClient.setRequestProperty("User-Agent", "Mozilla/5.0");
            try (BufferedReader in = new BufferedReader(new InputStreamReader((httpClient.getResponseCode() == 200) ? httpClient.getInputStream() : httpClient.getErrorStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        } catch (IOException e) {
            return "null";
        }
    }
}
