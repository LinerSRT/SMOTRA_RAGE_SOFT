package com.liner.ragebot.server.updater;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.liner.ragebot.Core;
import com.liner.ragebot.messages.MessageConfig;
import com.liner.ragebot.messages.MessageForm;
import com.liner.ragebot.messages.MessagePosition;
import com.liner.ragebot.messages.MessageType;
import com.liner.ragebot.utils.Files;
import jdk.nashorn.internal.parser.JSONParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateCheck {
    private static final String SERVER_URL = "http://80.87.199.200";
    private static final String UPDATE_MANIFEST = SERVER_URL + "/updates.json";

    public static void checkUpdates(UpdateCallback updateCallback) {
        Update update = getUpdate();
        boolean needUpdate = update.update > Core.UPDATE;
        if (needUpdate && update.forceUpdate) {
            updateCallback.onForceUpdate(
                    "update " + update.update,
                    SERVER_URL + "/" + update.file,
                    update.changes
            );
        } else if (needUpdate) {
            updateCallback.onUpdate(
                    "update " + update.update,
                    SERVER_URL + "/" + update.file,
                    update.changes
            );
        } else {
            updateCallback.onNoUpdate();
        }
    }

    public interface UpdateCallback {
        void onForceUpdate(String version, String url, String changes);

        void onUpdate(String version, String url, String changes);

        void onNoUpdate();
    }

    private static Update getUpdate() {
        try {
            HttpURLConnection httpClient = (HttpURLConnection) new URL(UPDATE_MANIFEST).openConnection();
            httpClient.setRequestMethod("GET");
            httpClient.setReadTimeout(30 * 1000);
            httpClient.setConnectTimeout(30 * 1000);
            httpClient.setRequestProperty("User-Agent", "Mozilla/5.0");
            try (BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
                Update update = new Update();
                if (jsonObject.has("file")) {
                    update.file = jsonObject.get("file").getAsString();
                    update.update = jsonObject.get("update").getAsInt();
                    update.forceUpdate = jsonObject.get("forceupdate").getAsBoolean();
                    update.changes = jsonObject.get("changes").getAsString();
                }
                return update;
            }
        } catch (IOException e) {
            return new Update();
        }

    }


    public static void forceUpdate(String url, String version) {
        MessageForm messageForm = new MessageForm(
                new MessageConfig.Builder()
                        .setMessageText("Загрузка обновления, подождите")
                        .setMessageTitle("Обновление " + version)
                        .setMessageIcon(Core.Icon.updateIcon)
                        .setMessageType(MessageType.PROGRESS)
                        .setMessagePosition(MessagePosition.TOP_RIGHT)
                        .build()
        );
        messageForm.show();
        messageForm.playSound(Core.Sound.notification);
        new Thread(() -> {
            try {
                HttpURLConnection httpConnection = (HttpURLConnection) (new URL(url).openConnection());
                long completeFileSize = httpConnection.getContentLength();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(httpConnection.getInputStream());
                FileOutputStream fileOutputStream = new FileOutputStream(
                        "update.zip"
                );
                BufferedOutputStream bout = new BufferedOutputStream(
                        fileOutputStream,
                        1024
                );
                byte[] data = new byte[1024];
                long downloadedFileSize = 0;
                int x = 0;
                while ((x = bufferedInputStream.read(data, 0, 1024)) >= 0) {
                    downloadedFileSize += x;
                    final int currentProgress = (int) ((((double) downloadedFileSize) / ((double) completeFileSize)) * 100);
                    bout.write(data, 0, x);
                    messageForm.setProgress(currentProgress);
                }
                messageForm.close();
                bout.close();
                bufferedInputStream.close();
                Files.writeFile(new File(
                        System.getProperty("user.dir"), "updater.bat"
                ), "@echo off\n" +
                        "echo \"Update started...\"\n" +
                        "set currpath=%cd%\n" +
                        "timeout 3 > NUL\n" +
                        "del %currpath%\\*.exe\n" +
                        "powershell.exe -nologo -noprofile -command \"& { $shell = New-Object -COM Shell.Application; $target = $shell.NameSpace('%currpath%'); $zip = $shell.NameSpace('%currpath%\\update.zip'); $target.CopyHere($zip.Items(), 16); Get-ChildItem *.zip | foreach { Remove-Item -Path $_.FullName }; Get-ChildItem *.bat | foreach { Remove-Item -Path $_.FullName }; & %currpath%\\Fishbot.exe}\"\n" +
                        "echo \"Update finished!\"\n" +
                        "start \"\" /B \"" + System.getProperty("user.dir") + "\\Fishbot.exe\"\nexit");
                Runtime.getRuntime().exec(
                        "cmd /c updater.bat", null, new File(System.getProperty("user.dir")));
                System.exit(0);
            } catch (IOException ignored) {
            }
        }).start();
    }

    public static class Update {
        public String file;
        public int update;
        public boolean forceUpdate;
        public String changes;

        public Update(String file, String md5, int update, boolean forceUpdate, String changes) {
            this.file = file;
            this.update = update;
            this.forceUpdate = forceUpdate;
            this.changes = changes;
        }

        public Update() {
            this.file = "none";
            this.update = 0;
            this.forceUpdate = false;
            this.changes = "none";
        }
    }
}
