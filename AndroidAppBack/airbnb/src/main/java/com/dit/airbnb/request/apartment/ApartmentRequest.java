package com.dit.airbnb.request.apartment;

import com.dit.airbnb.dto.enums.RentalType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApartmentRequest {

    private String amenities;

    private String address;

    private String country;

    private String city;

    private String district;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date availableStartDate;

    @JsonFormat(pattern="yyyy-MM-dd")
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
