package com.dit.airbnb.request.apartment;

import com.dit.airbnb.dto.enums.RentalType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
public class ApartmentRequest {

    private String country;

    private String city;

    private String district;

    private Date availableStartDate;

    private Date availableEndDate;

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

    @Enumerated
    private RentalType rentalType;

}
