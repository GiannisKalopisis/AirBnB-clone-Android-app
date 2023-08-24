package com.example.fakebnb.model;

public class HostRentalMainPageModel {
    private long rentalId;
    private String description, area;
    private float rating;

    public HostRentalMainPageModel(String description, String area, float rating, long rentalId) {
        this.description = description;
        this.area = area;
        this.rating = rating;
        this.rentalId = rentalId;
    }

    public long getRentalId() {
        return rentalId;
    }

    public void setRentalId(long rentalId) {
        this.rentalId = rentalId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
