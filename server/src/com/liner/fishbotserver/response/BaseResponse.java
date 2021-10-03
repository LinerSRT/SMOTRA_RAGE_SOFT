package com.liner.fishbotserver.response;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

public class BaseResponse<T> {
    private final int code;
    private final T body;

    public BaseResponse(int code, T body) {
        this.code = code;
        this.body = body;
    }

    public void send(HttpExchange exchange){
        try {
            Headers headers = exchange.getResponseHeaders();
            headers.add("Content-Type", "application/json");
            String result = new Gson().toJson(this);
            exchange.sendResponseHeaders(code, result.getBytes().length);
            OutputStream stream = exchange.getResponseBody();
            stream.write(result.getBytes());
            stream.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "code=" + code +
                ", body=" + body +
                '}';
    }
}
