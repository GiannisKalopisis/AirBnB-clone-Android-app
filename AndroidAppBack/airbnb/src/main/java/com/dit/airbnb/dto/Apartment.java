package com.dit.airbnb.dto;


import com.dit.airbnb.csv_dto.ApartmentCSV;
import com.dit.airbnb.dto.enums.RentalType;
import com.dit.airbnb.request.apartment.ApartmentRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "apartment")
public class Apartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "amenities")
    private String amenities;

    @Column(name = "address")
    private String address;

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    @Column(name = "district")
    private String district;

    @JsonFormat(pattern="yyyy-MM-dd")
    @Column(name = "availableStartDate")
    private Date availableStartDate;

    @JsonFormat(pattern="yyyy-MM-dd")
    @Column(name = "availableEndDate")
    private Date availableEndDate;

    @Column(name = "maxVisitors")
    private Integer maxVisitors;

    @Column(name = "minRetailPrice")
    private BigDecimal minRetailPrice;

    @Column(name = "extraCostPerPerson")
    private BigDecimal extraCostPerPerson;

    @Column(name = "description")
    private String description;

    @Column(name = "number_of_beds")
    private Short numberOfBeds;

    @Column(name = "number_of_bedrooms")
    private Short numberOfBedrooms;

    @Column(name = "number_of_bathrooms")
    private Short numberOfBathrooms;

    @Column(name = "number_of_living_rooms")
    private Short numberOfLivingRooms;

    @Column(name = "area")
    private BigDecimal area;

    @Column(name = "geo_lat",  precision = 10, scale = 6)
    private BigDecimal geoLat;

    @Column(name = "geo_long",  precision = 10, scale = 6)
    private BigDecimal geoLong;

    @Column(name = "rules")
    private String rules;

    @Enumerated
    @Column(name = "rental_type")
    private RentalType rentalType;

    // external tables
    @Getter
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_reg_host_id")
    private UserReg userRegHost;

    @Getter
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "apartment", cascade = CascadeType.ALL)
    private Set<Booking> bookings = new HashSet<>();

    @Getter
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "apartment", cascade = CascadeType.ALL)
    private Set<Image> images;

    public Apartment(ApartmentRequest apartmentRequest) {
        this.amenities = apartmentRequest.getAmenities();
        this.address = apartmentRequest.getAddress();
        this.country = apartmentRequest.getCountry();
        this.city = apartmentRequest.getCity();
        this.district = apartmentRequest.getDistrict();
        this.availableStartDate = apartmentRequest.getAvailableStartDate();
        this.availableEndDate = apartmentRequest.getAvailableEndDate();
        this.maxVisitors = apartmentRequest.getMaxVisitors();
        this.minRetailPrice = apartmentRequest.getMinRetailPrice();
        this.extraCostPerPerson = apartmentRequest.getExtraCostPerPerson();
        this.description = apartmentRequest.getDescription();
        this.numberOfBeds = apartmentRequest.getNumberOfBeds();
        this.numberOfBedrooms = apartmentRequest.getNumberOfBedrooms();
        this.numberOfBathrooms = apartmentRequest.getNumberOfBathrooms();
        this.numberOfLivingRooms = apartmentRequest.getNumberOfLivingRooms();
        this.area = apartmentRequest.getArea();
        this.geoLat= apartmentRequest.getGeoLat();
        this.geoLong = apartmentRequest.getGeoLong();
        this.rentalType = apartmentRequest.getRentalType();
        this.rules = apartmentRequest.getRules();
    }

    public Apartment(ApartmentCSV apartmentCSV) {
        this.amenities = apartmentCSV.getAmenities();
        this.country = apartmentCSV.getCountry();
        this.city = apartmentCSV.getCity();
        this.district = apartmentCSV.getDistrict();
        this.availableStartDate = apartmentCSV.getAvailableStartDate();
        this.availableEndDate = apartmentCSV.getAvailableEndDate();
        this.maxVisitors = apartmentCSV.getMaxVisitors();
        this.minRetailPrice = apartmentCSV.getMinRetailPrice();
        this.extraCostPerPerson = apartmentCSV.getExtraCostPerPerson();
        this.description = apartmentCSV.getDescription();
        this.numberOfBeds = apartmentCSV.getNumberOfBeds();
        this.numberOfBedrooms = apartmentCSV.getNumberOfBedrooms();
        this.numberOfBathrooms = apartmentCSV.getNumberOfBathrooms();
        this.numberOfLivingRooms = apartmentCSV.getNumberOfLivingRooms();
        this.area = apartmentCSV.getArea();
        this.geoLat= apartmentCSV.getGeoLat();
        this.geoLong = apartmentCSV.getGeoLong();
        this.rentalType = apartmentCSV.getRentalType().equals("RENTAL_ROOM") ? RentalType.RENTAL_ROOM : RentalType.RENTAL_HOUSE;
        this.address = apartmentCSV.getAddress();
        this.rules = apartmentCSV.getRules();
    }

    public void updateApartment(ApartmentRequest apartmentRequest) {
        if (apartmentRequest.getAmenities() != null) this.amenities = apartmentRequest.getAmenities();
        if (apartmentRequest.getAddress() != null) this.address = apartmentRequest.getAddress();
        if (apartmentRequest.getCountry() != null) this.country = apartmentRequest.getCountry();
        if (apartmentRequest.getCity() != null) this.city = apartmentRequest.getCity();
        if (apartmentRequest.getDistrict() != null) this.district = apartmentRequest.getDistrict();
        if (apartmentRequest.getAvailableStartDate() != null) this.availableStartDate = apartmentRequest.getAvailableStartDate();
        if (apartmentRequest.getAvailableEndDate() != null) this.availableEndDate = apartmentRequest.getAvailableEndDate();
        if (apartmentRequest.getMaxVisitors() != null) this.maxVisitors = apartmentRequest.getMaxVisitors();
        if (apartmentRequest.getMinRetailPrice() != null)  this.minRetailPrice = apartmentRequest.getMinRetailPrice();
        if (apartmentRequest.getExtraCostPerPerson() != null) this.extraCostPerPerson = apartmentRequest.getExtraCostPerPerson();
        if (apartmentRequest.getDescription() != null) this.description = apartmentRequest.getDescription();
        if (apartmentRequest.getNumberOfBeds() != null) this.numberOfBeds = apartmentRequest.getNumberOfBeds();
        if (apartmentRequest.getNumberOfBedrooms() != null) this.numberOfBedrooms = apartmentRequest.getNumberOfBedrooms();
        if (apartmentRequest.getNumberOfBathrooms() != null) this.numberOfBathrooms = apartmentRequest.getNumberOfBathrooms();
        if (apartmentRequest.getNumberOfLivingRooms() != null) this.numberOfLivingRooms = apartmentRequest.getNumberOfLivingRooms();
        if (apartmentRequest.getArea() != null) this.area = apartmentRequest.getArea();
        if (apartmentRequest.getGeoLat() != null) this.geoLat = apartmentRequest.getGeoLat();
        if (apartmentRequest.getGeoLong() != null) this.geoLong = apartmentRequest.getGeoLong();
        if (apartmentRequest.getRules() != null) this.rules = apartmentRequest.getRules();
        if (apartmentRequest.getRentalType() != null) this.rentalType = apartmentRequest.getRentalType();
    }

    public void setUserRegHost(UserReg userRegHost) {
        this.userRegHost = userRegHost;
    }

    public void setBookings(Set<Booking> bookings) {
        this.bookings = bookings;
    }

    public void setImages(Set<Image> images) {
        this.images = images;
    }

}
