package com.example.fakebnb.model;

public class HostRoomModel {

    private String address, startDate, endDate, rentalType, photoUpload, rules, description;
    private int maxVisitors, beds, bedrooms, bathrooms, livingRooms;
    private float minPrice, extraCost, area;

    public HostRoomModel(String address, String startDate, String endDate, String rentalType,
                         String photoUpload, String rules, String description, int maxVisitors,
                         int beds, int bedrooms, int bathrooms, int livingRooms, float minPrice,
                         float extraCost, float area) {
        this.address = address;
        this.startDate = startDate;
        this.endDate = endDate;
        this.rentalType = rentalType;
        this.photoUpload = photoUpload;
        this.rules = rules;
        this.description = description;
        this.maxVisitors = maxVisitors;
        this.beds = beds;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.livingRooms = livingRooms;
        this.minPrice = minPrice;
        this.extraCost = extraCost;
        this.area = area;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getRentalType() {
        return rentalType;
    }

    public void setRentalType(String rentalType) {
        this.rentalType = rentalType;
    }

    public String getPhotoUpload() {
        return photoUpload;
    }

    public void setPhotoUpload(String photoUpload) {
        this.photoUpload = photoUpload;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxVisitors() {
        return maxVisitors;
    }

    public void setMaxVisitors(int maxVisitors) {
        this.maxVisitors = maxVisitors;
    }

    public int getBeds() {
        return beds;
    }

    public void setBeds(int beds) {
        this.beds = beds;
    }

    public int getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(int bedrooms) {
        this.bedrooms = bedrooms;
    }

    public int getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(int bathrooms) {
        this.bathrooms = bathrooms;
    }

    public int getLivingRooms() {
        return livingRooms;
    }

    public void setLivingRooms(int livingRooms) {
        this.livingRooms = livingRooms;
    }

    public float getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(float minPrice) {
        this.minPrice = minPrice;
    }

    public float getExtraCost() {
        return extraCost;
    }

    public void setExtraCost(float extraCost) {
        this.extraCost = extraCost;
    }

    public float getArea() {
        return area;
    }

    public void setArea(float area) {
        this.area = area;
    }
}
