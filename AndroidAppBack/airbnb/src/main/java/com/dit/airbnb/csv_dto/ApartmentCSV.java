package com.dit.airbnb.csv_dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import jakarta.persistence.Column;
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
    private Long hostId;

    @CsvBindByName(column = "amenities")
    private String amenities;

    @CsvBindByName(column = "address")
    private String address;

    @CsvBindByName(column = "country")
    private String country;

    @CsvBindByName(column = "city")
    private String city;

    @CsvBindByName(column = "district")
    private String district;

    @CsvDate(value = "yyyy-MM-dd")
    @CsvBindByName(column = "availableStartDate")
    private Date availableStartDate;

    @CsvDate(value = "yyyy-MM-dd")
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

    @CsvBindByName(column = "rules")
    private String rules;

    @CsvBindByName(column = "rentalType")
    private String rentalType;

}
