package com.example.fakebnb.model;


public class RentalModel {

    private Long id;

    private String address;

    private String country;

    private String city;

    private String district;

    private String description;

    private Double avgApartmentRating;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getAvgApartmentRating() {
        return avgApartmentRating;
    }

    public void setAvgApartmentRating(Double avgApartmentRating) {
        this.avgApartmentRating = avgApartmentRating;
    }
}
