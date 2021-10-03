package com.liner.fishbotserver.data;


public class Comment {
    private final String username;
    private final String comment;
    private final long time;
    private final int rating;

    public Comment(String username, String comment, long time, int rating) {
        this.username = username;
        this.comment = comment;
        this.time = time;
        this.rating = rating;
    }

    public String getUsername() {
        return username;
    }

    public String getComment() {
        return comment;
    }

    public long getTime() {
        return time;
    }

    public int getRating() {
        return rating;
    }
}
