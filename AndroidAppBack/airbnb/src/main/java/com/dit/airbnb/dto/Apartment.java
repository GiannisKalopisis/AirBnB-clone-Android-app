package com.dit.airbnb.dto;


import com.dit.airbnb.dto.enums.RentalType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
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

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    @Column(name = "district")
    private String district;

    @Column(name = "availableStartDate")
    private Date availableStartDate;

    @Column(name = "availableEndDate")
    private Date availableEndDate;

    @Column(name = "maxVisitors")
    private int maxVisitors;

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

    @Enumerated
    @Column(name = "rental_type")
    private RentalType rentalType;

    // external tables
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_reg_host_id")
    private UserReg userRegHost;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "apartment", cascade = CascadeType.ALL)
    private Set<Booking> bookings = new HashSet<>();

}
