package com.liner.ragebot.utils;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Paths;

public class Files {

    @Nullable
    public static String readFile(@NotNull File file, Charset charset) {
        try {
            StringBuilder stringBuffer = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
            char[] buffer = new char[1024];
            int read;
            while ((read = reader.read(buffer)) != -1) {
                stringBuffer.append(String.valueOf(buffer, 0, read));
            }
            reader.close();
            return stringBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static String readFile(File file) {
        try {
            StringBuilder stringBuffer = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
            char[] buffer = new char[1024];
            int read;
            while ((read = reader.read(buffer)) != -1) {
                stringBuffer.append(String.valueOf(buffer, 0, read));
            }
            reader.close();
            return stringBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void writeFile(File file, String content) {
        if (file.exists())
            if (!file.delete()) {
            }
        try {
            if (!file.createNewFile()) {
            }
            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                fileOutputStream.write(content.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean ensureDirectory(File directory) {
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                return false;
            }
        }
        return true;
    }
}
