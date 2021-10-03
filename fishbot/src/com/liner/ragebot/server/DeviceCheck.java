package com.liner.ragebot.server;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.liner.ragebot.Settings;
import com.liner.ragebot.jna.HardwareInfo;
import com.liner.ragebot.utils.Files;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({"unchecked", "rawtypes"})
public class DeviceCheck {
    private Settings settings;

    public DeviceCheck(JLabel statusText, Settings settings) {
        this.settings = settings;
        if(!settings.isRegisteredOnServer())
            registerDevice(statusText);
    }

    public void registerDevice(JLabel statusText){
       // try {

//
//            if (response.body instanceof ErrorResponse) {
//                loadingStatus.setVisible(true);
//                loadingStatus.setForeground(Color.RED);
//                new Timer().scheduleAtFixedRate(new TimerTask() {
//                    int count = 6;
//                    @Override
//                    public void run() {
//                        count--;
//                        loadingStatus.setText("Не зарегистрирован! Выход через: " + count + " сек.");
//                        if (count < 0) {
//                            System.exit(0);
//                        }
//                    }
//                }, 0, TimeUnit.SECONDS.toMillis(1));
//            } else {
//                settings.setRegisteredOnServer(true);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public Response getKeyResponse(String key) {
        Response result = null;
//        try {
//            BaseResponse<Object> response = new Gson().fromJson(Server.sendRequest(Server.getCheckQuery(
//                    System.getProperty("user.name"),
//                    key,
//                    false
//            )), BaseResponse.class);
//            if (response.body instanceof LinkedTreeMap) {
//                if (((LinkedTreeMap) response.body).containsKey("key")) {
//                    result = new Response(((LinkedTreeMap) response.body));
//                }
//                if(((LinkedTreeMap) response.body).containsKey("cause")){
//                    String cause = (String) ((LinkedTreeMap) response.body).get("cause");
//                    if(cause.equals("Key not exists")){
//                        return new Response();
//                    }
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return result;
    }

    public Response getKeyActivateResponse(String key) {
        Response result = null;
//        try {
//            BaseResponse<Object> response = new Gson().fromJson(Server.sendRequest(Server.getActivateQuery(
//                    System.getProperty("user.name"),
//                    key,
//                    false
//            )), BaseResponse.class);
//            if (response.body instanceof LinkedTreeMap) {
//                if (((LinkedTreeMap) response.body).containsKey("key")) {
//                    result = new Response(((LinkedTreeMap) response.body));
//                }
//                if(((LinkedTreeMap) response.body).containsKey("cause")){
//                    String cause = (String) ((LinkedTreeMap) response.body).get("cause");
//                    if(cause.equals("Key not exists")){
//                        return new Response();
//                    }
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return result;
    }
}
