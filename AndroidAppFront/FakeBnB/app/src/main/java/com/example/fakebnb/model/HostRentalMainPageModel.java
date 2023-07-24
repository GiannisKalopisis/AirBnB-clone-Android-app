package com.example.fakebnb.model;

public class HostRentalMainPageModel {

    private String description, area;
    private float rating;

    public HostRentalMainPageModel(String description, String area, float rating) {
        this.description = description;
        this.area = area;
        this.rating = rating;
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
