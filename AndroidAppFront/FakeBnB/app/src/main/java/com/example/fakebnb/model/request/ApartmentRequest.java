package com.example.fakebnb.model.request;

import com.example.fakebnb.enums.RentalType;

import java.math.BigDecimal;
import java.util.List;

public class ApartmentRequest {

    private String amenities;
    private String address;
    private String country;
    private String city;
    private String district;
    private String availableStartDate;  // send as String, backend will parse it to Date
    private String availableEndDate;    // send as String, backend will parse it to Date
    private Integer maxVisitors;
    private BigDecimal minRetailPrice;
    private BigDecimal extraCostPerPerson;
    private String description;
    private Short numberOfBeds;
    private Short numberOfBedrooms;
    private Short numberOfBathrooms;
    private Short numberOfLivingRooms;
    private BigDecimal area;
    private BigDecimal geoLat;
    private BigDecimal geoLong;
    private String rules;
    private RentalType rentalType;
    private List<Long> deleteImageIds;

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
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

    public String getAvailableStartDate() {
        return availableStartDate;
    }

    public void setAvailableStartDate(String availableStartDate) {
        this.availableStartDate = availableStartDate;
    }

    public String getAvailableEndDate() {
        return availableEndDate;
    }

    public void setAvailableEndDate(String availableEndDate) {
        this.availableEndDate = availableEndDate;
    }

    public Integer getMaxVisitors() {
        return maxVisitors;
    }

    public void setMaxVisitors(Integer maxVisitors) {
        this.maxVisitors = maxVisitors;
    }

    public BigDecimal getMinRetailPrice() {
        return minRetailPrice;
    }

    public void setMinRetailPrice(BigDecimal minRetailPrice) {
        this.minRetailPrice = minRetailPrice;
    }

    public BigDecimal getExtraCostPerPerson() {
        return extraCostPerPerson;
    }

    public void setExtraCostPerPerson(BigDecimal extraCostPerPerson) {
        this.extraCostPerPerson = extraCostPerPerson;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Short getNumberOfBeds() {
        return numberOfBeds;
    }

    public void setNumberOfBeds(Short numberOfBeds) {
        this.numberOfBeds = numberOfBeds;
    }

    public Short getNumberOfBedrooms() {
        return numberOfBedrooms;
    }

    public void setNumberOfBedrooms(Short numberOfBedrooms) {
        this.numberOfBedrooms = numberOfBedrooms;
    }

    public Short getNumberOfBathrooms() {
        return numberOfBathrooms;
    }

    public void setNumberOfBathrooms(Short numberOfBathrooms) {
        this.numberOfBathrooms = numberOfBathrooms;
    }

    public Short getNumberOfLivingRooms() {
        return numberOfLivingRooms;
    }

    public void setNumberOfLivingRooms(Short numberOfLivingRooms) {
        this.numberOfLivingRooms = numberOfLivingRooms;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public BigDecimal getGeoLat() {
        return geoLat;
    }

    public void setGeoLat(BigDecimal geoLat) {
        this.geoLat = geoLat;
    }

    public BigDecimal getGeoLong() {
        return geoLong;
    }

    public void setGeoLong(BigDecimal geoLong) {
        this.geoLong = geoLong;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public RentalType getRentalType() {
        return rentalType;
    }

    public void setRentalType(RentalType rentalType) {
        this.rentalType = rentalType;
    }

    public List<Long> getDeleteImageIds() {
        return deleteImageIds;
    }

    public void setDeleteImageIds(List<Long> deleteImageIds) {
        this.deleteImageIds = deleteImageIds;
    }
}
