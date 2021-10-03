package com.liner.fishbotserver.server;

import com.google.gson.Gson;
import com.liner.fishbotserver.data.HardwareInfo;
import com.liner.fishbotserver.data.Notification;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

public class Test {

    public static void main(String[] args) {
        String secretKey = "44ZOifZhQuVZnjZ/zCsfZ/K/IltLdO/VnkEmLCH4V2I=";
        Base64.Encoder encoder = Base64.getEncoder();
        try {
            System.out.println(sendRequest(
                    "http://127.0.0.1:80/user?action=" +
                            "register_user&" +
                            "username=" + encoder.encodeToString("LineR".getBytes()) + "&" +
                            "hardware=" + encoder.encodeToString(new Gson().toJson(new HardwareInfo()).getBytes())));

            System.out.println(sendRequest(
                    "http://127.0.0.1:80/user?action=" +
                            "get_user&" +
                            "username=" + encoder.encodeToString("LineR".getBytes())));
            System.out.println(sendRequest(
                    "http://127.0.0.1:80/user?action=" +
                            "get_all_users&" +
                            "apikey=" + encoder.encodeToString(secretKey.getBytes())));
            System.out.println(sendRequest(
                    "http://80.87.199.200:8282/user?action=" +
                            "get_users_online&" +
                            "apikey=" + encoder.encodeToString(secretKey.getBytes())));
            System.out.println(sendRequest(
                    "http://127.0.0.1:80/user?action=" +
                            "send_notification&" +
                            "username=" + encoder.encodeToString("LineR".getBytes()) + "&" +
                            "notification=" + encoder.encodeToString(new Gson().toJson(new Notification(
                                    "test",
                            "Test message",
                            false
                    )).getBytes())));
            System.out.println(sendRequest(
                    "http://127.0.0.1:80/user?action=" +
                            "get_notifications&" +
                            "username=" + encoder.encodeToString("LineR".getBytes())
                    ));
            System.out.println(sendRequest(
                    "http://127.0.0.1:80/user?action=" +
                            "read_notification&" +
                            "username=" + encoder.encodeToString("LineR".getBytes()) + "&" +
                            "notification_id=" + encoder.encodeToString("test".getBytes())
                    ));
            System.out.println(sendRequest(
                    "http://127.0.0.1:80/user?action=" +
                            "remove_notification&" +
                            "username=" + encoder.encodeToString("LineR".getBytes()) + "&" +
                            "notification_id=" + encoder.encodeToString("test".getBytes())
                    ));
            System.out.println(sendRequest(
                    "http://80.87.199.200:8282/key?action=" +
                            "create_key&" +
                            "key=" + encoder.encodeToString("XXXX-XXXX-XXXX-XXX9".getBytes())+"&"+
                            "duration=" + encoder.encodeToString(String.valueOf(TimeUnit.SECONDS.toMillis(30)).getBytes())+"&"+
                            "apikey=" + encoder.encodeToString(secretKey.getBytes())));

            System.out.println(sendRequest(
                    "http://80.87.199.200:8282/key?action=" +
                            "activate_key&"+
                            "key=" + encoder.encodeToString("XXXX-XXXX-XXXX-XXX3".getBytes())+"&"+
                            "hardware=" + encoder.encodeToString(new Gson().toJson(new HardwareInfo()).getBytes())));


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static String sendRequest(String url) throws IOException {
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

    }

}
