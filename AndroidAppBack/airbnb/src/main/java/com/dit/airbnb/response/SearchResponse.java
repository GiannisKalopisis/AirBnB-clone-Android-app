package com.dit.airbnb.response;

import com.dit.airbnb.dto.enums.RentalType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class SearchResponse {

    private Long id;

    // = minRetailPrice + numOfGuests*extraCostPerPerson)
    private BigDecimal totalCost;

    private Double avgRating;

    private String country;

    private String city;

    private String district;

    private String description;
}
