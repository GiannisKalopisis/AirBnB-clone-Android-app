package com.dit.airbnb.csv_dto.custom;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingCSV {

    @CsvBindByName(column = "userId")
    private Long userId;

    @CsvBindByName(column = "apartmentId")
    private Long apartmentId;

    @CsvDate(value = "yyyy-MM-dd")
    @CsvBindByName(column = "checkInDate")
    private Date checkInDate;

    @CsvDate(value = "yyyy-MM-dd")
    @CsvBindByName(column = "checkOutDate")
    private Date checkOutDate;

    @CsvBindByName(column = "isReviewed")
    private String isReviewed;

    @CsvBindByName(column = "numberOfPeople")
    private Short numberOfPeople;

}
