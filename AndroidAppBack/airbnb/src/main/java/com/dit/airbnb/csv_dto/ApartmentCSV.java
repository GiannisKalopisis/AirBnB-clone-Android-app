package com.dit.airbnb.csv_dto;

import com.dit.airbnb.dto.enums.RentalType;
import com.opencsv.bean.CsvBindByName;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApartmentCSV {

    @CsvBindByName(column = "hostId")
    private Integer hostId;

    @CsvBindByName(column = "country")
    private String country;

    @CsvBindByName(column = "city")
    private String city;

    @CsvBindByName(column = "district")
    private String district;

    @CsvBindByName(column = "availableStartDate")
    private Date availableStartDate;

    @CsvBindByName(column = "availableEndDate")
    private Date availableEndDate;

    @CsvBindByName(column = "maxVisitors")
    private Integer maxVisitors;

    @CsvBindByName(column = "minRetailPrice")
    private BigDecimal minRetailPrice;

    @CsvBindByName(column = "extraCostPerPerson")
    private BigDecimal extraCostPerPerson;

    @CsvBindByName(column = "description")
    private String description;

    @CsvBindByName(column = "numberOfBeds")
    private Short numberOfBeds;

    @CsvBindByName(column = "numberOfBedrooms")
    private Short numberOfBedrooms;

    @CsvBindByName(column = "numberOfBathrooms")
    private Short numberOfBathrooms;

    @CsvBindByName(column = "numberOfLivingRooms")
    private Short numberOfLivingRooms;

    @CsvBindByName(column = "area")
    private BigDecimal area;

    @CsvBindByName(column = "geoLat")
    private BigDecimal geoLat;

    @CsvBindByName(column = "geoLong")
    private BigDecimal geoLong;

    @CsvBindByName(column = "rentalType")
    private String rentalType;

}
