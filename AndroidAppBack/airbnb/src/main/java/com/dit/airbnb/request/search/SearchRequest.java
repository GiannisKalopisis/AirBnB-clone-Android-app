package com.dit.airbnb.request.search;

import com.dit.airbnb.dto.enums.RentalType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SearchRequest {

    private String district;

    private String city;

    private String country;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date availableStartDate;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date availableEndDate;

    @Enumerated
    private RentalType rentalType;

    private Integer numberOfGuests;

}
