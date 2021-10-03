package com.liner.fishbotserver.funpay;

public class FunPayComment {
    private final String commentPhoto;
    private final String commentTime;
    private final String commentDetails;
    private final String commentText;
    private final int rating;

    public FunPayComment(String commentPhoto, String commentTime, String commentDetails, String commentText, int rating) {
        this.commentPhoto = commentPhoto;
        this.commentTime = commentTime;
        this.commentDetails = commentDetails;
        this.commentText = commentText;
        this.rating = rating;
    }

    public String getCommentPhoto() {
        return commentPhoto;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public String getCommentDetails() {
        return commentDetails;
    }

    public String getCommentText() {
        return commentText;
    }

    public int getRating() {
        return rating;
    }

    @Override
    public String toString() {
        return "FunPayComment{" +
                "commentPhoto='" + commentPhoto + '\'' +
                ", commentTime='" + commentTime + '\'' +
                ", commentDetails='" + commentDetails + '\'' +
                ", commentText='" + commentText + '\'' +
                ", rating=" + rating +
                '}';
    }
}
