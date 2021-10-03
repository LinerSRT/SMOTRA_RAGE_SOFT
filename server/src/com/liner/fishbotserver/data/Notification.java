package com.liner.fishbotserver.data;

public class Notification {
    private final String id;
    private String message;
    private boolean isRead;

    public Notification(String id, String message, boolean isRead) {
        this.id = id;
        this.message = message;
        this.isRead = isRead;
    }

    public String getId() {
        return id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public boolean isRead() {
        return isRead;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "message='" + message + '\'' +
                ", isRead=" + isRead +
                '}';
    }
}
