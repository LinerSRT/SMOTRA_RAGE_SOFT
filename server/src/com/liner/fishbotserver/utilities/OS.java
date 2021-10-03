package com.liner.fishbotserver.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class OS {
    public static String execCommand(String command) {
        try {
            StringBuilder result = new StringBuilder();
            Process process = Runtime.getRuntime().exec(command);
            Thread.sleep(100);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            bufferedReader.close();
            return result.toString();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "";
        }
    }
    public static String execWaitCommand(String command) {
        try {
            StringBuilder result = new StringBuilder();
            Process proc = Runtime.getRuntime().exec(command);
            proc.waitFor();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            bufferedReader.close();
            return result.toString();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "";
        }
    }
}
