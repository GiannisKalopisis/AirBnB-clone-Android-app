package com.dit.airbnb.csv_dto.recommendation;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApartmentRecCSV {

    @CsvBindByName(column = "id")
    private Long apartmentId;

    @CsvBindByName(column = "host_id")
    private Long hostId;

    @CsvBindByName(column = "host_name")
    private String hostName;

    @CsvBindByName(column = "description")
    private String description;

    @CsvBindByName(column = "accommodates")
    private Integer maxVisitors;

    @CsvBindByName(column = "bathrooms")
    private Double numberOfBathrooms;

    @CsvBindByName(column = "bedrooms")
    private Short numberOfBedrooms;

    @CsvBindByName(column = "beds")
    private Short numberOfBeds;

    @CsvBindByName(column = "amenities")
    private String amenities;

    @CsvBindByName(column = "square_feet")
    private Integer squareFeet;

    // contains dollar sign
    @CsvBindByName(column = "price")
    private String price;

    // contains dollar sign
    @CsvBindByName(column = "extra_people")
    private String extraPeoplePrice;

    @CsvBindByName(column = "latitude")
    private BigDecimal geoLat;

    @CsvBindByName(column = "longitude")
    private BigDecimal geoLong;
}
