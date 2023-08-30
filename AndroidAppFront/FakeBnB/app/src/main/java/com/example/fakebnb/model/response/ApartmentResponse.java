package com.example.fakebnb.model.response;

import com.example.fakebnb.enums.RentalType;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ApartmentResponse implements Serializable {

    @SerializedName("success")
    private Boolean success;
    @SerializedName("message")
    private String message;
    @SerializedName("object")
    private ApartmentData object;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ApartmentData getObject() {
        return object;
    }

    public void setObject(ApartmentData object) {
        this.object = object;
    }


    public class ApartmentData implements Serializable {

        @SerializedName("amenities")
        private String amenities;

        @SerializedName("address")
        private String address;

        @SerializedName("country")
        private String country;

        @SerializedName("city")
        private String city;

        @SerializedName("district")
        private String district;

        @SerializedName("availableStartDate")
        private Date availableStartDate;

        @SerializedName("availableEndDate")
        private Date availableEndDate;

        @SerializedName("maxVisitors")
        private Integer maxVisitors;

        @SerializedName("minRetailPrice")
        private BigDecimal minRetailPrice;

        @SerializedName("extraCostPerPerson")
        private BigDecimal extraCostPerPerson;

        @SerializedName("description")
        private String description;

        @SerializedName("numberOfBeds")
        private Short numberOfBeds;

        @SerializedName("numberOfBedrooms")
        private Short numberOfBedrooms;

        @SerializedName("numberOfBathrooms")
        private Short numberOfBathrooms;

        @SerializedName("numberOfLivingRooms")
        private Short numberOfLivingRooms;

        @SerializedName("area")
        private BigDecimal area;

        @SerializedName("geoLat")
        private BigDecimal geoLat;

        @SerializedName("geoLong")
        private BigDecimal geoLong;

        @SerializedName("rules")
        private String rules;

        @SerializedName("rentalType")
        private RentalType rentalType;

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

        public Date getAvailableStartDate() {
            return availableStartDate;
        }

        public void setAvailableStartDate(Date availableStartDate) {
            this.availableStartDate = availableStartDate;
        }

        public Date getAvailableEndDate() {
            return availableEndDate;
        }

        public void setAvailableEndDate(Date availableEndDate) {
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
    }
}
