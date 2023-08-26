package com.dit.airbnb.response;

import com.dit.airbnb.dto.enums.RentalType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class ApartmentResponse {

    private String address;

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

    private String rules;

    @Enumerated
    private RentalType rentalType;

}