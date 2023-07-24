package com.example.fakebnb.model;

public class HostReviewModel {

    private String username;
    private float stars;
    private String comment;

    public HostReviewModel(String username, float stars, String comment) {
        this.username = username;
        this.stars = stars;
        this.comment = comment;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public float getStars() {
        return stars;
    }

    public void setStars(float stars) {
        this.stars = stars;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
