package com.liner.keygen;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class MainForm {
    public static final String SERVER_URL = "http://80.87.199.200:8000/key?";
    public static String secretKey = "NDRaT2lmWmhRdVZabmpaL3pDc2ZaL0svSWx0TGRPL1Zua0VtTENINFYyST0=";
    private static JFrame frame;
    private JTextField code1;
    private JTextField code2;
    private JTextField code3;
    private JTextField code4;
    private JButton sendToServer;
    private JLabel statusText;
    private JTextField timeText;
    public JPanel panel;
    private JButton generateButton;
    private JTextField codeCopy;
    private JButton updateTime;
    private JTextField keyEdit;
    private JTextField oldTime;
    private JLabel expiredText;
    private JTextField newTime;
    private JButton sendNewKey;
    private JButton checkKey;
    private JLabel status2;

    public MainForm() {
        code1.setText(randomString(4));
        code2.setText(randomString(4));
        code3.setText(randomString(4));
        code4.setText(randomString(4));
        timeText.setText(new SimpleDateFormat("HH:mm:ss dd.MM.yyyy").format(new Date()));
        generateButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                code1.setText(randomString(4));
                code2.setText(randomString(4));
                code3.setText(randomString(4));
                code4.setText(randomString(4));
                codeCopy.setText(code1.getText() + "-" + code2.getText() + "-" + code3.getText() + "-" + code4.getText());
            }
        });
        updateTime.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                timeText.setText(new SimpleDateFormat("HH:mm:ss dd.MM.yyyy").format(new Date()));
            }
        });
        sendToServer.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    statusText.setText(sendRequest(getGenerateQuery(
                            (code1.getText() + "-" + code2.getText() + "-" + code3.getText() + "-" + code4.getText()),
                            secretKey,
                            timeText.getText(),
                            false
                    )).contains("generated") ? "Ключ сохранен на сервер" : "Шо-то пошло по пизде");
                } catch (IOException e) {
                    e.printStackTrace();
                    statusText.setText("ОшЕбкА11!");
                }
            }
        });




        checkKey.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                long oldTimeL = Long.parseLong(oldTime.getText());
                boolean expired = System.currentTimeMillis() - oldTimeL > 0;
                if(expired){
                    expiredText.setText("Вышел срок");
                    expiredText.setForeground(Color.RED);
                } else {
                    expiredText.setText("Действительный");
                    expiredText.setForeground(Color.BLUE);
                    newTime.setText(new SimpleDateFormat("HH:mm:ss dd.MM.yyyy").format(oldTimeL));
                }
            }
        });
        sendNewKey.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    status2.setText(sendRequest(getGenerateQuery(
                            keyEdit.getText(),
                            secretKey,
                            newTime.getText(),
                            false
                    )).contains("generated") ? "Ключ сохранен на сервер" : "Шо-то пошло по пизде");
                } catch (IOException e) {
                    e.printStackTrace();
                    statusText.setText("ОшЕбкА11!");
                }
            }
        });
    }

    public static void main(String[] args) throws IOException {
        frame = new JFrame("Анальная варежка");
        frame.setContentPane(new MainForm().panel);
        frame.setIconImage(ImageIO.read(new File("var.png")));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(550, 330);
        frame.setVisible(true);
    }


    private static String sendRequest(String url) throws IOException {
        HttpURLConnection httpClient = (HttpURLConnection) new URL(url).openConnection();
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
            return response.toString();
        }
    }

    private String generateURL(String key, String date) {
        return "http://80.87.199.200:8000/key?action=genkey&code=NDRaT2lmWmhRdVZabmpaL3pDc2ZaL0svSWx0TGRPL1Zua0VtTENINFYyST0=&key=" +
                Base64.getEncoder().encodeToString(key.getBytes()) +
                "&expire=" +
                Base64.getEncoder().encodeToString(String.valueOf(date).getBytes())
                ;
    }


    String randomString(int len) {
        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    private static String getGenerateQuery(String key, String skey, String expire, boolean local) {
        Base64.Encoder encoder = Base64.getEncoder();
        return (local ? "http://127.0.0.1:80/key?" : "http://80.87.199.200:8000/key?") +
                "action=generate_key&" +
                "key=" + encoder.encodeToString(key.getBytes()) + "&" +
                "skey=" + skey + "&" +
                "expire=" + encoder.encodeToString(expire.getBytes());
    }
}
