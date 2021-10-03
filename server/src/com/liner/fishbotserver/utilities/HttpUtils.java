package com.liner.fishbotserver.utilities;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public class HttpUtils {
    public static void sendResult(int code, String result, HashMap<String, String> headers, HttpExchange exchange) throws IOException {
        if (headers != null) {
            Headers h = exchange.getResponseHeaders();
            for (String key : headers.keySet())
                h.add(key, headers.get(key));
        }
        exchange.sendResponseHeaders(code, result.length());
        OutputStream stream = exchange.getResponseBody();
        stream.write(result.getBytes());
        stream.close();
    }

    public static void sendResult(int code, String result, HttpExchange exchange) throws IOException {
        sendResult(code, result, null, exchange);
    }

    public static String getQueryCommand(HttpExchange exchange) {
        return exchange.getRequestURI().toString().replaceAll("(^\\w+ )|(\\?.*)", "");
    }

    public static HashMap<String, String> getQueryValues(HttpExchange exchange) {
        HashMap<String, String> map = new HashMap<>();
        String input = exchange.getRequestURI().toString();
        for (String pair : input.replaceFirst(".*?\\?", "").split("&"))
            map.put(pair.split("=")[0], pair.split("=")[1]);
        return map;
    }
}
