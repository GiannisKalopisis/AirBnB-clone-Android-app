package com.example.fakebnb.model;

public class RentalMainPageModel {

    private Long rentalId;
    private String description, area, price;
    private float rating;

    public RentalMainPageModel(String description, String area, String price, float rating, Long rentalId) {
        this.description = description;
        this.area = area;
        this.price = price;
        this.rating = rating;
        this.rentalId = rentalId;
    }

    public Long getRentalId() {
        return rentalId;
    }

    public void setRentalId(Long rentalId) {
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

}
