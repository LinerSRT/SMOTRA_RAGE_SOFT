package com.liner.fishbotserver.utilities;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;

public class Files {

    @Nullable
    public static String readFile(@NotNull File file, Charset charset) {
        try {
            return new String(java.nio.file.Files.readAllBytes(Paths.get(file.getAbsolutePath())), charset);
        } catch (IOException e) {
            return null;
        }
    }

    public static void writeFile(File file, String content) {
        if (file.exists())
            if (!file.delete()) {
                System.out.println("Error! Cant delete original file {" + file.getAbsolutePath() + "}");
            }
        try {
            if (!file.createNewFile()) {
                System.out.println("Error! Cant create file {" + file.getAbsolutePath() + "}");
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
                System.out.println("Error! Cannot create {" + directory.getAbsolutePath() + "}");
                return false;
            }
        }
        return true;
    }
}
