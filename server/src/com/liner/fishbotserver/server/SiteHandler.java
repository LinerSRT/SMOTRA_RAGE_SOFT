package com.liner.fishbotserver.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.util.HashMap;

import static com.liner.fishbotserver.utilities.HttpUtils.getQueryCommand;
import static com.liner.fishbotserver.utilities.HttpUtils.getQueryValues;

public class SiteHandler implements HttpHandler {
    private final String endPoint;
    private final Callback callback;
    public SiteHandler(HttpServer server, Callback callback) {
        this.endPoint = "/site";
        server.createContext(endPoint, this);
        this.callback = callback;
    }
    public String getEndPoint() {
        return endPoint;
    }
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        httpExchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        callback.onReceive(httpExchange, httpExchange.getRequestMethod(), getQueryCommand(httpExchange), getQueryValues(httpExchange));
    }
    public interface Callback{
        void onReceive(HttpExchange exchange, String method, String command, HashMap<String, String> queryValues);
    }
}
